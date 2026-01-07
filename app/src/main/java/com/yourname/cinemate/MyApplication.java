package com.yourname.cinemate;

import static android.content.ContentValues.TAG;

import android.app.Application;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.yourname.cinemate.data.repository.UserRepository;
import com.yourname.cinemate.utils.SessionManager;

public class MyApplication extends Application {
    private static SessionManager sessionManager;
    private static MyApplication instance;

    public static MyApplication getInstance() {
        return instance;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        // Khởi tạo SessionManager một lần duy nhất khi ứng dụng bắt đầu.
        sessionManager = new SessionManager(getApplicationContext());
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Lấy token FCM mới
                    String token = task.getResult();

                    // Gửi lên server (nếu người dùng đã đăng nhập)
                    if (MyApplication.getSessionManager().getAuthToken() != null) {
                        new UserRepository().registerDeviceToken(token);
                    }
                });
    }

    public static SessionManager getSessionManager() {
        return sessionManager;
    }
}
