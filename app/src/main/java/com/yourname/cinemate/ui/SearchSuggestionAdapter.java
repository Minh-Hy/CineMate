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

public class SearchSuggestionAdapter extends RecyclerView.Adapter<SearchSuggestionAdapter.SuggestionViewHolder> {

    public interface OnSuggestionClickListener {
        void onSuggestionClick(Movie movie);
    }
    private List<Movie> movies = new ArrayList<>();
    private final OnSuggestionClickListener listener;

    public SearchSuggestionAdapter(OnSuggestionClickListener listener) {
        this.listener = listener;
    }

    public void setSuggestions(List<Movie> movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }

    @NonNull @Override
    public SuggestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_suggestion, parent, false);
        return new SuggestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestionViewHolder holder, int position) {
        holder.bind(movies.get(position), listener);
    }
    @Override public int getItemCount() { return movies.size(); }

    static class SuggestionViewHolder extends RecyclerView.ViewHolder {
        ImageView poster;
        TextView title, year;
        public SuggestionViewHolder(@NonNull View itemView) {
            super(itemView);
            poster = itemView.findViewById(R.id.image_suggestion_poster);
            title = itemView.findViewById(R.id.text_suggestion_title);
            year = itemView.findViewById(R.id.text_suggestion_year);
        }

        public void bind(Movie movie, OnSuggestionClickListener listener) {
            title.setText(movie.getTitle());
            if (movie.getReleaseDate() != null && !movie.getReleaseDate().isEmpty()) {
                year.setText(movie.getReleaseDate().substring(0, 4));
            } else {
                year.setText("");
            }

            String posterPath = movie.getPosterPath();
            if (posterPath != null && !posterPath.isEmpty()) {
                String fullPosterUrl = Constants.IMAGE_BASE_URL + Constants.IMAGE_SIZE_W500 + posterPath;
                Glide.with(itemView.getContext()).load(fullPosterUrl).into(poster);
            } else {
                poster.setImageResource(R.color.netflix_gray_dark); // Placeholder
            }
            itemView.setOnClickListener(v -> listener.onSuggestionClick(movie));
        }
    }
}