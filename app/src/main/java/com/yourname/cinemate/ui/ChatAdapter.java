package com.yourname.cinemate.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.yourname.cinemate.R;
import com.yourname.cinemate.data.model.Attachment;
import com.yourname.cinemate.data.model.ChatMessage;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_USER = 1;
    private static final int VIEW_TYPE_ADMIN = 2;
    private List<ChatMessage> messages = new ArrayList<>();

    public void setMessages(List<ChatMessage> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }

    public void addMessage(ChatMessage message) {
        // 1. Kiểm tra trùng lặp ID
        if (message.getId() != null) {
            for (ChatMessage m : messages) {
                if (m.getId() != null && m.getId().equals(message.getId())) {
                    return; // Đã tồn tại -> Thoát
                }
            }
        }
        // 2. Thêm mới
        this.messages.add(message);
        notifyItemInserted(this.messages.size() - 1);
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = messages.get(position);
        if ("USER".equalsIgnoreCase(message.getSenderType())) {
            return VIEW_TYPE_USER;
        } else {
            return VIEW_TYPE_ADMIN;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_USER) {
            View view = inflater.inflate(R.layout.item_chat_sent, parent, false);
            return new SentMessageViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_chat_received, parent, false);
            return new ReceivedMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        if (holder.getItemViewType() == VIEW_TYPE_USER) {
            ((SentMessageViewHolder) holder).bind(message);
        } else {
            ((ReceivedMessageViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    // ==========================================
    // ViewHolder cho tin nhắn của User (Gửi đi)
    // ==========================================
    static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        TextView content;
        RecyclerView recyclerAttachments; // Dùng RecyclerView thay vì ImageView lẻ

        public SentMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.text_message_content);
            recyclerAttachments = itemView.findViewById(R.id.recycler_attachments);
        }

        void bind(ChatMessage message) {
            // 1. Hiển thị nội dung text
            if (message.getContent() != null && !message.getContent().trim().isEmpty()) {
                content.setVisibility(View.VISIBLE);
                content.setText(message.getContent());
            } else {
                content.setVisibility(View.GONE);
            }

            // 2. Xử lý danh sách file đính kèm
            List<Attachment> attachments = message.getAttachments();
            if (attachments != null && !attachments.isEmpty()) {
                recyclerAttachments.setVisibility(View.VISIBLE);

                // Sử dụng ChatAttachmentAdapter để hiển thị danh sách
                ChatAttachmentAdapter attachmentAdapter = new ChatAttachmentAdapter(attachments);
                recyclerAttachments.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
                recyclerAttachments.setAdapter(attachmentAdapter);
            } else {
                recyclerAttachments.setVisibility(View.GONE);
            }
        }
    }

    // ==========================================
    // ViewHolder cho tin nhắn của Admin (Nhận được)
    // ==========================================
    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        TextView content;
        ImageView avatar;
        RecyclerView recyclerAttachments; // Dùng RecyclerView

        public ReceivedMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.image_avatar);
            content = itemView.findViewById(R.id.text_message_content);
            recyclerAttachments = itemView.findViewById(R.id.recycler_attachments);
        }

        void bind(ChatMessage message) {
            // 1. Hiển thị nội dung text
            if (message.getContent() != null && !message.getContent().trim().isEmpty()) {
                content.setText(message.getContent());
                content.setVisibility(View.VISIBLE);
            } else {
                content.setVisibility(View.GONE);
            }

            // 2. Hiển thị Avatar
            String avatarUrl = message.getSenderAvatarUrl();
            if (avatarUrl != null) {
                Glide.with(itemView.getContext())
                        .load(avatarUrl)
                        .circleCrop()
                        .placeholder(R.drawable.ic_profile)
                        .into(avatar);
            } else {
                avatar.setImageResource(R.drawable.ic_profile);
            }

            // 3. Xử lý danh sách file đính kèm
            List<Attachment> attachments = message.getAttachments();
            if (attachments != null && !attachments.isEmpty()) {
                recyclerAttachments.setVisibility(View.VISIBLE);

                // Sử dụng ChatAttachmentAdapter
                ChatAttachmentAdapter attachmentAdapter = new ChatAttachmentAdapter(attachments);
                recyclerAttachments.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
                recyclerAttachments.setAdapter(attachmentAdapter);
            } else {
                recyclerAttachments.setVisibility(View.GONE);
            }
        }
    }
}