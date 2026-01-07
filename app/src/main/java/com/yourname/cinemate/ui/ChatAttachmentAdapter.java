package com.yourname.cinemate.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.yourname.cinemate.R;
import com.yourname.cinemate.data.model.Attachment;
import java.util.List;

public class ChatAttachmentAdapter extends RecyclerView.Adapter<ChatAttachmentAdapter.AttachmentViewHolder> {

    private final List<Attachment> attachments;

    public ChatAttachmentAdapter(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    @NonNull
    @Override
    public AttachmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_attachment, parent, false);
        return new AttachmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttachmentViewHolder holder, int position) {
        holder.bind(attachments.get(position));
    }

    @Override
    public int getItemCount() {
        return attachments != null ? attachments.size() : 0;
    }

    static class AttachmentViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        LinearLayout fileLayout;
        TextView fileName;

        public AttachmentViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_attachment_preview);
            fileLayout = itemView.findViewById(R.id.layout_file_info);
            fileName = itemView.findViewById(R.id.text_attachment_name);
        }

        public void bind(Attachment attachment) {
            Context context = itemView.getContext();
            String fileType = attachment.getType();
            String url = attachment.getUrl();

            // Xử lý sự kiện click để mở ảnh/file
            itemView.setOnClickListener(v -> {
                if (url != null && !url.isEmpty()) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        context.startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            // Kiểm tra xem có phải là ảnh không
            boolean isImage = fileType != null && fileType.startsWith("image");

            if (isImage) {
                fileLayout.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);

                // Load ảnh bằng Glide
                Glide.with(context)
                        .load(url)
                        .placeholder(R.drawable.ic_launcher_background) // Thay bằng ảnh placeholder của bạn
                        .error(android.R.drawable.stat_notify_error)
                        .centerCrop()
                        .into(imageView);
            } else {
                // Là file thường
                imageView.setVisibility(View.GONE);
                fileLayout.setVisibility(View.VISIBLE);
                fileName.setText(attachment.getFileName());
            }
        }
    }
}