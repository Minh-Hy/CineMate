package com.yourname.cinemate.viewmodel;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.yourname.cinemate.data.model.Movie;
import com.yourname.cinemate.data.model.MovieCategory;
import com.yourname.cinemate.data.repository.MovieRepository;
import com.yourname.cinemate.utils.Event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class HomeViewModel extends ViewModel {

    private final MovieRepository movieRepository;
    // Đổi từ MutableLiveData -> MediatorLiveData
    private final MediatorLiveData<List<MovieCategory>> movieCategories = new MediatorLiveData<>();
    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>();
    private final MutableLiveData<Event<String>> _error = new MutableLiveData<>();
    public LiveData<Event<String>> getError() { return _error; }

    // 2) Holder đếm & giữ kết quả
    class ApiResultHolder {
        List<Movie> topRated, forYou, trending, discover;
        int completed = 0;
        final int total = 4;
    }
    public HomeViewModel() {
        movieRepository = new MovieRepository();
        fetchMovieCategories();
    }

    public LiveData<List<MovieCategory>> getMovieCategories() { return movieCategories; }
    public LiveData<Boolean> getIsLoading() { return _isLoading; }

    public void retryFetch() { fetchMovieCategories(); }

    private void fetchMovieCategories() {
        _isLoading.setValue(true);

        // 1) Lấy 4 nguồn
        LiveData<List<Movie>> topRatedSource = movieRepository.getTopRatedMovies();
        LiveData<List<Movie>> forYouSource   = movieRepository.getMoviesForYou();
        LiveData<List<Movie>> trendingSource = movieRepository.getPopularMovies();
        LiveData<List<Movie>> discoverSource = movieRepository.getRandomMovies();


        final ApiResultHolder results = new ApiResultHolder();

        // 3) Đăng ký 4 nguồn vào CHÍNH movieCategories (vốn là MediatorLiveData và đang được Fragment observe)
        movieCategories.addSource(topRatedSource, movies -> {
            results.topRated = movies;
            if (++results.completed == results.total) onAllApisDone(results, topRatedSource, forYouSource, trendingSource, discoverSource);
        });
        movieCategories.addSource(forYouSource, movies -> {
            results.forYou = movies;
            if (++results.completed == results.total) onAllApisDone(results, topRatedSource, forYouSource, trendingSource, discoverSource);
        });
        movieCategories.addSource(trendingSource, movies -> {
            results.trending = movies;
            if (++results.completed == results.total) onAllApisDone(results, topRatedSource, forYouSource, trendingSource, discoverSource);
        });
        movieCategories.addSource(discoverSource, movies -> {
            results.discover = movies;
            if (++results.completed == results.total) onAllApisDone(results, topRatedSource, forYouSource, trendingSource, discoverSource);
        });
    }

    // 4) Khi đủ 4 API xong: removeSource rồi xử lý
    private void onAllApisDone(ApiResultHolder r,
                               LiveData<List<Movie>> top, LiveData<List<Movie>> you,
                               LiveData<List<Movie>> trend, LiveData<List<Movie>> disc) {

        movieCategories.removeSource(top);
        movieCategories.removeSource(you);
        movieCategories.removeSource(trend);
        movieCategories.removeSource(disc);

        processAndBuildCategories(r.topRated, r.forYou, r.trending, r.discover);
    }

    private void processAndBuildCategories(List<Movie> topRatedMovies, List<Movie> forYouMovies,
                                           List<Movie> trendingMovies, List<Movie> discoverMovies) {

        if (topRatedMovies == null || forYouMovies == null || trendingMovies == null || discoverMovies == null) {
            _error.setValue(new Event<>("Không thể tải toàn bộ danh sách phim."));
            _isLoading.setValue(false);
            return;
        }

        Movie bestMovieForBanner = findBestMovieForBanner(topRatedMovies);

        if (bestMovieForBanner != null) {
            // ✅ DÙNG addSource CHÍNH TRÊN movieCategories (Mediator) cho call chi tiết banner
            LiveData<Movie> bannerSource = movieRepository.getMovieById(bestMovieForBanner.getId());
            movieCategories.addSource(bannerSource, detailed -> {
                movieCategories.removeSource(bannerSource); // one-shot
                _isLoading.setValue(false);

                if (detailed != null) {
                    buildCategoryList(detailed, topRatedMovies, forYouMovies, trendingMovies, discoverMovies);
                } else {
                    _error.setValue(new Event<>("Lỗi khi tải thông tin phim nổi bật."));
                    buildCategoryList(null, topRatedMovies, forYouMovies, trendingMovies, discoverMovies);
                }
            });
        } else {
            buildCategoryList(null, topRatedMovies, forYouMovies, trendingMovies, discoverMovies);
            _isLoading.setValue(false);
        }
    }

    private void buildCategoryList(@Nullable Movie bannerMovie, List<Movie> topRated,
                                   List<Movie> forYou, List<Movie> trending, List<Movie> discover) {
        ArrayList<MovieCategory> categories = new ArrayList<>();
        if (bannerMovie != null) {
            categories.add(new MovieCategory("Banner", Collections.singletonList(bannerMovie)));
        }
        categories.add(new MovieCategory("Thịnh Hành Nhất", trending));
        categories.add(new MovieCategory("Gợi ý cho bạn", forYou));
        categories.add(new MovieCategory("Đánh giá cao", topRated));
        categories.add(new MovieCategory("Khám phá", discover));

        // setValue NGAY TRÊN MediatorLiveData
        movieCategories.setValue(categories);
    }

    private Movie findBestMovieForBanner(List<Movie> movies) {
        if (movies == null || movies.isEmpty()) return null;
        return movies.stream()
                .filter(m -> m.getBackdropPath() != null && !m.getBackdropPath().isEmpty())
                .max(Comparator.comparingDouble(Movie::getVoteAverage))
                .orElse(null);
    }
}
