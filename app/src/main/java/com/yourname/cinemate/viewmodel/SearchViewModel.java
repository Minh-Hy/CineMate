package com.yourname.cinemate.viewmodel;

import android.os.Handler;
import android.os.Looper;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.yourname.cinemate.data.model.Movie;
import com.yourname.cinemate.data.repository.MovieRepository;
import java.util.ArrayList;
import java.util.List;

public class SearchViewModel extends ViewModel {

    private final MovieRepository movieRepository;

    private final MutableLiveData<List<Movie>> _searchResults = new MutableLiveData<>();
    public LiveData<List<Movie>> getSearchResults() { return _searchResults; }

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    public LiveData<Boolean> isLoading() { return _isLoading; }

    // --- LOGIC MỚI CHO DEBOUNCE ---
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    private static final long DEBOUNCE_DELAY = 500; // 500ms delay

    public SearchViewModel() {
        this.movieRepository = new MovieRepository();
    }

    /**
     * Kích hoạt việc tìm kiếm phim với cơ chế Debounce.
     * @param query Từ khóa tìm kiếm từ người dùng.
     */
    public void search(String query) {
        final String trimmedQuery = query.trim();

        // 1. Hủy bỏ bất kỳ yêu cầu tìm kiếm nào đang chờ xử lý
        handler.removeCallbacks(searchRunnable);

        // 2. Tạo một yêu cầu tìm kiếm mới (Runnable)
        searchRunnable = () -> {
            if (trimmedQuery.isEmpty()) {
                _searchResults.setValue(null);
                return;
            }

            _isLoading.setValue(true);
            movieRepository.searchMovies(trimmedQuery).observeForever(movies -> {
                _isLoading.setValue(false);
                if (movies != null) {
                    _searchResults.setValue(movies);
                } else {
                    _searchResults.setValue(new ArrayList<>());
                }
            });
        };

        // 3. Lên lịch để thực thi yêu cầu tìm kiếm sau một khoảng trễ
        handler.postDelayed(searchRunnable, DEBOUNCE_DELAY);
    }
}