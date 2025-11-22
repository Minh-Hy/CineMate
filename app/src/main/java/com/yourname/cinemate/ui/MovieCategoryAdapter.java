package com.yourname.cinemate.ui;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.yourname.cinemate.R;
import com.yourname.cinemate.data.model.Movie;
import com.yourname.cinemate.data.model.MovieCategory;
import com.yourname.cinemate.utils.Constants;
import java.util.ArrayList;
import java.util.List;

public class MovieCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // Định nghĩa 2 kiểu View Type
    private static final int VIEW_TYPE_BANNER = 0;
    private static final int VIEW_TYPE_CATEGORY = 1;

    private List<MovieCategory> categories = new ArrayList<>();
    private final MovieBannerAdapter.OnMovieClickListener movieClickListener;

    public MovieCategoryAdapter(MovieBannerAdapter.OnMovieClickListener movieClickListener) {
        this.movieClickListener = movieClickListener;
    }

    public void setCategories(List<MovieCategory> categories) {
        this.categories = categories;
        notifyDataSetChanged();
    }

    // --- ViewHolder cho Banner ---
    static class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView backdropImage;
        TextView titleText;
        Button playButton, infoButton;

        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            backdropImage = itemView.findViewById(R.id.image_banner_backdrop);
            titleText = itemView.findViewById(R.id.text_banner_title);
            playButton = itemView.findViewById(R.id.button_banner_play);
            infoButton = itemView.findViewById(R.id.button_banner_info);
        }

        public void bind(Movie movie, MovieBannerAdapter.OnMovieClickListener listener) {
            titleText.setText(movie.getTitle());
            String backdropPath = movie.getBackdropPath();
            if (backdropPath != null && !backdropPath.isEmpty()) {
                // Dùng ảnh kích thước lớn hơn cho banner
                String fullBackdropUrl = Constants.IMAGE_BASE_URL + "w780" + backdropPath;

                // THÊM DÒNG LOG NÀY
                Log.d("BannerViewHolder", "Loading BANNER image from URL: " + fullBackdropUrl);

                Glide.with(itemView.getContext()).load(fullBackdropUrl).into(backdropImage);
            } else {
                Log.w("BannerViewHolder", "Banner movie has NO backdrop path!");
            }
            // Kiểm tra listener trước khi gán sự kiện
            if (listener != null) {
                // Gán sự kiện click cho nút "Thông tin" -> gọi onMovieInfoClick
                infoButton.setOnClickListener(v -> listener.onMovieInfoClick(movie));

                // Gán sự kiện click cho nút "Phát" -> gọi onMoviePlayClick
                playButton.setOnClickListener(v -> listener.onMoviePlayClick(movie));
            }
        }
    }

    // --- ViewHolder cho Category (giống code cũ) ---
    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryTitle;
        RecyclerView moviesRecyclerView;
        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryTitle = itemView.findViewById(R.id.text_category_title);
            moviesRecyclerView = itemView.findViewById(R.id.recycler_movies_child);
        }
        public void bind(MovieCategory category, MovieBannerAdapter.OnMovieClickListener movieClickListener) {
            categoryTitle.setText(category.getTitle());
            MovieBannerAdapter adapter = new MovieBannerAdapter(movieClickListener);
            moviesRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
            moviesRecyclerView.setAdapter(adapter);
            adapter.setMovies(category.getMovies());
        }
    }

    // --- Các phương thức được override của Adapter ---

    @Override
    public int getItemViewType(int position) {
        // Nếu là item đầu tiên, trả về kiểu BANNER. Ngược lại, trả về kiểu CATEGORY.
        if (position == 0) {
            return VIEW_TYPE_BANNER;
        } else {
            return VIEW_TYPE_CATEGORY;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Dựa vào viewType, inflate layout tương ứng
        if (viewType == VIEW_TYPE_BANNER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_banner, parent, false);
            return new BannerViewHolder(view);
        } else { // viewType == VIEW_TYPE_CATEGORY
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie_category, parent, false);
            return new CategoryViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // Dựa vào kiểu của ViewHolder, gọi hàm bind tương ứng
        if (holder.getItemViewType() == VIEW_TYPE_BANNER) {
            // Lấy phim đầu tiên trong category đầu tiên để làm banner
            if (!categories.isEmpty() && !categories.get(0).getMovies().isEmpty()) {
                Movie bannerMovie = categories.get(0).getMovies().get(0);
                ((BannerViewHolder) holder).bind(bannerMovie, movieClickListener);
            }
        } else { // holder.getItemViewType() == VIEW_TYPE_CATEGORY
            MovieCategory category = categories.get(position);
            ((CategoryViewHolder) holder).bind(category, movieClickListener);
        }
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }
}