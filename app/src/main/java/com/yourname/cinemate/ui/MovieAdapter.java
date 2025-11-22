package com.yourname.cinemate.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.yourname.cinemate.R;
import com.yourname.cinemate.data.model.Movie;
import com.yourname.cinemate.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    public interface OnMovieClickListener {
        void onMovieClick(Movie movie);
    }

    private List<Movie> movieList = new ArrayList<>();
    private final OnMovieClickListener clickListener;

    public MovieAdapter(OnMovieClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setMovieList(List<Movie> movieList) {
        this.movieList = movieList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movieList.get(position);
        holder.bind(movie, clickListener);
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    static class MovieViewHolder extends RecyclerView.ViewHolder {
        private final ImageView poster;
        private final TextView title;
        private final TextView rating;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            poster = itemView.findViewById(R.id.image_poster);
            title = itemView.findViewById(R.id.text_movie_title);
            rating = itemView.findViewById(R.id.text_movie_rating);
        }

        public void bind(final Movie movie, final OnMovieClickListener clickListener) {
            title.setText(movie.getTitle());
            rating.setText(String.format(Locale.US, "%.1f", movie.getVoteAverage()));
            //Ghep noi url day du
            String fullPosterUrl = Constants.IMAGE_BASE_URL + Constants.IMAGE_SIZE_W500 + movie.getPosterPath();
            Glide.with(itemView.getContext())
                    .load(fullPosterUrl)
                    .placeholder(R.drawable.ic_launcher_background) // Ảnh tạm thời khi đang tải
                    .error(R.drawable.ic_launcher_background) // Ảnh khi lỗi
                    .into(poster);

            itemView.setOnClickListener(v -> clickListener.onMovieClick(movie));
        }
    }
}