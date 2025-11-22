package com.yourname.cinemate.viewmodel;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.yourname.cinemate.data.model.Comment;
import com.yourname.cinemate.data.model.Movie;
import com.yourname.cinemate.data.repository.MovieRepository;
import com.yourname.cinemate.utils.Event;

import java.util.ArrayList;
import java.util.List;

public class DetailViewModel extends ViewModel {

    private final MovieRepository movieRepository;

    // LiveData cho trạng thái loading chính
    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>();
    public LiveData<Boolean> isLoading() { return _isLoading; }
    //LiveData cho Rating
    private final MutableLiveData<Integer> _userRating = new MutableLiveData<>(0); // Đổi thành Integer, mặc định là 0
    public LiveData<Integer> getUserRating() { return _userRating; } // Đổi thành Integer

    // LiveData cho chi tiết phim
    private final MutableLiveData<Movie> _movieDetails = new MutableLiveData<>();
    public LiveData<Movie> getMovieDetails() { return _movieDetails; }

    // LiveData cho trạng thái watchlist
    private final MutableLiveData<Boolean> _isMovieInWatchlist = new MutableLiveData<>(false);
    public LiveData<Boolean> isMovieInWatchlist() { return _isMovieInWatchlist; }

    // LiveData cho thông báo (Toast)
    private final MutableLiveData<Event<String>> _toastMessage = new MutableLiveData<>();
    public LiveData<Event<String>> getToastMessage() { return _toastMessage; }

    // --- Logic cho Bình luận ---
    private static final int COMMENT_LIMIT = 20;
    private int currentCommentPage = 1;
    private boolean isLoadingComments = false;
    private boolean isLastCommentPage = false;

    private final MutableLiveData<List<Comment>> _comments = new MutableLiveData<>();
    public LiveData<List<Comment>> getComments() { return _comments; }

    private final MutableLiveData<Event<Comment>> _newComment = new MutableLiveData<>();
    public LiveData<Event<Comment>> getNewComment() { return _newComment; }

    // Constructor
    public DetailViewModel() {
        this.movieRepository = new MovieRepository();
    }

    /**
     * Phương thức chính để bắt đầu tải TẤT CẢ dữ liệu cho màn hình chi tiết.
     * Sẽ được gọi mỗi khi Fragment được tạo.
     */
    public void loadAllDataForMovie(int movieId) {
        _isLoading.setValue(true);

        // Tải đồng thời chi tiết phim, trạng thái watchlist, và trang bình luận đầu tiên
        loadMovieDetails(movieId);
        loadInitialComments(movieId);
    }

    private void loadMovieDetails(int movieId) {
        movieRepository.getMovieById(movieId).observeForever(movie -> {
            _isLoading.setValue(false);
            if (movie != null) {
                _userRating.setValue(movie.getUserRating());
                _movieDetails.setValue(movie);
                // **** CẬP NHẬT TRẠNG THÁI WATCHLIST TỪ DỮ LIỆU PHIM ****
                _isMovieInWatchlist.setValue(movie.isInWatchlist());
            } else {
                _toastMessage.setValue(new Event<>("Lỗi khi tải chi tiết phim"));
            }
        });
    }

    private void loadInitialComments(int movieId) {
        // Reset trạng thái phân trang
        currentCommentPage = 1;
        isLastCommentPage = false;
        isLoadingComments = false;
        _comments.setValue(new ArrayList<>()); // Xóa danh sách bình luận cũ

        loadMoreComments(movieId);
    }

    public void loadMoreComments(int movieId) {
        if (isLoadingComments || isLastCommentPage) return;
        isLoadingComments = true;

        movieRepository.getMovieComments(movieId, currentCommentPage, COMMENT_LIMIT).observeForever(paginated -> {
            isLoadingComments = false;
            if (paginated != null && paginated.getComments() != null) {
                if (paginated.getComments().isEmpty() || paginated.getComments().size() < COMMENT_LIMIT) {
                    isLastCommentPage = true;
                }
                List<Comment> currentList = _comments.getValue();
                if (currentList != null) {
                    currentList.addAll(paginated.getComments());
                    _comments.setValue(currentList); // Trigger observer để cập nhật UI
                }
                currentCommentPage++;
            } else {
                _toastMessage.setValue(new Event<>("Lỗi khi tải thêm bình luận"));
            }
        });
    }

    public void postComment(int movieId, String content, @Nullable String parentCommentId) {
        movieRepository.createComment(movieId, content, parentCommentId).observeForever(newComment -> {
            if (newComment != null) {
                _newComment.setValue(new Event<>(newComment));
                _toastMessage.setValue(new Event<>("Đăng bình luận thành công!"));
            } else {
                _toastMessage.setValue(new Event<>("Lỗi: Không thể đăng bình luận"));
            }
        });
    }

    public void toggleWatchlistStatus(int movieId) {
        Boolean isInWatchlist = _isMovieInWatchlist.getValue();
        if (isInWatchlist != null && isInWatchlist) {
            movieRepository.removeFromWatchlist(movieId).observeForever(success -> {
                if (success) {
                    _isMovieInWatchlist.setValue(false); // Cập nhật trạng thái ngay lập tức
                    _toastMessage.setValue(new Event<>("Đã xóa khỏi Danh sách xem"));
                } else {
                    _toastMessage.setValue(new Event<>("Lỗi: Không thể xóa"));
                }
            });
        } else {
            movieRepository.addToWatchlist(movieId).observeForever(success -> {
                if (success) {
                    _isMovieInWatchlist.setValue(true); // Cập nhật trạng thái ngay lập tức
                    _toastMessage.setValue(new Event<>("Đã thêm vào Danh sách xem"));
                } else {
                    _toastMessage.setValue(new Event<>("Lỗi: Không thể thêm"));
                }
            });
        }
    }

    public void rateMovie(int movieId, int rating) { // Đổi thành int
        movieRepository.rateMovie(movieId, rating).observeForever(success -> {
            if (success) {
                _userRating.setValue(rating);
                _toastMessage.setValue(new Event<>("Cảm ơn bạn đã đánh giá!"));
            } else {
                _toastMessage.setValue(new Event<>("Lỗi: Không thể gửi đánh giá"));
            }
        });
    }
    public void trackMovieView(int movieId) {
        movieRepository.trackMovieView(movieId);
    }
}