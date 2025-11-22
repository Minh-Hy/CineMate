package com.yourname.cinemate.data.remote;

import com.yourname.cinemate.MyApplication;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String BASE_URL = "https://cinemate-backend-6mju.onrender.com/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            // 1. Tạo Logging Interceptor (để debug)
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            // 2. Tạo Auth Interceptor (để thêm token)
            // Lấy sessionManager từ lớp MyApplication đã khởi tạo.
            AuthInterceptor authInterceptor = new AuthInterceptor(MyApplication.getSessionManager());

            // 3. Xây dựng OkHttpClient
            OkHttpClient client = new OkHttpClient.Builder()
                    // Thêm AuthInterceptor vào TRƯỚC.
                    .addInterceptor(authInterceptor)
                    // Thêm LoggingInterceptor vào SAU.
                    // Thứ tự này giúp chúng ta thấy được request cuối cùng đã có header Authorization.
                    .addInterceptor(loggingInterceptor)
                    .cache(null)
                    .authenticator(new TokenAuthenticator())
                    .build();

            // 4. Xây dựng Retrofit với OkHttpClient đã được cấu hình
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static ApiService getApiService() {
        return getClient().create(ApiService.class);
    }
}