package com.yourname.cinemate.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu; // THÊM IMPORT NÀY
import android.view.MenuInflater; // THÊM IMPORT NÀY
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import com.yourname.cinemate.R;
import com.yourname.cinemate.data.model.Movie;
import com.yourname.cinemate.viewmodel.HomeViewModel;

public class HomeFragment extends Fragment implements MovieBannerAdapter.OnMovieClickListener {

    private HomeViewModel viewModel;
    private RecyclerView parentRecyclerView;
    private MovieCategoryAdapter categoryAdapter;
    private ProgressBar progressBar;
    private LinearLayout errorLayout;
    private Button retryButton;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); // Báo cho hệ thống rằng Fragment này có menu
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // Ánh xạ các views
        parentRecyclerView = view.findViewById(R.id.recycler_category_parent);
        progressBar = view.findViewById(R.id.progress_bar_home);
        errorLayout = view.findViewById(R.id.layout_error_home);
        retryButton = view.findViewById(R.id.button_retry_home);

        retryButton.setOnClickListener(v -> viewModel.retryFetch());

        // Thiết lập Toolbar
        setupToolbar(view);

        setupRecyclerView();
        observeViewModel();
    }

    private void setupToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar_home);
        // Đặt Toolbar này làm ActionBar cho Activity
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
    }

    private void setupRecyclerView() {
        categoryAdapter = new MovieCategoryAdapter(this);
        parentRecyclerView.setAdapter(categoryAdapter);
    }

    private void observeViewModel() {
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });
        viewModel.getError().observe(getViewLifecycleOwner(), event -> {
            String message = event.getContentIfNotHandled();
            if (message != null) {
                parentRecyclerView.setVisibility(View.GONE);
                errorLayout.setVisibility(View.VISIBLE);
                // có thể set text cho TextView lỗi ở đây
            }
        });
        viewModel.getMovieCategories().observe(getViewLifecycleOwner(), categories -> {
            if (categories != null && !categories.isEmpty()) {
                errorLayout.setVisibility(View.GONE); // Ẩn lỗi nếu có dữ liệu
                parentRecyclerView.setVisibility(View.VISIBLE);
                categoryAdapter.setCategories(categories);
            }
        });
    }

    @Override
    public void onMovieInfoClick(Movie movie) {
        if (movie == null) return;

        Log.d("HomeFragment", "Navigating to Detail for movie: " + movie.getTitle());
        Bundle bundle = new Bundle();
        bundle.putInt("movieId", movie.getId());

        try {
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                    .navigate(R.id.action_homeFragment_to_detailFragment, bundle);
        } catch (IllegalArgumentException e) {
            Log.e("HomeFragment", "Navigation to Detail failed!", e);
            Toast.makeText(getContext(), "Không thể mở phim lúc này.", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onMoviePlayClick(Movie movie) {
        if (movie == null) return;

        Log.d("HomeFragment", "Play button clicked for movie: " + movie.getTitle());
        String trailerUrl = movie.getTrailerUrl();
        String videoId = getYouTubeVideoId(trailerUrl); // Cần hàm tiện ích này

        if (videoId != null && !videoId.isEmpty()) {
            Intent intent = new Intent(requireContext(), PlayerActivity.class);
            intent.putExtra(PlayerActivity.EXTRA_VIDEO_ID, videoId);
            startActivity(intent);
        } else {
            Toast.makeText(getContext(), "Phim này hiện chưa có trailer", Toast.LENGTH_SHORT).show();
        }
    }
    private String getYouTubeVideoId(String youtubeUrl) {
        if (youtubeUrl == null || !youtubeUrl.trim().contains("v=")) {
            return null;
        }
        String videoId = null;
        String[] urlParts = youtubeUrl.split("v=");
        if (urlParts.length > 1) {
            videoId = urlParts[1];
            int ampersandPosition = videoId.indexOf('&');
            if (ampersandPosition != -1) {
                videoId = videoId.substring(0, ampersandPosition);
            }
        }
        return videoId;
    }

    // --- PHƯƠNG THỨC CÒN THIẾU ---
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // "Thổi phồng" (inflate) file menu của chúng ta vào đối tượng Menu
        inflater.inflate(R.menu.home_menu, menu);
    }

    // --- PHƯƠNG THỨC XỬ LÝ CLICK (Đã có sẵn trong code của bạn, không cần sửa) ---
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_search) {
            // Điều hướng sang SearchFragment
            Navigation.findNavController(requireView()).navigate(R.id.action_homeFragment_to_searchFragment);
            return true;
        } else if (itemId == R.id.action_profile) {
            // Điều hướng sang ProfileFragment
            Navigation.findNavController(requireView()).navigate(R.id.action_homeFragment_to_profileFragment);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}