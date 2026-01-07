package com.yourname.cinemate.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.yourname.cinemate.R;
import com.yourname.cinemate.data.model.NotificationItem;
import java.util.ArrayList;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotifViewHolder> {

    public interface OnNotificationClickListener {
        void onNotificationClick(NotificationItem item);
    }

    private List<NotificationItem> items = new ArrayList<>();
    private final OnNotificationClickListener listener;

    public NotificationAdapter(OnNotificationClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<NotificationItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull @Override
    public NotifViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new NotifViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotifViewHolder holder, int position) {
        holder.bind(items.get(position), listener);
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class NotifViewHolder extends RecyclerView.ViewHolder {
        TextView title, body, time;
        View unreadDot;
        ImageView icon;

        public NotifViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.text_notif_title);
            body = itemView.findViewById(R.id.text_notif_body);
            time = itemView.findViewById(R.id.text_notif_time);
            unreadDot = itemView.findViewById(R.id.view_unread_dot);
            icon = itemView.findViewById(R.id.img_notif_icon);
        }

        public void bind(NotificationItem item, OnNotificationClickListener listener) {
            title.setText(item.getTitle());
            body.setText(item.getBody());
            // TODO: Format date cho đẹp (ví dụ: "vừa xong", "1 giờ trước")
            time.setText(item.getCreatedAt());

            // Hiển thị chấm đỏ nếu chưa đọc
            unreadDot.setVisibility(item.isRead() ? View.INVISIBLE : View.VISIBLE);

            // Thay đổi độ đậm nhạt của text nếu đã đọc
            title.setAlpha(item.isRead() ? 0.7f : 1.0f);

            itemView.setOnClickListener(v -> listener.onNotificationClick(item));
        }
    }
}