package com.yourname.cinemate.ui;

import android.util.Log;
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
import com.yourname.cinemate.data.model.Comment;
import java.util.ArrayList;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    // Interface để giao tiếp ngược lại với Fragment
    public interface CommentInteractionListener {
        void onReplyClicked(Comment parentComment);
        void onLoadMoreReplies(String parentCommentId, int nextPage);
    }

    private List<Comment> comments = new ArrayList<>();
    private final CommentInteractionListener listener;

    public CommentAdapter(CommentInteractionListener listener) {
        this.listener = listener;
    }

    // Ghi đè toàn bộ danh sách, thường dùng cho lần tải đầu tiên
    public void setComments(List<Comment> newComments) {
        this.comments = new ArrayList<>(newComments); // Tạo một bản sao để tránh các vấn đề tham chiếu
        notifyDataSetChanged();
    }

    // Thêm một bình luận gốc mới vào đầu danh sách
    public void addComment(Comment comment) {
        this.comments.add(0, comment);
        notifyItemInserted(0);
    }

    // Thêm một reply mới vào một comment cha đã tồn tại trong danh sách
    public void addReplyToParent(Comment reply) {
        if (reply.getParentCommentId() == null) {
            Log.e("CommentAdapter", "addReplyToParent called with a non-reply comment.");
            return;
        }

        for (int i = 0; i < comments.size(); i++) {
            Comment parent = comments.get(i);
            if (parent.getId().equals(reply.getParentCommentId())) {
                // Đảm bảo list replies được khởi tạo
                if (parent.getReplies() == null) {
                    parent.setReplies(new ArrayList<>()); // Cần thêm setter `setReplies` trong model `Comment`
                }
                parent.getReplies().add(reply);
                notifyItemChanged(i); // Chỉ vẽ lại item cha này và các view con của nó
                return;
            }
        }
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        holder.bind(comments.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return comments != null ? comments.size() : 0;
    }

    // --- ViewHolder cho một bình luận gốc ---
    static class CommentViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView username, date, content, replyButton, loadMoreRepliesButton;
        RecyclerView repliesRecyclerView;
        ReplyAdapter replyAdapter;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.image_avatar);
            username = itemView.findViewById(R.id.text_username);
            date = itemView.findViewById(R.id.text_comment_date);
            content = itemView.findViewById(R.id.text_comment_content);
            replyButton = itemView.findViewById(R.id.text_reply_button);
            repliesRecyclerView = itemView.findViewById(R.id.recycler_replies);
            loadMoreRepliesButton = itemView.findViewById(R.id.button_load_more_replies);

            // Setup RecyclerView con cho replies
            replyAdapter = new ReplyAdapter(); // Bạn cần tạo class ReplyAdapter này
            repliesRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            repliesRecyclerView.setAdapter(replyAdapter);
        }

        public void bind(final Comment comment, final CommentInteractionListener listener) {
            // Bind dữ liệu cho bình luận gốc
            if (comment.getUser() != null) {
                username.setText(comment.getUser().getDisplayName());
                Glide.with(itemView.getContext())
                        .load(comment.getUser().getAvatarUrl())
                        .placeholder(android.R.drawable.sym_def_app_icon)
                        .error(android.R.drawable.sym_def_app_icon)
                        .circleCrop()
                        .into(avatar);
            }
            content.setText(comment.getContent());
            date.setText(comment.getCreatedAt()); // TODO: Format lại date cho đẹp

            // Gán sự kiện click cho nút "Trả lời"
            replyButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onReplyClicked(comment);
                }
            });

            // Hiển thị các replies đã có sẵn
            if (comment.getReplies() != null && !comment.getReplies().isEmpty()) {
                repliesRecyclerView.setVisibility(View.VISIBLE);
                replyAdapter.setReplies(comment.getReplies());
            } else {
                repliesRecyclerView.setVisibility(View.GONE);
            }

            // TODO: Logic cho nút "Xem thêm trả lời"
            // loadMoreRepliesButton.setOnClickListener(v -> listener.onLoadMoreReplies(...));
        }
    }
}