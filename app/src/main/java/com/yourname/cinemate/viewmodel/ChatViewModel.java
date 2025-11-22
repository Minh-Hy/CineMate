package com.yourname.cinemate.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.yourname.cinemate.data.model.ChatMessage;
import com.yourname.cinemate.data.repository.ChatRepository;
import com.yourname.cinemate.utils.Event;
import java.util.ArrayList;
import java.util.List;

public class ChatViewModel extends ViewModel {

    private final ChatRepository chatRepository;

    // Danh sách tin nhắn hiển thị trên UI
    private final MutableLiveData<List<ChatMessage>> _messages = new MutableLiveData<>();
    public LiveData<List<ChatMessage>> getMessages() { return _messages; }

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>();
    public LiveData<Boolean> isLoading() { return _isLoading; }

    public ChatViewModel() {
        this.chatRepository = new ChatRepository();
    }

    // Gọi khi màn hình Chat mở ra
    public void loadChatHistory() {
        _isLoading.setValue(true);
        // Lấy lịch sử REST API
        chatRepository.getConversationHistory().observeForever(historyMessages -> {
            _isLoading.setValue(false);
            if (historyMessages != null) {
                _messages.setValue(historyMessages);
            } else {
                // Có thể là cuộc hội thoại mới
                _messages.setValue(new ArrayList<>());
            }
        });
    }

    // Lấy LiveData tin nhắn realtime từ Repository
    public LiveData<ChatMessage> getIncomingMessage() {
        return chatRepository.getIncomingMessage();
    }

    // Thêm tin nhắn mới vào danh sách hiện tại
    public void addNewMessageToList(ChatMessage message) {
        List<ChatMessage> currentList = _messages.getValue();
        if (currentList == null) currentList = new ArrayList<>();
        currentList.add(message);
        _messages.setValue(currentList);
    }

    public void sendMessage(String content) {
        chatRepository.sendMessage(content);
        // Không cần add vào list ngay, chờ socket trả về sự kiện 'newMessage'
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        chatRepository.disconnectSocket();
    }
}