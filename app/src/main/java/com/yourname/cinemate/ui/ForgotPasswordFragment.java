package com.yourname.cinemate.ui;

import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.google.android.material.textfield.TextInputEditText;
import com.yourname.cinemate.R;
import com.yourname.cinemate.viewmodel.AuthViewModel;

public class ForgotPasswordFragment extends Fragment {

    private AuthViewModel authViewModel;
    private TextInputEditText emailEditText;
    private Button sendRequestButton;
    private ProgressBar progressBar;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_forgot_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Lấy ViewModel của Activity cha để chia sẻ trạng thái
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        bindViews(view);
        setupToolbar(view);
        observeViewModel();

        sendRequestButton.setOnClickListener(v -> handleSendRequest());
    }

    private void bindViews(View view) {
        emailEditText = view.findViewById(R.id.edit_text_forgot_email);
        sendRequestButton = view.findViewById(R.id.button_send_request);
        progressBar = view.findViewById(R.id.progress_bar_forgot);
    }

    private void setupToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar_forgot_password);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        if (((AppCompatActivity) requireActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle("");
        }
        toolbar.setNavigationOnClickListener(v -> getParentFragmentManager().popBackStack());
    }

    private void observeViewModel() {
        authViewModel.getPasswordResetStatus().observe(getViewLifecycleOwner(), event -> {
            String message = event.peekContent(); // Dùng peek để không tiêu thụ event
            if (message != null && message.contains("hướng dẫn")) {
                progressBar.setVisibility(View.GONE);
                sendRequestButton.setEnabled(true);
                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                // Tùy chọn: Tự động điều hướng đến màn hình reset
                // Navigation.findNavController(getView()).navigate(R.id.action_forgotPasswordFragment_to_resetPasswordFragment);
            }
        });
    }

    private void handleSendRequest() {
        String email = emailEditText.getText().toString().trim();
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Vui lòng nhập một email hợp lệ");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        sendRequestButton.setEnabled(false);
        authViewModel.requestPasswordReset(email);
    }
}