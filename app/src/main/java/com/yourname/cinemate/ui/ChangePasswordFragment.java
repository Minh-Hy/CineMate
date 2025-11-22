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
import com.yourname.cinemate.viewmodel.ProfileViewModel;

public class ChangePasswordFragment extends Fragment {

    private ProfileViewModel viewModel;
    private TextInputEditText oldPasswordEditText, newPasswordEditText, confirmPasswordEditText;
    private Button saveButton;
    private ProgressBar progressBar;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_change_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Sử dụng chung ViewModel với ProfileFragment
        viewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);

        bindViews(view);
        setupToolbar(view);
        observeViewModel();

        saveButton.setOnClickListener(v -> handleChangePassword());
    }

    private void bindViews(View view) {
        oldPasswordEditText = view.findViewById(R.id.edit_text_old_password);
        newPasswordEditText = view.findViewById(R.id.edit_text_new_password);
        confirmPasswordEditText = view.findViewById(R.id.edit_text_confirm_password);
        saveButton = view.findViewById(R.id.button_save_password);
        progressBar = view.findViewById(R.id.progress_bar_change_password);
    }

    private void setupToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar_change_password);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> getParentFragmentManager().popBackStack());
    }

    private void observeViewModel() {
        viewModel.getChangePasswordStatus().observe(getViewLifecycleOwner(), event -> {
            String message = event.getContentIfNotHandled();
            if (message != null) {
                progressBar.setVisibility(View.GONE);
                saveButton.setEnabled(true);
                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                // Nếu thành công thì quay lại màn hình trước
                if (message.contains("thành công")) {
                    getParentFragmentManager().popBackStack();
                }
            }
        });
    }

    private void handleChangePassword() {
        String oldPass = oldPasswordEditText.getText().toString();
        String newPass = newPasswordEditText.getText().toString();
        String confirmPass = confirmPasswordEditText.getText().toString();

        if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }
        if (newPass.length() < 6) { // Ví dụ: kiểm tra độ dài
            Toast.makeText(getContext(), "Mật khẩu mới phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!newPass.equals(confirmPass)) {
            Toast.makeText(getContext(), "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        saveButton.setEnabled(false);
        viewModel.changePassword(oldPass, newPass);
    }
}