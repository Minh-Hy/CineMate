package com.yourname.cinemate.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.yourname.cinemate.R;
import com.yourname.cinemate.data.model.Comment;
import java.util.ArrayList;
import java.util.List;

/**
 * Adapter này dùng để hiển thị danh sách các câu trả lời (replies)
 * bên trong một item bình luận gốc.
 */
public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.ReplyViewHolder> {

    private List<Comment> replies = new ArrayList<>();

    // Dùng để gán danh sách replies ban đầu
    public void setReplies(List<Comment> replies) {
        this.replies = new ArrayList<>(replies);
        notifyDataSetChanged();
    }

    // Dùng để thêm các replies mới khi "Tải thêm"
    public void addReplies(List<Comment> newReplies) {
        int startPosition = this.replies.size();
        this.replies.addAll(newReplies);
        notifyItemRangeInserted(startPosition, newReplies.size());
    }

    @NonNull
    @Override
    public ReplyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reply, parent, false);
        return new ReplyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReplyViewHolder holder, int position) {
        holder.bind(replies.get(position));
    }

    @Override
    public int getItemCount() {
        return replies != null ? replies.size() : 0;
    }

    // --- ViewHolder cho một câu trả lời ---
    static class ReplyViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView username, date, content;

        public ReplyViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.image_avatar_reply);
            username = itemView.findViewById(R.id.text_username_reply);
            date = itemView.findViewById(R.id.text_date_reply);
            content = itemView.findViewById(R.id.text_content_reply);
        }

        public void bind(Comment reply) {
            // Bind dữ liệu cho câu trả lời
            if (reply.getUser() != null) {
                username.setText(reply.getUser().getDisplayName());
                Glide.with(itemView.getContext())
                        .load(reply.getUser().getAvatarUrl())
                        .placeholder(android.R.drawable.sym_def_app_icon)
                        .error(android.R.drawable.sym_def_app_icon)
                        .circleCrop()
                        .into(avatar);
            }
            content.setText(reply.getContent());
            date.setText(reply.getCreatedAt()); // TODO: Format lại date cho đẹp
        }
    }
}