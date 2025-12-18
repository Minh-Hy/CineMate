package com.yourname.cinemate.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.yourname.cinemate.R;
import com.yourname.cinemate.viewmodel.ChatViewModel;

public class ChatFragment extends Fragment {

    private ChatViewModel viewModel;
    private RecyclerView recyclerView;
    private ChatAdapter adapter;
    private EditText messageEditText;
    private ImageButton sendButton;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ChatViewModel.class);

        bindViews(view);
        setupToolbar(view);
        setupRecyclerView();

        // Gọi lấy lịch sử chat
        viewModel.loadChatHistory();

        observeViewModel();

        sendButton.setOnClickListener(v -> {
            String content = messageEditText.getText().toString().trim();
            if (!content.isEmpty()) {
                viewModel.sendMessage(content);
                messageEditText.setText(""); // Xóa ô nhập
            }
        });

        // Pull to refresh để tải lại lịch sử (nếu cần)
        swipeRefreshLayout.setOnRefreshListener(() -> {
            viewModel.loadChatHistory();
        });
    }

    private void bindViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_chat_messages);
        messageEditText = view.findViewById(R.id.edit_text_chat_message);
        sendButton = view.findViewById(R.id.button_send_chat_message);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_chat);
    }

    private void setupToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar_chat);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> getParentFragmentManager().popBackStack());
    }

    private void setupRecyclerView() {
        // Không cần truyền currentUserId vào Adapter nữa vì ta dùng senderType từ backend
        adapter = new ChatAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true); // Tin nhắn mới nhất ở dưới cùng
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void observeViewModel() {
        // Quan sát danh sách tin nhắn (lịch sử)
        viewModel.getMessages().observe(getViewLifecycleOwner(), messages -> {
            if (messages != null) {
                adapter.setMessages(messages);
                // Cuộn xuống cuối khi load xong lịch sử
                if (!messages.isEmpty()) {
                    recyclerView.scrollToPosition(messages.size() - 1);
                }
            }
        });

        viewModel.isLoading().observe(getViewLifecycleOwner(), isLoading -> {
            swipeRefreshLayout.setRefreshing(isLoading);
        });
    }
}