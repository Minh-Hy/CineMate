package com.yourname.cinemate.data.repository;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import android.util.Log; // Thêm import Log

import com.yourname.cinemate.data.model.Comment;
import com.yourname.cinemate.data.model.CreateCommentDto;
import com.yourname.cinemate.data.model.CreateRatingDto;
import com.yourname.cinemate.data.model.Movie;
import com.yourname.cinemate.data.model.PaginatedComments;
import com.yourname.cinemate.data.model.PaginatedMovies;
import com.yourname.cinemate.data.model.Rating;
import com.yourname.cinemate.data.model.ShareLinks;
import com.yourname.cinemate.data.remote.ApiService;
import com.yourname.cinemate.data.remote.RetrofitClient;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieRepository {
    private final ApiService apiService;

    public MovieRepository() {
        this.apiService = RetrofitClient.getApiService();
    }

    public LiveData<List<Movie>> getPopularMovies() {
        final MutableLiveData<List<Movie>> data = new MutableLiveData<>();
        Log.d("RepoCheck", "Requesting /trending..."); // Log trước khi gọi
        apiService.getPopularMovies().enqueue(new Callback<List<Movie>>() {
            @Override
            public void onResponse(Call<List<Movie>> call, Response<List<Movie>> response) {
                Log.d("RepoCheck", "/trending FINISHED. Success: " + response.isSuccessful()); // Log khi có response
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }
            @Override
            public void onFailure(Call<List<Movie>> call, Throwable t) {
                Log.e("MovieRepository", "API call failed for getTrendingMovies", t);
                data.setValue(null);
            }
        });
        return data;
    }

    // THÊM PHƯƠNG THỨC MỚI NÀY VÀO
    public LiveData<List<Movie>> getTopRatedMovies() {
        final MutableLiveData<List<Movie>> data = new MutableLiveData<>();
        Log.d("RepoCheck", "Requesting /toprated..."); // Log trước khi gọi

        apiService.getTopRatedMovies().enqueue(new Callback<List<Movie>>() {
            @Override
            public void onResponse(Call<List<Movie>> call, Response<List<Movie>> response) {
                Log.d("RepoCheck", "/toprated FINISHED. Success: " + response.isSuccessful()); // Log khi có response
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    // Nếu lỗi (ví dụ 401 Unauthorized), ta cũng set null
                    data.setValue(null);
                }
            }
            @Override
            public void onFailure(Call<List<Movie>> call, Throwable t) {
                Log.e("MovieRepository", "API call failed for getForYouMovies", t);
                data.setValue(null);
            }
        });
        return data;
    }
    public LiveData<List<Movie>> getMoviesForYou() {
        final MutableLiveData<List<Movie>> data = new MutableLiveData<>();
        Log.d("RepoCheck", "Requesting /for you..."); // Log trước khi gọi

        apiService.getMoviesForYou().enqueue(new Callback<List<Movie>>() {
            @Override
            public void onResponse(Call<List<Movie>> call, Response<List<Movie>> response) {
                Log.d("RepoCheck", "/for you FINISHED. Success: " + response.isSuccessful()); // Log khi có response
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    // Nếu lỗi (ví dụ 401 Unauthorized), ta cũng set null
                    data.setValue(null);
                }
            }
            @Override
            public void onFailure(Call<List<Movie>> call, Throwable t) {
                Log.e("MovieRepository", "API call failed for getForYouMovies", t);
                data.setValue(null);
            }
        });
        return data;
    }
    public LiveData<List<Movie>> getRandomMovies() {
        final MutableLiveData<List<Movie>> data = new MutableLiveData<>();
        Log.d("RepoCheck", "Requesting /discover..."); // Log trước khi gọi

        apiService.getRandomMovies().enqueue(new Callback<List<Movie>>() {
            @Override
            public void onResponse(Call<List<Movie>> call, Response<List<Movie>> response) {
                Log.d("RepoCheck", "/discover FINISHED. Success: " + response.isSuccessful()); // Log khi có response
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    // Nếu lỗi (ví dụ 401 Unauthorized), ta cũng set null
                    data.setValue(null);
                }
            }
            @Override
            public void onFailure(Call<List<Movie>> call, Throwable t) {
                Log.e("MovieRepository", "API call failed for getForYouMovies", t);
                data.setValue(null);
            }
        });
        return data;
    }



    public LiveData<Movie> getMovieById(int movieId) {
        final MutableLiveData<Movie> data = new MutableLiveData<>();
        apiService.getMovieById(movieId).enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }
            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                Log.e("MovieRepository", "API call failed for getMovieById", t);
                data.setValue(null);
            }
        });
        return data;
    }

    public LiveData<List<Movie>> getUserWatchlist() { // <-- SỬA 1: Kiểu trả về là LiveData<List<Movie>>
        final MutableLiveData<List<Movie>> data = new MutableLiveData<>(); // <-- SỬA 2: Kiểu LiveData là List<Movie>
        apiService.getWatchlist().enqueue(new Callback<List<Movie>>() { // <-- SỬA 3: Kiểu Callback là List<Movie>
            @Override
            public void onResponse(Call<List<Movie>> call, Response<List<Movie>> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }
            @Override
            public void onFailure(Call<List<Movie>> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }


    public LiveData<Boolean> addToWatchlist(int movieId) {
        final MutableLiveData<Boolean> success = new MutableLiveData<>();
        apiService.addToWatchlist(movieId).enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                success.setValue(response.isSuccessful());
            }
            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                success.setValue(false);
            }
        });
        return success;
    }

    public LiveData<Boolean> removeFromWatchlist(int movieId) {
        final MutableLiveData<Boolean> success = new MutableLiveData<>();
        apiService.removeFromWatchlist(movieId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                success.setValue(response.isSuccessful());
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                success.setValue(false);
            }
        });
        return success;
    }

    public LiveData<Boolean> rateMovie(int movieId, int rating) {
        final MutableLiveData<Boolean> success = new MutableLiveData<>();
        CreateRatingDto ratingDto = new CreateRatingDto(rating);

        apiService.rateMovie(movieId, ratingDto).enqueue(new Callback<Rating>() {
            @Override
            public void onResponse(Call<Rating> call, Response<Rating> response) {
                success.setValue(response.isSuccessful());
            }

            @Override
            public void onFailure(Call<Rating> call, Throwable t) {
                Log.e("MovieRepository", "API call failed for rateMovie", t);
                success.setValue(false);
            }
        });
        return success;
    }

    public LiveData<PaginatedComments> getMovieComments(int movieId, int page, int limit) {
        final MutableLiveData<PaginatedComments> data = new MutableLiveData<>();
        apiService.getMovieComments(movieId, page, limit).enqueue(new Callback<PaginatedComments>() {
            @Override
            public void onResponse(Call<PaginatedComments> call, Response<PaginatedComments> response) {
                data.setValue(response.isSuccessful() ? response.body() : null);
            }
            @Override
            public void onFailure(Call<PaginatedComments> call, Throwable t) {
                Log.e("MovieRepository", "API call failed for getMovieComments", t);
                data.setValue(null);
            }
        });
        return data;
    }

    public LiveData<PaginatedComments> getCommentReplies(int movieId, String commentId, int page, int limit) {
        final MutableLiveData<PaginatedComments> data = new MutableLiveData<>();
        apiService.getCommentReplies(movieId, commentId, page, limit).enqueue(new Callback<PaginatedComments>() {
            @Override
            public void onResponse(Call<PaginatedComments> call, Response<PaginatedComments> response) {
                data.setValue(response.isSuccessful() ? response.body() : null);
            }
            @Override
            public void onFailure(Call<PaginatedComments> call, Throwable t) {
                Log.e("MovieRepository", "API call failed for getCommentReplies", t);
                data.setValue(null);
            }
        });
        return data;
    }

    public LiveData<Comment> createComment(int movieId, String content, @Nullable String parentCommentId) {
        final MutableLiveData<Comment> data = new MutableLiveData<>();
        CreateCommentDto commentDto = new CreateCommentDto(content, parentCommentId);
        apiService.createComment(movieId, commentDto).enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(Call<Comment> call, Response<Comment> response) {
                data.setValue(response.isSuccessful() ? response.body() : null);
            }
            @Override
            public void onFailure(Call<Comment> call, Throwable t) {
                Log.e("MovieRepository", "API call failed for createComment", t);
                data.setValue(null);
            }
        });
        return data;
    }

    public LiveData<List<Movie>> searchMovies(String query) {
        final MutableLiveData<List<Movie>> data = new MutableLiveData<>();

        apiService.searchMovies(query).enqueue(new Callback<PaginatedMovies>() {
            @Override
            public void onResponse(Call<PaginatedMovies> call, Response<PaginatedMovies> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Movie> movies = response.body().getData();
                    if (movies != null) {
                        data.setValue(movies);
                    } else {
                        data.setValue(new ArrayList<>());                 }
                } else {
                    data.setValue(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<PaginatedMovies> call, Throwable t) {
                Log.e("MovieRepository", "API call failed for searchMovies", t);
                data.setValue(null); // Trả về null khi có lỗi mạng
            }
        });
        return data;
    }
    public void trackMovieView(int movieId) {
        apiService.trackView(movieId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("MovieRepository", "Successfully tracked view for movieId: " + movieId);
                } else {
                    Log.w("MovieRepository", "Failed to track view for movieId: " + movieId);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("MovieRepository", "API call failed for trackMovieView", t);
            }
        });
    }
    public LiveData<ShareLinks> getShareLinks(int movieId) {
        final MutableLiveData<ShareLinks> data = new MutableLiveData<>();

        apiService.getShareLinks(movieId).enqueue(new Callback<ShareLinks>() {
            @Override
            public void onResponse(Call<ShareLinks> call, Response<ShareLinks> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ShareLinks> call, Throwable t) {
                Log.e("MovieRepository", "API call failed for getShareLinks", t);
                data.setValue(null);
            }
        });
        return data;
    }
}