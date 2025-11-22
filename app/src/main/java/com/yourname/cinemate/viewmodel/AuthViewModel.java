package com.yourname.cinemate.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.yourname.cinemate.MyApplication;
import com.yourname.cinemate.data.model.LoginResponse;
import com.yourname.cinemate.data.model.RegisterResponse;
import com.yourname.cinemate.data.repository.AuthRepository;
import com.yourname.cinemate.utils.Event;
import com.yourname.cinemate.utils.SessionManager;

public class AuthViewModel extends ViewModel {
    private final AuthRepository authRepository;
    private final SessionManager sessionManager;
    private final MutableLiveData<Event<String>> _passwordResetStatus = new MutableLiveData<>();

    public LiveData<Event<String>> getPasswordResetStatus() { return _passwordResetStatus; }

    public AuthViewModel() {
        this.authRepository = new AuthRepository();
        //Lay session tu lop application
        this.sessionManager = MyApplication.getSessionManager();
    }
    /**
     * Gọi Repository để thực hiện đăng nhập.
     * Khi nhận được kết quả, nó sẽ lưu token.
     * Fragment sẽ observe LiveData trả về này để biết kết quả.
     */
    public LiveData<LoginResponse> login (String email, String password) {
        LiveData<LoginResponse> loginResponseLiveData = authRepository.loginUser(email, password);

        // Chúng ta không trực tiếp observe ở đây. Thay vào đó, chúng ta sẽ "biến đổi" nó.
        // Nhưng để đơn giản, chúng ta sẽ để Fragment observe trực tiếp và xử lý lưu token ở Fragment.
        // Cách nâng cao hơn là dùng MediatorLiveData hoặc Transformations.map.
        // For simplicity, we will save the token in the fragment after observing.
        return loginResponseLiveData;
    }
    /**
     * Gọi Repository để thực hiện đăng ký.
     */
    public LiveData<RegisterResponse> register (String username, String email, String password) {
        return authRepository.registerUser(username, email, password);
    }
    /**
     * Một phương thức tiện ích để lưu token.
     * ViewModel là nơi tốt hơn để xử lý logic này thay vì Fragment.
     */
    public void saveTokens(String accessToken, String refreshToken) {
        sessionManager.saveTokens(accessToken, refreshToken);
    }
    public LiveData<LoginResponse> googleSignIn(String idToken) {
        return authRepository.googleSignIn(idToken);
    }
    public void requestPasswordReset(String email) {
        authRepository.requestPasswordReset(email).observeForever(success -> {
            if (success) {
                _passwordResetStatus.setValue(new Event<>("Nếu email tồn tại, một hướng dẫn sẽ được gửi đến bạn."));
            } else {
                _passwordResetStatus.setValue(new Event<>("Yêu cầu thất bại, vui lòng thử lại."));
            }
        });
    }

    public void resetPassword(String token, String newPassword) {
        authRepository.resetPassword(token, newPassword).observeForever(success -> {
            if (success) {
                _passwordResetStatus.setValue(new Event<>("Đổi mật khẩu thành công! Vui lòng đăng nhập lại."));
            } else {
                _passwordResetStatus.setValue(new Event<>("Lỗi: Token không hợp lệ hoặc đã hết hạn."));
            }
        });
    }
}
