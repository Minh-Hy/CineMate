package com.yourname.cinemate.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.yourname.cinemate.R;
import com.yourname.cinemate.data.model.Comment;
import com.yourname.cinemate.data.model.Movie;
import com.yourname.cinemate.utils.Constants;
import com.yourname.cinemate.viewmodel.DetailViewModel;
import java.util.stream.Collectors;

public class DetailFragment extends Fragment implements CommentAdapter.CommentInteractionListener {

    private DetailViewModel viewModel;
    private int movieId;

    // --- Khai báo các View ---
    private ImageView backdropImage;
    private TextView titleText, yearText, genresText, overviewText, watchlistButton, rateButton;
    private ProgressBar progressBar;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbar;
    private Button playTrailerButton;
    private RecyclerView commentsRecyclerView;
    private CommentAdapter commentAdapter;
    private EditText commentEditText;
    private ImageButton sendCommentButton;

    // Biến để quản lý trạng thái trả lời bình luận
    private Comment parentCommentToReply = null;
    // --- LOGIC CHO TRACKING VIEW HISTORY ---
    private Handler trackingHandler;
    private Runnable trackingRunnable;
    private static final long TRACKING_DELAY_MS = 30000; // 30 giây

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            movieId = getArguments().getInt("movieId");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(DetailViewModel.class);

        bindViews(view);
        setupToolbar();
        setupClickListeners();
        observeViewModel();

