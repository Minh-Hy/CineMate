package com.yourname.cinemate.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.yourname.cinemate.R;
import com.yourname.cinemate.data.model.Movie;
import com.yourname.cinemate.utils.Constants;
import java.util.ArrayList;
import java.util.List;

public class MovieBannerAdapter extends RecyclerView.Adapter<MovieBannerAdapter.PosterViewHolder> {
    public interface OnMovieClickListener {
        void onMovieInfoClick(Movie movie);
        void onMoviePlayClick(Movie movie);
    }
    private List<Movie> movies = new ArrayList<>();
    private final OnMovieClickListener listener;
    public MovieBannerAdapter(OnMovieClickListener listener) {
        this.listener = listener;
    }
    public void setMovies(List<Movie> movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }
    @NonNull @Override
    public PosterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie_poster, parent, false);
        return new PosterViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull PosterViewHolder holder, int position) {
        holder.bind(movies.get(position), listener);
    }
    @Override public int getItemCount() { return movies.size(); }
    static class PosterViewHolder extends RecyclerView.ViewHolder {
        ImageView posterImage;
        public PosterViewHolder(@NonNull View itemView) {
            super(itemView);
            posterImage = itemView.findViewById(R.id.image_view_poster);
        }
        public void bind(final Movie movie, final OnMovieClickListener listener) {
            String posterPath = movie.getPosterPath();
            if (posterPath != null && !posterPath.isEmpty()) {
                String fullPosterUrl = Constants.IMAGE_BASE_URL + Constants.IMAGE_SIZE_W500 + posterPath;
                Glide.with(itemView.getContext()).load(fullPosterUrl).into(posterImage);
            }
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onMovieInfoClick(movie);
                }
            });
        }
    }
}