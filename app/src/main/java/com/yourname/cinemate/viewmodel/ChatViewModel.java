package com.yourname.cinemate.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.yourname.cinemate.data.model.Attachment;
import com.yourname.cinemate.data.model.ChatMessage;
import com.yourname.cinemate.data.repository.ChatRepository;
import com.yourname.cinemate.utils.Event;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ChatViewModel extends ViewModel {

    private final ChatRepository chatRepository;

    private final MutableLiveData<List<ChatMessage>> _messages = new MutableLiveData<>();
    public LiveData<List<ChatMessage>> getMessages() { return _messages; }

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>();
    public LiveData<Boolean> isLoading() { return _isLoading; }
    private final MutableLiveData<Event<String>> _error = new MutableLiveData<>();
    public LiveData<Event<String>> getError() { return _error; }

    public ChatViewModel() {
        this.chatRepository = new ChatRepository();

        // 1. Theo dõi tin nhắn Real-time TẠI ĐÂY (Nội bộ ViewModel)
        // Khi có tin nhắn mới, nó sẽ tự động cập nhật _messages LiveData
        chatRepository.getIncomingMessage().observeForever(this::handleNewMessage);
    }

    public void loadChatHistory() {
        _isLoading.setValue(true);
        // Lấy lịch sử REST API
        chatRepository.getConversationHistory().observeForever(historyMessages -> {
            _isLoading.setValue(false);
            if (historyMessages != null) {
                // SỬA: Sắp xếp tin nhắn theo timestamp (giả định có trường timestamp)
                // Collections.sort(historyMessages, Comparator.comparingLong(ChatMessage::getTimestamp));
                _messages.setValue(historyMessages);
            } else {
                _messages.setValue(new ArrayList<>());
            }
        });
    }

    // [LOẠI BỎ] public LiveData<ChatMessage> getIncomingMessage()
    // [LOẠI BỎ] public void addNewMessageToList(ChatMessage message)

    // Phương thức xử lý tin nhắn mới (Cả tin nhắn nhận và tin nhắn tự gửi đã được xác nhận)
    private void handleNewMessage(ChatMessage message) {
        List<ChatMessage> currentList = _messages.getValue();
        if (currentList == null) currentList = new ArrayList<>();

        // 2. Chống trùng lặp (Kiểm tra ID nếu tin nhắn đã tồn tại)
        // Đây là bước quan trọng để tránh trùng lặp khi listener bắn lại toàn bộ dữ liệu.
        if (message.getId() != null) {
            boolean alreadyExists = currentList.stream()
                    .anyMatch(m -> message.getId().equals(m.getId()));

            if (alreadyExists) {
                return;
            }
        }

        // 3. Thêm vào danh sách và CẬP NHẬT LiveData
        currentList.add(message);
        // SỬA: Sắp xếp lại danh sách sau khi thêm (rất quan trọng)
        // Collections.sort(currentList, Comparator.comparingLong(ChatMessage::getTimestamp));

        // Thiết lập giá trị mới. Điều này sẽ kích hoạt getMessages().observe() trong Fragment
        _messages.setValue(currentList);
    }

    public void sendMessage(String content) {
        // Gửi tin nhắn qua Repository. Khi Repository nhận được ACK/tin nhắn được gửi
        // nó sẽ bắn tin nhắn đó qua luồng Real-time (được theo dõi bởi handleNewMessage)
        chatRepository.sendMessage(content, null);
    }

    public void sendImage(File imageFile) {
        _isLoading.setValue(true);
        chatRepository.uploadFile(imageFile).observeForever(attachment -> {
            _isLoading.setValue(false);
            if (attachment != null) {
                List<Attachment> attList = new ArrayList<>();
                attList.add(attachment);
                chatRepository.sendMessage("", attList);
            } else {
                _error.setValue(new Event<>("Lỗi upload ảnh"));
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        chatRepository.disconnectSocket();
    }
}