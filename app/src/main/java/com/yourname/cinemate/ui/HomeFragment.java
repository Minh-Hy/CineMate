package com.yourname.cinemate.ui;

import android.content.Intent;
import android.net.Uri;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
    private SwipeRefreshLayout swipeRefreshLayout;



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
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_home);
        retryButton.setOnClickListener(v -> viewModel.retryFetch());

        // Thiết lập Toolbar
        setupToolbar(view);

        setupRecyclerView();
        setupSwipeRefresh();
        retryButton.setOnClickListener(v-> viewModel.retryFetch());
        observeViewModel();


    }
    private void setupSwipeRefresh() {
        // Đặt màu cho vòng xoay (màu đỏ Netflix) để đẹp hơn
        swipeRefreshLayout.setColorSchemeResources(R.color.netflix_red);

        // Bắt sự kiện khi người dùng kéo xuống
        swipeRefreshLayout.setOnRefreshListener(() -> {
            // Gọi hàm trong ViewModel để tải lại toàn bộ dữ liệu
            viewModel.retryFetch();
        });
    }

    private void setupToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar_home);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
    }

    private void setupRecyclerView() {
        categoryAdapter = new MovieCategoryAdapter(this);
        parentRecyclerView.setAdapter(categoryAdapter);
    }

    private void observeViewModel() {
        // Quan sát trạng thái Loading
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                // Nếu đang tải và KHÔNG PHẢI do đang vuốt (ví dụ lần đầu mở app)
                // thì hiện ProgressBar giữa màn hình.
                // Nếu đang vuốt (isRefreshing = true) thì vòng xoay của SwipeLayout đã hiện rồi.
                if (!swipeRefreshLayout.isRefreshing()) {
                    progressBar.setVisibility(View.VISIBLE);
                }
                errorLayout.setVisibility(View.GONE);
            } else {
                // Tải xong: Ẩn tất cả các biểu tượng loading
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false); // QUAN TRỌNG: Tắt vòng xoay
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), event -> {
            String error = event.getContentIfNotHandled();
            if (error != null) {
                // Nếu đang vuốt mà lỗi thì tắt vòng xoay đi
                swipeRefreshLayout.setRefreshing(false);
                progressBar.setVisibility(View.GONE);

                // Nếu danh sách đang trống thì hiện màn hình lỗi
                if (categoryAdapter.getItemCount() == 0) {
                    errorLayout.setVisibility(View.VISIBLE);
                    parentRecyclerView.setVisibility(View.GONE);
                } else {
                    // Nếu đã có phim rồi mà load lại bị lỗi thì chỉ hiện Toast
                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                }
            }
        });

        viewModel.getMovieCategories().observe(getViewLifecycleOwner(), categories -> {
            if (categories != null && !categories.isEmpty()) {
                parentRecyclerView.setVisibility(View.VISIBLE);
                errorLayout.setVisibility(View.GONE);
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
    public void onMoviePlayClick(Movie movie){
        if (movie == null) return;

        String trailerUrl = movie.getTrailerUrl();
        String videoId = getYouTubeVideoId(trailerUrl); // Sử dụng cùng hàm tiện ích như trên
        Log.e("TrailerCheck", "Original URL: " + trailerUrl);
        Log.e("TrailerCheck", "Extracted ID: " + videoId);
        Log.e("TrailerCheck", "Final Link: http://www.youtube.com/watch?v=" + videoId);

        if (videoId != null) {
            Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoId));
            Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + videoId));
            try {
                startActivity(appIntent);
            } catch (Exception ex) {
                startActivity(webIntent);
            }
        } else if (trailerUrl != null && !trailerUrl.isEmpty()) {
            // Fallback: Mở link trực tiếp nếu không tách được ID
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(trailerUrl));
            startActivity(browserIntent);
        } else {
            Toast.makeText(getContext(), "Phim này hiện chưa có trailer", Toast.LENGTH_SHORT).show();
        }
    }
    private String getYouTubeVideoId(String youtubeUrl) {
        String videoId = null;
        if (youtubeUrl != null && youtubeUrl.trim().length() > 0) {
            String pattern = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*";
            java.util.regex.Pattern compiledPattern = java.util.regex.Pattern.compile(pattern);
            java.util.regex.Matcher matcher = compiledPattern.matcher(youtubeUrl);
            if (matcher.find()) {
                videoId = matcher.group();
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
        } else if (itemId == R.id.action_notifications) {
        Navigation.findNavController(requireView()).navigate(R.id.action_homeFragment_to_notificationFragment);
        return true;
    }
        return super.onOptionsItemSelected(item);
    }
}