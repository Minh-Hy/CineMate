package com.yourname.cinemate.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.google.android.material.textfield.TextInputEditText;
import com.yourname.cinemate.R;
import com.yourname.cinemate.viewmodel.AuthViewModel;

public class RegisterFragment extends Fragment {

    private AuthViewModel authViewModel;
    private TextInputEditText usernameEditText, emailEditText, passwordEditText;
    private Button registerButton;
    private TextView goToLoginTextView;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        // Ánh xạ views
        usernameEditText = view.findViewById(R.id.edit_text_username);
        emailEditText = view.findViewById(R.id.edit_text_email_register);
        passwordEditText = view.findViewById(R.id.edit_text_password_register);
        registerButton = view.findViewById(R.id.button_register);
        goToLoginTextView = view.findViewById(R.id.text_go_to_login);
        progressBar = view.findViewById(R.id.progress_bar_register);

        setupClickListeners();
    }

    private void setupClickListeners() {
        registerButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            performRegistration(username, email, password);
        });

        goToLoginTextView.setOnClickListener(v -> {
            // Điều hướng về màn hình đăng nhập
            Navigation.findNavController(v).navigate(R.id.action_registerFragment_to_loginFragment);
        });
    }

    private void performRegistration(String username, String email, String password) {
        progressBar.setVisibility(View.VISIBLE);
        registerButton.setEnabled(false);

        authViewModel.register(username, email, password).observe(getViewLifecycleOwner(), registerResponse -> {
            progressBar.setVisibility(View.GONE);
            registerButton.setEnabled(true);

            if (registerResponse != null) {
                // Đăng ký thành công
                Toast.makeText(getContext(), "Đăng ký thành công! Vui lòng đăng nhập.", Toast.LENGTH_LONG).show();
                // Tự động điều hướng về màn hình đăng nhập
                Navigation.findNavController(getView()).navigate(R.id.action_registerFragment_to_loginFragment);
            } else {
                // Đăng ký thất bại (ví dụ: email đã tồn tại)
                Toast.makeText(getContext(), "Đăng ký thất bại. Email có thể đã được sử dụng.", Toast.LENGTH_LONG).show();
            }
        });
    }
}