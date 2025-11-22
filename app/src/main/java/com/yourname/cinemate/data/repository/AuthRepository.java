package com.yourname.cinemate.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.yourname.cinemate.data.model.ForgotPasswordDto;
import com.yourname.cinemate.data.model.GoogleTokenDto;
import com.yourname.cinemate.data.model.LoginDto;
import com.yourname.cinemate.data.model.LoginResponse;
import com.yourname.cinemate.data.model.RegisterDto;
import com.yourname.cinemate.data.model.RegisterResponse;
import com.yourname.cinemate.data.model.ResetPasswordDto;
import com.yourname.cinemate.data.remote.ApiService;
import com.yourname.cinemate.data.remote.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {

    private final ApiService apiService;

    public AuthRepository() {
        this.apiService = RetrofitClient.getApiService();
    }

    /**
     * Thực hiện gọi API đăng nhập.
     * @param email Email người dùng nhập.
     * @param password Mật khẩu người dùng nhập.
     * @return LiveData chứa LoginResponse nếu thành công, hoặc null nếu thất bại.
     */
    public LiveData<LoginResponse> loginUser(String email, String password) {
        final MutableLiveData<LoginResponse> data = new MutableLiveData<>();
        LoginDto loginDto = new LoginDto(email, password);

        apiService.loginUser(loginDto).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }

    /**
     * Thực hiện gọi API đăng ký.
     * @param username Tên người dùng.
     * @param email Email.
     * @param password Mật khẩu.
     * @return LiveData chứa RegisterResponse nếu thành công, hoặc null nếu thất bại.
     */
    public LiveData<RegisterResponse> registerUser(String username, String email, String password) {
        final MutableLiveData<RegisterResponse> data = new MutableLiveData<>();
        RegisterDto registerDto = new RegisterDto(username, email, password);

        apiService.registerUser(registerDto).enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    // TODO: Xử lý lỗi chi tiết hơn, ví dụ đọc body lỗi từ server
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }

    public LiveData<LoginResponse> googleSignIn(String idToken) {
        final MutableLiveData<LoginResponse> data = new MutableLiveData<>();
        apiService.googleSignIn(new GoogleTokenDto(idToken)).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                data.setValue(response.isSuccessful() ? response.body() : null);
            }
            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e("AuthRepository", "API call failed for googleSignIn", t);
                data.setValue(null);
            }
        });
        return data;
    }
    // Trong Reset Password
    public LiveData<Boolean> requestPasswordReset(String email) {
        final MutableLiveData<Boolean> success = new MutableLiveData<>();
        apiService.requestPasswordReset(new ForgotPasswordDto(email)).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                success.setValue(response.isSuccessful());
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                success.setValue(false);
            }
        });
        return success;
    }

    public LiveData<Boolean> resetPassword(String token, String newPassword) {
        final MutableLiveData<Boolean> success = new MutableLiveData<>();
        apiService.resetPassword(new ResetPasswordDto(token, newPassword)).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                success.setValue(response.isSuccessful());
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                success.setValue(false);
            }
        });
        return success;
    }
}