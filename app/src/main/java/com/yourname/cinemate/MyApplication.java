package com.yourname.cinemate;

import android.app.Application;
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
    }

    public static SessionManager getSessionManager() {
        return sessionManager;
    }
}
