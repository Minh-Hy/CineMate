package com.yourname.cinemate.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;
import com.yourname.cinemate.R;



public class SupportFragment extends Fragment {
    MaterialButton buttonCall;
    MaterialButton buttonChat;
    MaterialButton buttonRestorePassword;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_support, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Gọi các phương thức để thiết lập UI
        bindViews(view);
        setupClickListeners();
    }

    private void bindViews(View view) {
        buttonCall = view.findViewById(R.id.button_call);
        buttonChat = view.findViewById(R.id.button_chat);
        buttonRestorePassword = view.findViewById(R.id.button_restore_password);
    }

    private void setupClickListeners() {
        buttonCall.setOnClickListener(v -> {
            // Xử lý khi người dùng nhấn nút Gọi
        });

        buttonChat.setOnClickListener(v -> {
            // Xử lý khi người dùng nhấn nút Trò chuyện
        });

        buttonRestorePassword.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_supportFragment_to_resetPasswordFragment);
        });
    }
}