package com.yourname.cinemate.data.repository;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.yourname.cinemate.data.model.NotificationItem;
import com.yourname.cinemate.data.model.NotificationResponse;
import com.yourname.cinemate.data.remote.ApiService;
import com.yourname.cinemate.data.remote.RetrofitClient;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationRepository {
    private final ApiService apiService;

    public NotificationRepository() {
        this.apiService = RetrofitClient.getApiService();
    }

    // Lấy danh sách thông báo
    public LiveData<List<NotificationItem>> getNotifications(int page, int limit) {
        final MutableLiveData<List<NotificationItem>> data = new MutableLiveData<>();

        apiService.getNotifications(page, limit).enqueue(new Callback<NotificationResponse>() {
            @Override
            public void onResponse(Call<NotificationResponse> call, Response<NotificationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(response.body().getItems());
                } else {
                    data.setValue(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<NotificationResponse> call, Throwable t) {
                Log.e("NotifRepo", "Get notifications failed", t);
                data.setValue(null);
            }
        });
        return data;
    }

    // Đánh dấu 1 thông báo đã đọc
    public void markAsRead(String notificationId) {
        apiService.markNotificationRead(notificationId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("NotifRepo", "Marked as read: " + notificationId);
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("NotifRepo", "Failed to mark as read", t);
            }
        });
    }
}