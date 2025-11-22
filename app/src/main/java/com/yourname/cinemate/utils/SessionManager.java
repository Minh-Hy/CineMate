package com.yourname.cinemate.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String SHARED_PREF_NAME = "cinemate_session";
    private static final String KEY_AUTH_TOKEN = "auth_token";

    private final SharedPreferences sharedPreferences;
    private static final String KEY_REFRESH_TOKEN = "refresh_token";

    // Constructor nhận vào một Context để có thể truy cập SharedPreferences.
    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Lưu trữ Access Token vào SharedPreferences.
     * @param token Access Token nhận được từ API login.
     */
    public void saveAuthToken(String token) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_AUTH_TOKEN, token);
        editor.apply(); // apply() lưu trữ bất đồng bộ, không chặn luồng chính.
    }

    /**
     * Lấy Access Token đã được lưu trữ.
     * @return Access Token, hoặc null nếu chưa có token nào được lưu.
     */
    public String getAuthToken() {
        return sharedPreferences.getString(KEY_AUTH_TOKEN, null);
    }

    /**
     * Xóa Access Token (dùng cho chức năng Đăng xuất).
     */
    public void clearTokens() { // Đổi tên từ clearAuthToken
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_AUTH_TOKEN);
        editor.remove(KEY_REFRESH_TOKEN);
        editor.apply();
    }
    public void saveTokens(String accessToken, String refreshToken) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_AUTH_TOKEN, accessToken);
        editor.putString(KEY_REFRESH_TOKEN, refreshToken);
        editor.apply();
    }

    public String getRefreshToken() {
        return sharedPreferences.getString(KEY_REFRESH_TOKEN, null);
    }
}