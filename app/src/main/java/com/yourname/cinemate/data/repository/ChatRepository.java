package com.yourname.cinemate.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.yourname.cinemate.MyApplication;
import com.yourname.cinemate.data.model.Attachment;
import com.yourname.cinemate.data.model.ChatMessage;
import com.yourname.cinemate.data.model.ConversationResponse;
import com.yourname.cinemate.data.remote.ApiService;
import com.yourname.cinemate.data.remote.RetrofitClient;
import com.yourname.cinemate.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatRepository {

    private static final String TAG = "ChatRepository";
    private static final String BACKEND_URL = "https://cinemate-backend-6mju.onrender.com";

    private final ApiService apiService;
    private final SessionManager sessionManager;
    private Socket mSocket;

    private final Gson gson = new Gson();

    // LiveData tin nhắn mới (từ socket) gửi lên ViewModel
    private final MutableLiveData<ChatMessage> _incomingMessage = new MutableLiveData<>();
    public LiveData<ChatMessage> getIncomingMessage() {
        return _incomingMessage;
    }

    private String currentConversationId = null;

    public ChatRepository() {
        this.apiService = RetrofitClient.getApiService();
        this.sessionManager = MyApplication.getSessionManager();
    }

    /**
     * 1. Gọi API lấy lịch sử chat + conversationId
     */
    public LiveData<List<ChatMessage>> getConversationHistory() {
        final MutableLiveData<List<ChatMessage>> data = new MutableLiveData<>();

        apiService.getUserConversation().enqueue(new Callback<ConversationResponse>() {
            @Override
            public void onResponse(Call<ConversationResponse> call, Response<ConversationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Lưu lại conversationId
                    currentConversationId = response.body().getId();
                    Log.d(TAG, "Conversation ID loaded: " + currentConversationId);

                    // Trả về list messages
                    List<ChatMessage> messages = response.body().getMessages();
                    data.setValue(messages != null ? messages : new ArrayList<>());

                    // Kết nối socket sau khi đã có conversationId
                    connectSocket();
                } else {
                    Log.e(TAG, "getConversationHistory: response not successful");
                    data.setValue(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<ConversationResponse> call, Throwable t) {
                Log.e(TAG, "Failed to get conversation", t);
                data.setValue(null); // ViewModel sẽ xử lý null -> hiện lỗi / list rỗng
            }
        });

        return data;
    }

    /**
     * 2. Gửi tin nhắn (text + attachments) qua socket
     * Backend yêu cầu: conversationId, content, attachments
     */
    public void sendMessage(String content, List<Attachment> attachments) {
        if (mSocket == null || !mSocket.connected() || currentConversationId == null) {
            Log.e(TAG, "Cannot send: Socket not connected or ConversationId null");
            return;
        }

        JSONObject payload = new JSONObject();
        try {
            payload.put("conversationId", currentConversationId);
            payload.put("content", content == null ? "" : content);

            // Attachments nếu có
            if (attachments != null && !attachments.isEmpty()) {
                JSONArray attachArray = new JSONArray();
                for (Attachment att : attachments) {
                    if (att == null) continue;
                    JSONObject attObj = new JSONObject();
                    attObj.put("url", att.getUrl());
                    // Nếu model có fileName thì dùng, không thì để default
                    attObj.put("fileName", att.getFileName() != null ? att.getFileName() : "file");
                    attObj.put("type", att.getType());
                    attachArray.put(attObj);
                }
                payload.put("attachments", attachArray);
            }

            mSocket.emit("sendMessage", payload);
            Log.d(TAG, "sendMessage: " + payload);

        } catch (JSONException e) {
            Log.e(TAG, "sendMessage: JSON error", e);
        }
    }

    /**
     * 2a. Hàm tiện ích: chỉ gửi content (không kèm attachments)
     * -> gọi sang hàm chính cho đồng bộ
     */
    public void sendMessage(String content) {
        sendMessage(content, null);
    }

    /**
     * 3. Upload file chat
     */
    public LiveData<Attachment> uploadFile(File file) {
        final MutableLiveData<Attachment> data = new MutableLiveData<>();

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        apiService.uploadChatFile(body).enqueue(new Callback<Attachment>() {
            @Override
            public void onResponse(Call<Attachment> call, Response<Attachment> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Upload file success");
                    data.setValue(response.body());
                } else {
                    Log.e(TAG, "Upload file failed: " + response.code());
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<Attachment> call, Throwable t) {
                Log.e(TAG, "Upload file failed", t);
                data.setValue(null);
            }
        });

        return data;
    }

    /**
     * 4. Kết nối Socket.IO
     */
    public void connectSocket() {
        // Nếu đã có socket và đang connect thì bỏ qua
        if (mSocket != null && mSocket.connected()) {
            return;
        }

        try {
            String token = sessionManager.getAuthToken();
            if (token == null) {
                Log.e(TAG, "connectSocket: token is null");
                return;
            }

            IO.Options options = IO.Options.builder()
                    .setAuth(Collections.singletonMap("token", token))
                    .build();

            if (mSocket == null) {
                mSocket = IO.socket(BACKEND_URL, options);
            }

            // Đảm bảo chỉ có 1 listener duy nhất cho "newMessage"
            mSocket.off("newMessage");
            mSocket.on("newMessage", onNewMessage);

            mSocket.connect();

        } catch (URISyntaxException e) {
            Log.e(TAG, "Socket URI Error", e);
        }
    }

    /**
     * 5. Ngắt kết nối socket
     */
    public void disconnectSocket() {
        if (mSocket != null) {
            mSocket.off("newMessage", onNewMessage);
            mSocket.disconnect();
            mSocket = null;
        }
    }

    /**
     * Listener nhận tin nhắn mới từ server
     */
    private final Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            try {
                if (args.length == 0 || !(args[0] instanceof JSONObject)) {
                    Log.e(TAG, "onNewMessage: invalid args");
                    return;
                }

                JSONObject data = (JSONObject) args[0];

                // Parse JSON -> ChatMessage bằng Gson
                ChatMessage parsedMessage = gson.fromJson(data.toString(), ChatMessage.class);

                _incomingMessage.postValue(parsedMessage);

            } catch (Exception e) {
                Log.e(TAG, "Error parsing new message", e);
            }
        }
    };
}
