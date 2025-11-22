package com.yourname.cinemate.data.repository;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.yourname.cinemate.MyApplication;
import com.yourname.cinemate.data.model.ChatMessage;
import com.yourname.cinemate.data.model.ConversationResponse;
import com.yourname.cinemate.data.remote.ApiService;
import com.yourname.cinemate.data.remote.RetrofitClient;
import com.yourname.cinemate.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatRepository {
    private static final String TAG = "ChatRepository";
    private static final String BACKEND_URL = "https://cinemate-backend-6mju.onrender.com"; // URL backend của bạn

    private final ApiService apiService;
    private final SessionManager sessionManager;
    private Socket mSocket;

    // LiveData để gửi tin nhắn mới nhận được từ Socket về ViewModel
    private final MutableLiveData<ChatMessage> _incomingMessage = new MutableLiveData<>();
    public LiveData<ChatMessage> getIncomingMessage() { return _incomingMessage; }

    public ChatRepository() {
        this.apiService = RetrofitClient.getApiService();
        this.sessionManager = MyApplication.getSessionManager();
    }

    // 1. Gọi API lấy lịch sử chat
    public LiveData<List<ChatMessage>> getConversationHistory() {
        final MutableLiveData<List<ChatMessage>> data = new MutableLiveData<>();

        apiService.getConversation().enqueue(new Callback<ConversationResponse>() {
            @Override
            public void onResponse(Call<ConversationResponse> call, Response<ConversationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // API trả về danh sách tin nhắn
                    data.setValue(response.body().getMessages());

                    // Sau khi lấy lịch sử thành công, ta kết nối Socket
                    connectSocket();
                } else {
                    data.setValue(new ArrayList<>()); // Trả về rỗng nếu lỗi hoặc chưa có chat
                    // Vẫn thử kết nối socket để chat mới
                    connectSocket();
                }
            }

            @Override
            public void onFailure(Call<ConversationResponse> call, Throwable t) {
                Log.e(TAG, "Failed to get conversation", t);
                data.setValue(null);
            }
        });
        return data;
    }

    // 2. Kết nối Socket.IO
    public void connectSocket() {
        // Nếu socket đã tồn tại và đang kết nối thì không làm gì cả
        if (mSocket != null && mSocket.connected()) {
            return;
        }

        try {
            String token = sessionManager.getAuthToken();
            if (token == null) return;

            IO.Options options = IO.Options.builder()
                    .setAuth(Collections.singletonMap("token", token))
                    .build();

            // Nếu mSocket chưa được khởi tạo thì tạo mới
            if (mSocket == null) {
                mSocket = IO.socket(BACKEND_URL, options);
            }

            // --- KHU VỰC QUAN TRỌNG NHẤT ---

            // BƯỚC 1: Xóa TẤT CẢ các listener cũ của sự kiện "newMessage"
            // Điều này đảm bảo không bao giờ có 2 listener cùng chạy
            mSocket.off("newMessage");

            // BƯỚC 2: Đăng ký listener mới
            mSocket.on("newMessage", onNewMessage);

            // -------------------------------

            mSocket.connect();

        } catch (URISyntaxException e) {
            Log.e(TAG, "Socket URI Error", e);
        }
    }

    // 3. Ngắt kết nối Socket
    public void disconnectSocket() {
        if (mSocket != null) {
            mSocket.disconnect();
            mSocket.off("newMessage", onNewMessage);
            mSocket = null;
        }
    }

    // 4. Gửi tin nhắn qua Socket
    public void sendMessage(String content) {
        if (mSocket != null && mSocket.connected()) {
            JSONObject payload = new JSONObject();
            try {
                payload.put("content", content);
                mSocket.emit("sendMessage", payload);
                // Lưu ý: Theo mô tả backend, server sẽ emit lại 'newMessage'
                // nên ta không cần add thủ công vào list ở đây,
                // mà chờ sự kiện 'newMessage' trả về để hiển thị.
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    // Xử lý sự kiện nhận tin nhắn mới
    private final Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            try {
                JSONObject data = (JSONObject) args[0];
                // Parse JSON thành đối tượng ChatMessage thủ công hoặc dùng GSON
                // Ở đây tôi demo parse thủ công các trường quan trọng
                ChatMessage message = new ChatMessage();
                // Lưu ý: Bạn cần đảm bảo các trường này khớp với JSON server trả về trong sự kiện socket
                message.setSenderType(data.getString("senderType"));

                // Backend trả về object đầy đủ, ta có thể dùng GSON để parse cho nhanh
                // Tuy nhiên để đơn giản trong luồng background thread này:
                // (Cần chuyển về UI thread nếu update LiveData, postValue làm điều đó tự động)

                // Cách tốt nhất: Dùng Gson parse chuỗi JSON string
                com.google.gson.Gson gson = new com.google.gson.Gson();
                ChatMessage parsedMessage = gson.fromJson(data.toString(), ChatMessage.class);

                _incomingMessage.postValue(parsedMessage);

            } catch (Exception e) {
                Log.e(TAG, "Error parsing new message", e);
            }
        }
    };
}