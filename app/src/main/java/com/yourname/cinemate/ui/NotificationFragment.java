package com.yourname.cinemate.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.yourname.cinemate.R;
import com.yourname.cinemate.data.model.NotificationItem;
import com.yourname.cinemate.viewmodel.NotificationViewModel;

public class NotificationFragment extends Fragment implements NotificationAdapter.OnNotificationClickListener {

    private NotificationViewModel viewModel;
    private NotificationAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(NotificationViewModel.class);

        // Setup Toolbar
        Toolbar toolbar = view.findViewById(R.id.toolbar_notification);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> getParentFragmentManager().popBackStack());

        // Setup RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recycler_notifications);
        adapter = new NotificationAdapter(this);
        recyclerView.setAdapter(adapter);

        // Setup Swipe Refresh
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_notif);
        swipeRefreshLayout.setOnRefreshListener(() -> viewModel.loadNotifications());

        observeViewModel();
    }

    private void observeViewModel() {
        viewModel.isLoading().observe(getViewLifecycleOwner(), isLoading -> {
            swipeRefreshLayout.setRefreshing(isLoading);
        });

        viewModel.getNotifications().observe(getViewLifecycleOwner(), items -> {
            if (items != null) {
                adapter.setItems(items);
            }
        });
    }

    @Override
    public void onNotificationClick(NotificationItem item) {
        // 1. Đánh dấu đã đọc
        if (!item.isRead()) {
            viewModel.markRead(item.getId());
        }

        // 2. Điều hướng dựa trên dữ liệu (Deeplink Handling)
        if (item.getData() != null && item.getData().getMovieId() > 0) {
            Bundle bundle = new Bundle();
            bundle.putInt("movieId", item.getData().getMovieId());
            // Có thể thêm commentId vào bundle để scroll tới comment đó nếu muốn

            Navigation.findNavController(requireView())
                    .navigate(R.id.action_notificationFragment_to_detailFragment, bundle);
        }
    }
}