        // Kích hoạt tải toàn bộ dữ liệu
        viewModel.loadAllDataForMovie(movieId);
        // Khởi tạo handler
        trackingHandler = new Handler(Looper.getMainLooper());
    }

    private void bindViews(View view) {
        backdropImage = view.findViewById(R.id.image_backdrop);
        titleText = view.findViewById(R.id.text_title_detail);
        yearText = view.findViewById(R.id.text_year_detail);
        genresText = view.findViewById(R.id.text_genres_detail);
        overviewText = view.findViewById(R.id.text_overview_detail);
        toolbar = view.findViewById(R.id.toolbar_detail);
        collapsingToolbar = view.findViewById(R.id.collapsing_toolbar);
        progressBar = view.findViewById(R.id.progress_bar_detail);
        watchlistButton = view.findViewById(R.id.button_add_watchlist);
        rateButton = view.findViewById(R.id.button_rate);
        playTrailerButton = view.findViewById(R.id.button_play_trailer);
        commentsRecyclerView = view.findViewById(R.id.recycler_comments);
        commentEditText = view.findViewById(R.id.edit_text_comment);
        sendCommentButton = view.findViewById(R.id.button_send_comment);

        setupCommentsRecyclerView();
    }

    private void setupCommentsRecyclerView() {
        commentAdapter = new CommentAdapter(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        commentsRecyclerView.setLayoutManager(layoutManager);
        commentsRecyclerView.setAdapter(commentAdapter);
        commentsRecyclerView.setNestedScrollingEnabled(false);

        commentsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1)) {
                    viewModel.loadMoreComments(movieId);
                }
            }
        });
    }

    private void setupToolbar() {
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        if (((AppCompatActivity) requireActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> getParentFragmentManager().popBackStack());
    }

    // Gộp tất cả sự kiện click vào một nơi
    private void setupClickListeners() {
        watchlistButton.setOnClickListener(v -> viewModel.toggleWatchlistStatus(movieId));

        rateButton.setOnClickListener(v -> {
            Integer currentRating = viewModel.getUserRating().getValue();
            showRatingDialog(currentRating != null ? currentRating : 0);
        });

        sendCommentButton.setOnClickListener(v -> {
            String content = commentEditText.getText().toString().trim();
            if (!content.isEmpty()) {
                String parentId = (parentCommentToReply != null) ? parentCommentToReply.getId() : null;
                viewModel.postComment(movieId, content, parentId);
                commentEditText.setText("");
                resetReplyMode(); // Reset lại trạng thái sau khi gửi
            }
        });
    }

    private void observeViewModel() {
        viewModel.isLoading().observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.getMovieDetails().observe(getViewLifecycleOwner(), this::populateUi);
        viewModel.isMovieInWatchlist().observe(getViewLifecycleOwner(), this::updateWatchlistButton);
        viewModel.getUserRating().observe(getViewLifecycleOwner(), this::updateRateButton);

        viewModel.getToastMessage().observe(getViewLifecycleOwner(), event -> {
            String message = event.getContentIfNotHandled();
            if (message != null) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getComments().observe(getViewLifecycleOwner(), comments -> {
            if (comments != null) {
                commentAdapter.setComments(comments);
            }
        });

        viewModel.getNewComment().observe(getViewLifecycleOwner(), event -> {
            Comment newComment = event.getContentIfNotHandled();
            if (newComment != null) {
                if (newComment.getParentCommentId() == null) {
                    commentAdapter.addComment(newComment); // SỬA 2: Dùng đúng tên hàm
                    commentsRecyclerView.scrollToPosition(0);
                } else {
                    commentAdapter.addReplyToParent(newComment);
                }
            }
        });
    }

    // --- Các hàm implement interface của CommentAdapter ---
    @Override
    public void onReplyClicked(Comment parentComment) {
        parentCommentToReply = parentComment;
        commentEditText.setHint("Đang trả lời " + parentComment.getUser().getDisplayName() + "...");
        commentEditText.requestFocus();
        // Mở bàn phím
        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(commentEditText, InputMethodManager.SHOW_IMPLICIT);
    }

    @Override
    public void onLoadMoreReplies(String parentCommentId, int nextPage) {
        // TODO: Gọi ViewModel để tải thêm replies
        // viewModel.loadMoreReplies(movieId, parentCommentId, nextPage);
    }

    // Hàm để thoát khỏi chế độ trả lời
    private void resetReplyMode() {
        parentCommentToReply = null;
        commentEditText.setHint("Viết bình luận...");
        // Ẩn bàn phím
        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(commentEditText.getWindowToken(), 0);
    }


    private void updateRateButton(Integer rating) {
        if (rating != null && rating > 0) {
            // --- TRƯỜNG HỢP ĐÃ ĐÁNH GIÁ ---

            // Cập nhật Text
            rateButton.setText(String.format("Đã đánh giá: %d", rating));

            // Cập nhật Icon (sử dụng ngôi sao đã tô màu)
            // setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom)
            rateButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_star_on, 0, 0);

            // Cập nhật màu sắc để làm nổi bật
            rateButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.netflix_red));

        } else {
            // --- TRƯỜNG HỢP CHƯA ĐÁNH GIÁ ---

            // Cập nhật Text
            rateButton.setText("Đánh giá");

            // Cập nhật Icon (sử dụng ngôi sao chỉ có viền)
            rateButton.setCompoundDrawablesWithIntrinsicBounds(0, android.R.drawable.star_off, 0, 0);

            // Cập nhật màu sắc về mặc định
            rateButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.netflix_gray_light));
        }
    }

    private void updateWatchlistButton(boolean isInWatchlist) {
        if (isInWatchlist) {
            watchlistButton.setText("Đã thêm");
            watchlistButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_check, 0, 0);
            watchlistButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.netflix_red));
        } else {
            watchlistButton.setText("Danh sách");
            watchlistButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_add, 0, 0);
            watchlistButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.netflix_gray_light));
        }
    }

    private void showRatingDialog(int currentRating) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_rating, null);
        builder.setView(dialogView);

        final RatingBar ratingBar = dialogView.findViewById(R.id.rating_bar_dialog);
        Button cancelButton = dialogView.findViewById(R.id.button_cancel_rating);
        Button submitButton = dialogView.findViewById(R.id.button_submit_rating);
        ratingBar.setRating(currentRating);

        final AlertDialog dialog = builder.create();

        cancelButton.setOnClickListener(v -> dialog.dismiss());
        submitButton.setOnClickListener(v -> {
            int rating = (int) ratingBar.getRating();
            if (rating > 0) {
                viewModel.rateMovie(movieId, rating);
                dialog.dismiss();
            } else {
                Toast.makeText(getContext(), "Vui lòng chọn số sao", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }

    private String getYouTubeVideoId(String youtubeUrl) {
        String videoId = null;
        if (youtubeUrl != null && youtubeUrl.trim().length() > 0) {
            // Xử lý các dạng link YouTube khác nhau
            String pattern = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*";
            java.util.regex.Pattern compiledPattern = java.util.regex.Pattern.compile(pattern);
            java.util.regex.Matcher matcher = compiledPattern.matcher(youtubeUrl);
            if (matcher.find()) {
                videoId = matcher.group();
            }
        }
        return videoId;
    }

    private void populateUi(Movie movie) {
        if (movie == null) return;

        collapsingToolbar.setTitle(movie.getTitle());
        titleText.setText(movie.getTitle());

        if (movie.getReleaseDate() != null && !movie.getReleaseDate().isEmpty()) {
            yearText.setText(movie.getReleaseDate().substring(0, 4));
        }
        if (movie.getGenres() != null) {
            String genres = movie.getGenres().stream()
                    .map(g -> g.getName())
                    .collect(Collectors.joining(", "));
            genresText.setText(genres);
        }
        overviewText.setText(movie.getOverview());

        String backdropPath = movie.getBackdropPath();
        if (backdropPath != null && !backdropPath.isEmpty()) {
            String fullBackdropUrl = Constants.IMAGE_BASE_URL + "w780" + backdropPath;
            // 1. Định nghĩa bán kính bo góc mong muốn bằng đơn vị DP
            float cornerRadiusInDp = 16f;

            // 2. Chuyển đổi giá trị DP sang Pixel một cách chính xác
            int cornerRadiusInPx = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    cornerRadiusInDp,
                    getResources().getDisplayMetrics()
            );
            Glide.with(this).load(fullBackdropUrl).apply(new RequestOptions().transform(new RoundedCorners(cornerRadiusInPx))).placeholder(R.color.netflix_gray_light).into(backdropImage);
        }

        final String trailerUrl = movie.getTrailerUrl();
        if (trailerUrl != null && !trailerUrl.isEmpty()) {
            playTrailerButton.setVisibility(View.VISIBLE);

            playTrailerButton.setOnClickListener(v -> {
                // Lấy Video ID từ URL (để tạo link chuẩn cho app YouTube)
                String videoId = getYouTubeVideoId(trailerUrl);

                if (videoId != null) {
                    // Tạo Intent để mở app YouTube
                    Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoId));

                    // Tạo Intent dự phòng để mở trình duyệt (nếu máy không có app YouTube)
                    Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + videoId));

                    try {
                        // Thử mở bằng app YouTube trước
                        startActivity(appIntent);
                    } catch (Exception ex) {
                        // Nếu lỗi (không có app), mở bằng trình duyệt
                        startActivity(webIntent);
                    }
                } else {
                    // Trường hợp link trailer không phải dạng chuẩn YouTube, mở trực tiếp link đó bằng trình duyệt
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(trailerUrl));
                    startActivity(browserIntent);
                }
            });
        } else {
            playTrailerButton.setVisibility(View.GONE);
        }
    }
    // onResume() được gọi khi Fragment trở nên sichtbar và tương tác được
    @Override
    public void onResume() {
        super.onResume();
        startViewTrackingTimer();
    }

    // onPause() được gọi khi người dùng rời khỏi Fragment (ví dụ: nhấn Back)
    @Override
    public void onPause() {
        super.onPause();
        cancelViewTrackingTimer();
    }

    // Hàm để bắt đầu đếm giờ
    private void startViewTrackingTimer() {
        // Tạo một tác vụ sẽ được thực thi sau 15 giây
        trackingRunnable = () -> {
            Log.d("DetailFragment", "15 seconds passed. Tracking view for movieId: " + movieId);
            viewModel.trackMovieView(movieId);
        };
        // Lên lịch cho tác vụ
        trackingHandler.postDelayed(trackingRunnable, TRACKING_DELAY_MS);
    }

    // Hàm để hủy bộ đếm giờ
    private void cancelViewTrackingTimer() {
        if (trackingHandler != null && trackingRunnable != null) {
            trackingHandler.removeCallbacks(trackingRunnable);
            Log.d("DetailFragment", "View tracking timer cancelled.");
        }
    }
}