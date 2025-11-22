package com.yourname.cinemate.ui;

import android.os.Bundle;
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

public class ResetPasswordFragment extends Fragment {

    private AuthViewModel authViewModel;
    private TextInputEditText tokenEditText, newPasswordEditText;
    private Button submitButton;
    private ProgressBar progressBar;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reset_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        bindViews(view);
        setupToolbar(view);
        observeViewModel();

        submitButton.setOnClickListener(v -> handleSubmitReset());
    }

    private void bindViews(View view) {
        tokenEditText = view.findViewById(R.id.edit_text_token);
        newPasswordEditText = view.findViewById(R.id.edit_text_new_password_reset);
        submitButton = view.findViewById(R.id.button_submit_reset);
        progressBar = view.findViewById(R.id.progress_bar_reset);
    }

    private void setupToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar_reset_password);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        if (((AppCompatActivity) requireActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle("");
        }
        toolbar.setNavigationOnClickListener(v -> getParentFragmentManager().popBackStack());
    }

    private void observeViewModel() {
        authViewModel.getPasswordResetStatus().observe(getViewLifecycleOwner(), event -> {
            String message = event.getContentIfNotHandled();
            if (message != null) {
                progressBar.setVisibility(View.GONE);
                submitButton.setEnabled(true);
                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();

                if (message.contains("thành công")) {
                    Navigation.findNavController(getView()).navigate(R.id.action_resetPasswordFragment_to_loginFragment);
                }
            }
        });
    }

    private void handleSubmitReset() {
        String token = tokenEditText.getText().toString().trim();
        String newPassword = newPasswordEditText.getText().toString();

        if (token.isEmpty() || newPassword.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }
        if (newPassword.length() < 6) {
            Toast.makeText(getContext(), "Mật khẩu mới phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        submitButton.setEnabled(false);
        authViewModel.resetPassword(token, newPassword);
    }
}