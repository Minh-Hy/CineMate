package com.yourname.cinemate.data.repository;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.yourname.cinemate.MyApplication;
import com.yourname.cinemate.data.model.ChangePasswordDto;
import com.yourname.cinemate.data.model.UpdateUserDto;
import com.yourname.cinemate.data.model.User;
import com.yourname.cinemate.data.remote.ApiService;
import com.yourname.cinemate.data.remote.RetrofitClient;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.BufferedSink;
import okio.Okio;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository chịu trách nhiệm cho các hoạt động liên quan đến dữ liệu người dùng.
 */
public class UserRepository {

    private final ApiService apiService;

    public UserRepository() {
        this.apiService = RetrofitClient.getApiService();
    }

    /**
     * Gọi API để lấy thông tin hồ sơ của người dùng hiện tại.
     * AuthInterceptor sẽ tự động đính kèm token xác thực.
     * @return LiveData chứa đối tượng User nếu thành công, hoặc null nếu thất bại.
     */
    public LiveData<User> getUserProfile() {
        final MutableLiveData<User> data = new MutableLiveData<>();

        apiService.getProfile().enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    // Lỗi có thể xảy ra nếu token hết hạn hoặc không hợp lệ
                    Log.e("UserRepository", "Failed to get user profile. Code: " + response.code());
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("UserRepository", "API call failed for getUserProfile", t);
                data.setValue(null);
            }
        });

        return data;
    }
    public LiveData<User> updateUserProfile(String newDisplayName) {
        final MutableLiveData<User> data = new MutableLiveData<>();
        UpdateUserDto dto = new UpdateUserDto(newDisplayName);

        apiService.updateUserProfile(dto).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("UserRepository", "API call failed for updateUserProfile", t);
                data.setValue(null);
            }
        });
        return data;
    }
    public LiveData<User> updateUserAvatar(Uri imageUri) {
        final MutableLiveData<User> data = new MutableLiveData<>();

        // Context cần để truy cập ContentResolver
        Context context = MyApplication.getInstance().getApplicationContext();

        // Tạo một RequestBody tùy chỉnh từ Uri
        RequestBody requestFile = new RequestBody() {
            @Override
            public MediaType contentType() {
                // Lấy kiểu MIME từ Uri
                return MediaType.parse(context.getContentResolver().getType(imageUri));
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                try (InputStream inputStream = context.getContentResolver().openInputStream(imageUri)) {
                    sink.writeAll(Okio.source(inputStream));
                }
            }
        };

        // "avatar" là tên trường backend mong đợi. "image.jpg" là tên file tạm.
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", "image.jpg", requestFile);

        apiService.updateUserAvatar(body).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                // ... logic onResponse giữ nguyên ...
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                // ... logic onFailure giữ nguyên ...
            }
        });
        return data;
    }
    public LiveData<Boolean> changePassword(String oldPassword, String newPassword) {
        final MutableLiveData<Boolean> success = new MutableLiveData<>();
        ChangePasswordDto dto = new ChangePasswordDto(oldPassword, newPassword);

        apiService.changePassword(dto).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                // response.isSuccessful() kiểm tra mã 2xx (ví dụ 200 OK hoặc 204 No Content)
                success.setValue(response.isSuccessful());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("UserRepository", "API call failed for changePassword", t);
                success.setValue(false);
            }
        });
        return success;
    }
}