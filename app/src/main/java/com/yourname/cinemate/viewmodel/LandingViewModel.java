package com.yourname.cinemate.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.yourname.cinemate.data.model.Movie;
import com.yourname.cinemate.data.repository.MovieRepository;
import java.util.List;

public class LandingViewModel extends ViewModel {

    private final MovieRepository movieRepository;

    // LiveData chỉ chứa danh sách phim nền
    private final MutableLiveData<List<Movie>> _backgroundMovies = new MutableLiveData<>();
    public LiveData<List<Movie>> getBackgroundMovies() { return _backgroundMovies; }

    public LandingViewModel() {
        this.movieRepository = new MovieRepository();
        fetchBackgroundMovies();
    }

    private void fetchBackgroundMovies() {
        // Chỉ gọi một API public duy nhất, ví dụ "trending"
        movieRepository.getPopularMovies().observeForever(movies -> {
            if (movies != null) {
                _backgroundMovies.setValue(movies);
            }
        });
    }
}