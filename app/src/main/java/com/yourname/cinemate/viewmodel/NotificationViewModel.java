package com.yourname.cinemate.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.yourname.cinemate.data.model.NotificationItem;
import com.yourname.cinemate.data.repository.NotificationRepository;
import java.util.List;

public class NotificationViewModel extends ViewModel {

    private final NotificationRepository repository;

    private final MutableLiveData<List<NotificationItem>> _notifications = new MutableLiveData<>();
    public LiveData<List<NotificationItem>> getNotifications() { return _notifications; }

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>();
    public LiveData<Boolean> isLoading() { return _isLoading; }

    public NotificationViewModel() {
        repository = new NotificationRepository();
        loadNotifications();
    }

    public void loadNotifications() {
        _isLoading.setValue(true);
        // Tạm thời lấy trang 1, 50 item mới nhất
        repository.getNotifications(1, 50).observeForever(items -> {
            _isLoading.setValue(false);
            if (items != null) {
                _notifications.setValue(items);
            }
        });
    }

    public void markRead(String notificationId) {
        repository.markAsRead(notificationId);
        // Cập nhật lại UI local ngay lập tức để người dùng thấy phản hồi nhanh
        List<NotificationItem> currentList = _notifications.getValue();
        if (currentList != null) {
            for (NotificationItem item : currentList) {
                if (item.getId().equals(notificationId)) {
                    item.setRead(true); // Cần có setter setRead trong Model
                    break;
                }
            }
            _notifications.setValue(currentList);
        }
    }
}