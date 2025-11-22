package com.yourname.cinemate.data.remote;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.yourname.cinemate.MyApplication;
import com.yourname.cinemate.data.model.LoginResponse;
import com.yourname.cinemate.data.model.RefreshTokenDto;
import com.yourname.cinemate.utils.SessionManager;
import java.io.IOException;
import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import retrofit2.Call;

public class TokenAuthenticator implements Authenticator {

    @Nullable
    @Override
    public Request authenticate(@Nullable Route route, @NonNull Response response) throws IOException {
        SessionManager sessionManager = MyApplication.getSessionManager();
        String refreshToken = sessionManager.getRefreshToken();

        // Nếu không có refresh token, không thể làm gì, trả về null để request thất bại
        if (refreshToken == null) {
            return null;
        }

        // --- Thực hiện gọi API /auth/refresh một cách ĐỒNG BỘ ---
        // Chúng ta phải gọi đồng bộ vì Authenticator chạy trên background thread của OkHttp
        ApiService apiService = RetrofitClient.getApiService();
        Call<LoginResponse> call = apiService.refreshToken(new RefreshTokenDto(refreshToken));

        try {
            retrofit2.Response<LoginResponse> refreshResponse = call.execute();

            if (refreshResponse.isSuccessful() && refreshResponse.body() != null) {
                LoginResponse newTokens = refreshResponse.body();
                // Lưu cặp token mới
                sessionManager.saveTokens(newTokens.getAccessToken(), newTokens.getRefreshToken());

                // Tạo lại request cũ với access token mới và thử lại
                return response.request().newBuilder()
                        .header("Authorization", "Bearer " + newTokens.getAccessToken())
                        .build();
            } else {
                // Nếu refresh token cũng thất bại, logout người dùng
                // TODO: Gửi một broadcast hoặc event để thông báo cho app cần logout
                sessionManager.clearTokens();
                return null;
            }
        } catch (IOException e) {
            return null;
        }
    }
}