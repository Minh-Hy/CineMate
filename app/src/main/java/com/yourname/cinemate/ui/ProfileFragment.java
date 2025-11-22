package com.yourname.cinemate.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.yourname.cinemate.R;
import com.yourname.cinemate.data.model.Movie;
import com.yourname.cinemate.data.model.User;
import com.yourname.cinemate.viewmodel.ProfileViewModel;

import java.io.File;
import java.util.ArrayList;

public class ProfileFragment extends Fragment implements MoviePosterAdapter.OnMovieClickListener {

    private ProfileViewModel viewModel;
    private ImageView avatarImage;
    private TextView nameText, emailText;
    private Button logoutButton;
    private RecyclerView watchlistRecyclerView;
    private MoviePosterAdapter watchlistAdapter;
    private ProgressBar progressBar;
    private ImageButton editProfileButton;
    private TextView emptyWatchlistText;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private Button changePasswordButton;
    private Button chatAdminButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // --- KHỞI TẠO ACTIVITY RESULT LAUNCHER ---
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            // Chuyển đổi Uri thành đường dẫn file thật
                            File imageFile = new File(getRealPathFromURI(selectedImageUri));
                            // Gọi ViewModel để upload
                            viewModel.updateUserAvatar(selectedImageUri);
                        }
                    }
                });
    }

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        bindViews(view);
        setupToolbar(view);
        observeViewModel();

        //Kich hoat tai du lieu
        viewModel.refreshData();

        logoutButton.setOnClickListener(v -> {
            viewModel.logout();
            // Điều hướng về màn hình login và xóa hết back stack
            Navigation.findNavController(v).navigate(R.id.action_global_to_loginFragment);
        });
        // Thêm sự kiện click cho nút Sửa
        editProfileButton.setOnClickListener(v -> {
            // Lấy tên hiện tại để hiển thị sẵn trong dialog
            User currentUser = viewModel.getUser().getValue();
            if (currentUser != null) {
                showEditUsernameDialog(currentUser.getDisplayName());
            }
        });
        avatarImage.setOnClickListener(v -> {
            openImageChooser();
        });
        changePasswordButton.setOnClickListener(v -> {
            // Điều hướng đến màn hình đổi mật khẩu
            Navigation.findNavController(v).navigate(R.id.action_profileFragment_to_changePasswordFragment);
        });
        chatAdminButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_profileFragment_to_chatFragment);
        });
    }

    private void bindViews(View view) {
        avatarImage = view.findViewById(R.id.image_profile_avatar);
        nameText = view.findViewById(R.id.text_profile_name);
        emailText = view.findViewById(R.id.text_profile_email);
        logoutButton = view.findViewById(R.id.button_logout);
        watchlistRecyclerView = view.findViewById(R.id.recycler_watchlist);
        progressBar = view.findViewById(R.id.progress_bar_profile);
        editProfileButton = view.findViewById(R.id.button_edit_profile);

        watchlistAdapter = new MoviePosterAdapter(this);
        watchlistRecyclerView.setAdapter(watchlistAdapter);
        emptyWatchlistText = view.findViewById(R.id.text_empty_watchlist);
        changePasswordButton = view.findViewById(R.id.button_change_password);
        chatAdminButton = view.findViewById(R.id.button_chat_admin);
    }

    private void setupToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar_profile);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> getParentFragmentManager().popBackStack());
    }

    private void observeViewModel() {
        viewModel.isLoading().observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                nameText.setText(user.getDisplayName());
                emailText.setText(user.getEmail());
                Glide.with(this)
                        .load(user.getAvatarUrl())
                        .circleCrop()
                        .placeholder(R.drawable.ic_profile) // Thêm placeholder
                        .into(avatarImage);
            }
        });

        viewModel.getWatchlist().observe(getViewLifecycleOwner(), movies -> {
            progressBar.setVisibility(View.GONE);
            if (movies != null && !movies.isEmpty()) {
                watchlistRecyclerView.setVisibility(View.VISIBLE);
                emptyWatchlistText.setVisibility(View.GONE);
                watchlistAdapter.setMovies(movies);
            } else {
                // Xử lý trường hợp watchlist rỗng hoặc lỗi
                watchlistRecyclerView.setVisibility(View.GONE);
                emptyWatchlistText.setVisibility(View.VISIBLE);
            }
        });
        // Thêm observer cho trạng thái cập nhật
        viewModel.getUpdateStatus().observe(getViewLifecycleOwner(), event -> {
            String message = event.getContentIfNotHandled();
            if (message != null) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEditUsernameDialog(String currentName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_username, null);
        builder.setView(dialogView);

        final TextInputEditText newNameEditText = dialogView.findViewById(R.id.edit_text_new_name);
        Button cancelButton = dialogView.findViewById(R.id.button_cancel_edit);
        Button saveButton = dialogView.findViewById(R.id.button_save_edit);

        // Hiển thị sẵn tên cũ
        newNameEditText.setText(currentName);

        final AlertDialog dialog = builder.create();

        cancelButton.setOnClickListener(v -> dialog.dismiss());
        saveButton.setOnClickListener(v -> {
            String newName = newNameEditText.getText().toString().trim();
            if (newName.isEmpty()) {
                newNameEditText.setError("Tên không được để trống");
            } else if (newName.equals(currentName)) {
                dialog.dismiss(); // Không có gì thay đổi
            }
            else {
                // Gọi ViewModel để cập nhật
                viewModel.updateUserDisplayName(newName);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public void onMovieClick(Movie movie) {
        if (movie == null) return;

        Log.d("HomeFragment", "Navigating to Detail for movie: " + movie.getTitle());
        Bundle bundle = new Bundle();
        bundle.putInt("movieId", movie.getId());

        try {
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                    .navigate(R.id.action_profileFragment_to_detailFragment, bundle);
        } catch (IllegalArgumentException e) {
            Log.e("HomeFragment", "Navigation to Detail failed!", e);
            Toast.makeText(getContext(), "Không thể mở phim lúc này.", Toast.LENGTH_SHORT).show();
        }
    }
    private String getYouTubeVideoId(String youtubeUrl) {
        if (youtubeUrl == null || !youtubeUrl.trim().contains("v=")) {
            return null;
        }
        String videoId = null;
        String[] urlParts = youtubeUrl.split("v=");
        if (urlParts.length > 1) {
            videoId = urlParts[1];
            int ampersandPosition = videoId.indexOf('&');
            if (ampersandPosition != -1) {
                videoId = videoId.substring(0, ampersandPosition);
            }
        }
        return videoId;
    }
    /**
     * Mở trình chọn ảnh của hệ thống.
     */
    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }
    /**
     * Hàm tiện ích để lấy đường dẫn file thật từ một content Uri.
     * Cần thiết vì trình chọn ảnh trả về một content Uri, không phải file path.
     */
    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = requireActivity().getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor == null) return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        cursor.close();
        return path;
    }
}