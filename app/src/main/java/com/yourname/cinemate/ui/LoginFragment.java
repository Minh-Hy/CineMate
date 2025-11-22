package com.yourname.cinemate.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.yourname.cinemate.R;
import com.yourname.cinemate.viewmodel.AuthViewModel;

public class LoginFragment extends Fragment {

    private AuthViewModel authViewModel;
    private TextInputEditText emailEditText;
    private TextInputEditText passwordEditText;
    private Button loginButton;
    private TextView goToRegisterTextView;
    private ProgressBar progressBar;
    private TextView forgotPasswordButton;
    private SignInButton googleSignInButton;
    private GoogleSignInClient mGoogleSignInClient;
    // Launcher để xử lý kết quả trả về từ màn hình đăng nhập của Google
    private ActivityResultLauncher<Intent> googleSignInLauncher;;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // --- BƯỚC 1: Cấu hình Google Sign-In ---
        // Yêu cầu lấy email và idToken, sử dụng Web Client ID mà bạn đã tạo
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);

        // --- BƯỚC 2: Khởi tạo Trình xử lý Kết quả (Launcher) ---
        googleSignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Kết quả trả về thành công, xử lý nó
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                        handleGoogleSignInResult(task);
                    } else {
                        // Người dùng đã hủy hoặc có lỗi xảy ra
                        Toast.makeText(getContext(), "Đăng nhập Google đã bị hủy.", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo ViewModel
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);



        // Thiết lập sự kiện click
        bindViews(view);
        setupToolbar(view);
        setupClickListeners();
    }
    private void bindViews(View view) {
        // Ánh xạ views
        emailEditText = view.findViewById(R.id.edit_text_email);
        passwordEditText = view.findViewById(R.id.edit_text_password);
        loginButton = view.findViewById(R.id.button_login);
        goToRegisterTextView = view.findViewById(R.id.text_go_to_register);
        progressBar = view.findViewById(R.id.progress_bar_login);
        forgotPasswordButton = view.findViewById(R.id.button_forgot_password);
        googleSignInButton = view.findViewById(R.id.button_google_sign_in);
    }

    private void setupToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar_login);
        // Bắt sự kiện click cho nút Back trên Toolbar
        toolbar.setNavigationOnClickListener(v -> getParentFragmentManager().popBackStack());
    }

    private void setupClickListeners() {
        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập đầy đủ email và mật khẩu", Toast.LENGTH_SHORT).show();
                return;
            }

            // Gọi ViewModel để thực hiện đăng nhập
            performLogin(email, password);
        });

        goToRegisterTextView.setOnClickListener(v -> {
            // Điều hướng đến màn hình đăng ký
            Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_registerFragment);
        });
        // Thêm sự kiện cho nút Quên mật khẩu
        forgotPasswordButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_forgotPasswordFragment);
        });
        // Gán sự kiện click cho nút Google
        googleSignInButton.setOnClickListener(v -> {
            // --- BƯỚC 3: Kích hoạt Luồng Đăng nhập ---
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            googleSignInLauncher.launch(signInIntent);
        });
    }

    private void handleGoogleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            // --- BƯỚC 4: Lấy idToken ---
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String idToken = account.getIdToken();

            if (idToken != null) {
                // --- BƯỚC 5: Gửi idToken lên Backend ---
                performGoogleSignInOnBackend(idToken);
            } else {
                Toast.makeText(getContext(), "Không thể lấy Google ID Token.", Toast.LENGTH_SHORT).show();
            }

        } catch (ApiException e) {
            Log.w("GoogleSignIn", "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(getContext(), "Đăng nhập Google thất bại. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Gọi ViewModel để xác thực với server của bạn.
     */
    private void performGoogleSignInOnBackend(String idToken) {
        progressBar.setVisibility(View.VISIBLE);

        authViewModel.googleSignIn(idToken).observe(getViewLifecycleOwner(), loginResponse -> {
            progressBar.setVisibility(View.GONE);
            if (loginResponse != null && loginResponse.getAccessToken() != null) {
                // --- BƯỚC 6: Xử lý Đăng nhập Thành công ---
                Toast.makeText(getContext(), "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                authViewModel.saveTokens(loginResponse.getAccessToken(), loginResponse.getRefreshToken());
                Navigation.findNavController(requireView()).navigate(R.id.action_loginFragment_to_homeFragment);
            } else {
                Toast.makeText(getContext(), "Xác thực với server thất bại.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void performLogin(String email, String password) {
        progressBar.setVisibility(View.VISIBLE);
        loginButton.setEnabled(false);

        authViewModel.login(email, password).observe(getViewLifecycleOwner(), loginResponse -> {
            progressBar.setVisibility(View.GONE);
            loginButton.setEnabled(true);

            // KIỂM TRA CẢ HAI TOKEN
            if (loginResponse != null && loginResponse.getAccessToken() != null && loginResponse.getRefreshToken() != null) {
                // Đăng nhập thành công
                Toast.makeText(getContext(), "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                // LƯU CẢ HAI TOKEN
                authViewModel.saveTokens(
                        loginResponse.getAccessToken(),
                        loginResponse.getRefreshToken()
                );

                // Điều hướng đến màn hình chính
                Navigation.findNavController(requireView()).navigate(R.id.action_loginFragment_to_homeFragment);

            } else {
                // Đăng nhập thất bại
                Toast.makeText(getContext(), "Đăng nhập thất bại. Vui lòng kiểm tra lại thông tin.", Toast.LENGTH_LONG).show();
            }
        });
    }
}