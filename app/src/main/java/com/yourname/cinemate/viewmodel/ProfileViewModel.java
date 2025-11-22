package com.yourname.cinemate.viewmodel;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.yourname.cinemate.MyApplication;
import com.yourname.cinemate.data.model.Movie;
import com.yourname.cinemate.data.model.User;
import com.yourname.cinemate.data.repository.MovieRepository;
import com.yourname.cinemate.data.repository.UserRepository;
import com.yourname.cinemate.utils.Event;
import com.yourname.cinemate.utils.SessionManager;

import java.io.File;
import java.util.List;

public class ProfileViewModel extends ViewModel {

    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final SessionManager sessionManager;

    // LiveData cho trạng thái loading
    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>();
    public LiveData<Boolean> isLoading() { return _isLoading; }

    // LiveData cho thông tin người dùng
    private final MutableLiveData<User> _user = new MutableLiveData<>();
    public LiveData<User> getUser() { return _user; }

    // LiveData cho watchlist
    private final MutableLiveData<List<Movie>> _watchlist = new MutableLiveData<>();
    public LiveData<List<Movie>> getWatchlist() { return _watchlist; }
    // Thêm LiveData cho thông báo sự kiện (ví dụ: Event wrapper)
    private final MutableLiveData<Event<String>> _updateStatus = new MutableLiveData<>();
    public LiveData<Event<String>> getUpdateStatus() { return _updateStatus; }

    // LiveData cho trạng thái đổi mật khẩu
    private final MutableLiveData<Event<String>> _changePasswordStatus = new MutableLiveData<>();
    public LiveData<Event<String>> getChangePasswordStatus() { return _changePasswordStatus; }

    public ProfileViewModel() {
        this.userRepository = new UserRepository();
        this.movieRepository = new MovieRepository();
        this.sessionManager = MyApplication.getSessionManager();
    }

    /**
     * Phương thức chính để tải hoặc làm mới tất cả dữ liệu trên màn hình Profile.
     * Sẽ được gọi mỗi khi Fragment được hiển thị.
     */
    public void refreshData() {
        _isLoading.setValue(true);
        loadUserProfile();
        loadUserWatchlist();
    }

    private void loadUserProfile() {
        userRepository.getUserProfile().observeForever(user -> {
            _user.setValue(user);
        });
    }

    private void loadUserWatchlist() {
        movieRepository.getUserWatchlist().observeForever(movies -> {
            _isLoading.setValue(false); // Dừng loading khi watchlist về (coi như đây là dữ liệu chính)
            _watchlist.setValue(movies);
        });
    }

    public void logout() {
        sessionManager.clearTokens();
    }
    public void updateUserDisplayName(String newName) {
        userRepository.updateUserProfile(newName).observeForever(updatedUser -> {
            if (updatedUser != null) {
                // Cập nhật lại LiveData chứa thông tin User
                _user.setValue(updatedUser);
                _updateStatus.setValue(new Event<>("Cập nhật tên thành công!"));
            } else {
                _updateStatus.setValue(new Event<>("Lỗi: Không thể cập nhật tên"));
            }
        });
    }
    public void updateUserAvatar(Uri imageUri) {
        // Có thể hiển thị một trạng thái loading riêng cho avatar
        _isLoading.setValue(true); // Tạm dùng loading chung
        userRepository.updateUserAvatar(imageUri).observeForever(updatedUser -> {
            _isLoading.setValue(false);
            if (updatedUser != null) {
                // Cập nhật lại LiveData user để giao diện tự thay đổi avatar
                _user.setValue(updatedUser);
                // Sử dụng LiveData _updateStatus đã có
                _updateStatus.setValue(new Event<>("Cập nhật ảnh đại diện thành công!"));
            } else {
                _updateStatus.setValue(new Event<>("Lỗi: Không thể cập nhật ảnh"));
            }
        });
    }
    public void changePassword(String oldPassword, String newPassword) {
        userRepository.changePassword(oldPassword, newPassword).observeForever(success -> {
            if (success) {
                _changePasswordStatus.setValue(new Event<>("Đổi mật khẩu thành công!"));
            } else {
                // Lỗi có thể do sai mật khẩu cũ hoặc lỗi server
                _changePasswordStatus.setValue(new Event<>("Đổi mật khẩu thất bại. Vui lòng kiểm tra lại mật khẩu cũ."));
            }
        });
    }
}