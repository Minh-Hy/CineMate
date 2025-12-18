package com.yourname.cinemate.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
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
        // 1. Kiểm tra xem tin nhắn này đã có trong danh sách chưa (dựa vào ID)
        if (message.getId() != null) {
            for (ChatMessage m : messages) {
                if (m.getId() != null && m.getId().equals(message.getId())) {
                    return; // Đã tồn tại -> Thoát ngay, không thêm nữa
                }
            }
        }

        // 2. Nếu chưa có thì mới thêm vào
        this.messages.add(message);
        notifyItemInserted(this.messages.size() - 1);
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = messages.get(position);
        // Kiểm tra senderType trả về từ backend
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
        ImageView imageAttachment; // Đã sửa tên cho thống nhất
        TextView fileAttachment;   // Thêm view hiển thị file

        public SentMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.text_message_content);
            imageAttachment = itemView.findViewById(R.id.image_attachment);
            fileAttachment = itemView.findViewById(R.id.text_file_attachment);
        }

        void bind(ChatMessage message) {
            // 1. Xử lý hiển thị Text
            if (message.getContent() != null && !message.getContent().isEmpty()) {
                content.setVisibility(View.VISIBLE);
                content.setText(message.getContent());
            } else {
                content.setVisibility(View.GONE);
            }

            // 2. Xử lý Attachment (Copy logic từ ReceivedMessageViewHolder)
            imageAttachment.setVisibility(View.GONE);
            fileAttachment.setVisibility(View.GONE);

            List<Attachment> attachments = message.getAttachments();

            if (attachments != null && !attachments.isEmpty()) {
                Attachment attachment = attachments.get(0);
                String type = attachment.getType();
                String url = attachment.getUrl();
                String fileName = attachment.getFileName();

                if ("image".equals(type)) {
                    imageAttachment.setVisibility(View.VISIBLE);
                    Glide.with(itemView.getContext())
                            .load(url)
                            .into(imageAttachment);

                    imageAttachment.setOnClickListener(v -> openUrlInBrowser(itemView.getContext(), url));
                } else if ("file".equals(type)) {
                    fileAttachment.setVisibility(View.VISIBLE);
                    fileAttachment.setText("📎 " + fileName);
                    fileAttachment.setOnClickListener(v -> openUrlInBrowser(itemView.getContext(), url));
                }
            }
        }

        private void openUrlInBrowser(Context context, String url) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // ==========================================
    // ViewHolder cho tin nhắn của Admin (Nhận được)
    // ==========================================
    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        TextView content;
        ImageView avatar;
        ImageView imageAttachment;
        TextView fileAttachment;

        public ReceivedMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.image_avatar);
            content = itemView.findViewById(R.id.text_message_content);
            imageAttachment = itemView.findViewById(R.id.image_attachment);
            fileAttachment = itemView.findViewById(R.id.text_file_attachment);
        }

        void bind(ChatMessage message) {
            // 1. Hiển thị nội dung text
            if (message.getContent() != null && !message.getContent().isEmpty()) {
                content.setText(message.getContent());
                content.setVisibility(View.VISIBLE);
            } else {
                content.setVisibility(View.GONE);
            }

            // 2. Hiển thị Avatar (Dùng helper method từ Model nếu có, hoặc check null)
            String avatarUrl = message.getSenderAvatarUrl(); // Sử dụng hàm tiện ích trong ChatMessage
            if (avatarUrl != null) {
                Glide.with(itemView.getContext())
                        .load(avatarUrl)
                        .circleCrop()
                        .placeholder(R.drawable.ic_profile)
                        .into(avatar);
            } else {
                // Avatar mặc định cho Admin
                avatar.setImageResource(R.drawable.ic_profile);
            }

            // 3. Xử lý Attachment
            imageAttachment.setVisibility(View.GONE);
            fileAttachment.setVisibility(View.GONE);

            List<Attachment> attachments = message.getAttachments();

            if (attachments != null && !attachments.isEmpty()) {
                Attachment attachment = attachments.get(0);
                String type = attachment.getType();
                String url = attachment.getUrl();
                String fileName = attachment.getFileName();

                if ("image".equals(type)) {
                    imageAttachment.setVisibility(View.VISIBLE);
                    Glide.with(itemView.getContext())
                            .load(url)
                            .into(imageAttachment);

                    imageAttachment.setOnClickListener(v -> openUrlInBrowser(itemView.getContext(), url));
                } else if ("file".equals(type)) {
                    fileAttachment.setVisibility(View.VISIBLE);
                    fileAttachment.setText("📎 " + fileName);
                    fileAttachment.setOnClickListener(v -> openUrlInBrowser(itemView.getContext(), url));
                }
            }
        }

        private void openUrlInBrowser(Context context, String url) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}