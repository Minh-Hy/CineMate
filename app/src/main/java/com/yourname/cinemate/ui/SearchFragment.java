package com.yourname.cinemate.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import com.yourname.cinemate.R;
import com.yourname.cinemate.data.model.Movie;
import com.yourname.cinemate.viewmodel.SearchViewModel;
import java.util.ArrayList;

public class SearchFragment extends Fragment implements SearchSuggestionAdapter.OnSuggestionClickListener {

    private SearchViewModel viewModel;
    private RecyclerView recyclerView;
    private SearchSuggestionAdapter adapter;
    private ProgressBar progressBar;
    private TextView placeholderText;
    private EditText searchEditText;
    private ImageButton backButton, clearButton;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(SearchViewModel.class);

        bindViews(view);
        setupSearchInput();
        observeViewModel();
    }

    private void bindViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_search_suggestions);
        progressBar = view.findViewById(R.id.progress_bar_search);
        placeholderText = view.findViewById(R.id.text_search_placeholder);
        searchEditText = view.findViewById(R.id.edit_text_search_custom);
        backButton = view.findViewById(R.id.button_back_search);
        clearButton = view.findViewById(R.id.button_clear_search);

        adapter = new SearchSuggestionAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    private void setupSearchInput() {
        backButton.setOnClickListener(v -> getParentFragmentManager().popBackStack());
        clearButton.setOnClickListener(v -> searchEditText.setText(""));

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Khi người dùng gõ, gọi ViewModel để xử lý debounce
                viewModel.search(s.toString());
                // Hiển thị nút xóa nếu có text
                clearButton.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    private void observeViewModel() {
        viewModel.isLoading().observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.getSearchResults().observe(getViewLifecycleOwner(), movies -> {
            placeholderText.setVisibility(View.GONE);
            if (movies == null) {
                // Trạng thái ban đầu, không hiển thị gì cả
                adapter.setSuggestions(new ArrayList<>());
            } else if (movies.isEmpty()) {
                // Đã tìm kiếm nhưng không có kết quả
                placeholderText.setVisibility(View.VISIBLE);
                adapter.setSuggestions(new ArrayList<>());
            } else {
                // Tìm thấy kết quả
                adapter.setSuggestions(movies);
            }
        });
    }

    @Override
    public void onSuggestionClick(Movie movie) {
        // Khi người dùng click vào một gợi ý, đi đến trang chi tiết
        Bundle bundle = new Bundle();
        bundle.putInt("movieId", movie.getId());
        Navigation.findNavController(requireView()).navigate(R.id.action_searchFragment_to_detailFragment, bundle);
    }
}