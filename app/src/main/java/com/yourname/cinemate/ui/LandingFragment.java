package com.yourname.cinemate.ui;

import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import com.yourname.cinemate.R;
import com.yourname.cinemate.data.model.Movie;
import com.yourname.cinemate.viewmodel.HomeViewModel; // Tái sử dụng HomeViewModel để lấy phim
import com.yourname.cinemate.viewmodel.LandingViewModel;

public class LandingFragment extends Fragment implements MoviePosterAdapter.OnMovieClickListener {

    private LandingViewModel viewModel;
    private RecyclerView backgroundRecyclerView;
    private MoviePosterAdapter posterAdapter;
    private Button goToLoginButton;
    private ImageButton moreOptions;


    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_landing, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(LandingViewModel.class);

        // Ánh xạ views
        backgroundRecyclerView = view.findViewById(R.id.recycler_background_posters);
        goToLoginButton = view.findViewById(R.id.button_go_to_login);
        moreOptions = view.findViewById(R.id.btn_more);

        setupRecyclerView();
        observeViewModel();
        setupClickListeners();
    }

    private void setupRecyclerView() {
        posterAdapter = new MoviePosterAdapter(this); // "this" để xử lý click (dù không cần thiết)
        backgroundRecyclerView.setAdapter(posterAdapter);
        backgroundRecyclerView.setAlpha(0.3f); // Làm mờ RecyclerView nền
    }

    private void observeViewModel() {
        // Tải một danh sách phim bất kỳ (ví dụ: trending) để làm nền
        viewModel.getBackgroundMovies().observe(getViewLifecycleOwner(), movies -> {
            if (movies != null && !movies.isEmpty()) {
                // Lấy danh sách phim từ category đầu tiên
                posterAdapter.setMovies(movies);
            }
        });
    }

    private void setupClickListeners() {
        goToLoginButton.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_landingFragment_to_loginFragment)
        );
        moreOptions.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(requireContext(), v, Gravity.END);
            popup.getMenuInflater().inflate(R.menu.menu_overflow_landing, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.action_signup) {
                    // Điều hướng tới màn hình Đăng ký
                    Navigation.findNavController(v).navigate(R.id.action_landingFragment_to_registerFragment);
                    return true;
                }
                return false;
            });
            popup.show();
        });
    }

    // Implement interface, nhưng không cần làm gì vì chỉ là ảnh nền
    @Override
    public void onMovieClick(Movie movie) { }
}