package com.yourname.cinemate.data.remote;

import androidx.annotation.NonNull;
import com.yourname.cinemate.utils.SessionManager;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    private final SessionManager sessionManager;

    public AuthInterceptor(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        // Lấy request gốc từ chain.
        Request originalRequest = chain.request();

        // Lấy token từ SessionManager.
        String token = sessionManager.getAuthToken();

        // Nếu có token, chúng ta sẽ thêm Header vào request.
        if (token != null) {
            Request.Builder builder = originalRequest.newBuilder()
                    .header("Authorization", "Bearer " + token);

            Request newRequest = builder.build();
            // Gửi request mới đã có header đi và trả về response của nó.
            return chain.proceed(newRequest);
        }

        // Nếu không có token, gửi request gốc đi mà không thay đổi gì.
        return chain.proceed(originalRequest);
    }
}