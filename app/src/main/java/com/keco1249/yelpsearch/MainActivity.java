package com.keco1249.yelpsearch;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.keco1249.yelpsearch.recents.RecentsAdapter;
import com.keco1249.yelpsearch.search.SearchResult;
import com.keco1249.yelpsearch.search.SearchResultsAdapter;
import com.keco1249.yelpsearch.ui.ItemMarginDecoration;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.view.View.DRAWING_CACHE_QUALITY_HIGH;
import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity implements SearchView {
    private static final int COARSE_LOCATION_PERMISSION_REQUEST_CODE = 0;

    private SearchViewController searchViewController;
    private SearchResultsAdapter searchResultsAdapter;
    private RecentsAdapter recentsAdapter;
    private MenuItem clearSearchMenuItem;

    @BindView(R.id.mainContent)
    ConstraintLayout mainContent;

    @BindView(R.id.searchBarEditText)
    EditText searchBarEditText;

    @BindView(R.id.searchResultsRecyclerView)
    RecyclerView searchResultsRecyclerView;

    @BindView(R.id.recentSearchRecyclerView)
    RecyclerView recentSearchRecyclerView;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        searchViewController = new SearchViewController(this);

        searchResultsRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL));

        searchResultsAdapter = new SearchResultsAdapter();
        searchResultsRecyclerView.setAdapter(searchResultsAdapter);

        // Increased cache view cache size to prevent shuffling animations in the StaggeredGridLayoutManager
        // A better long-term solution is to store a map of image dimensions by url once the bitmap has been
        // loaded to avoid having the StaggeredGridLayoutManager resize the child views when the image
        // is evicted from the cache and loads more than once.
        searchResultsRecyclerView.setHasFixedSize(true);
        searchResultsRecyclerView.setItemViewCacheSize(20);
        searchResultsRecyclerView.setDrawingCacheEnabled(true);
        searchResultsRecyclerView.setDrawingCacheQuality(DRAWING_CACHE_QUALITY_HIGH);

        int marginInPixels = getResources().getDimensionPixelSize(R.dimen.grid_layout_margin);
        searchResultsRecyclerView.addItemDecoration(new ItemMarginDecoration(marginInPixels));

        searchBarEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() > 0) {
                    recentSearchRecyclerView.setVisibility(View.VISIBLE);
                    searchViewController.retrieveRecents();
                    clearSearchMenuItem.setVisible(true);
                } else {
                    closeSearchHistoryList();
                    clearSearchMenuItem.setVisible(false);
                }
            }
        });

        recentSearchRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recentsAdapter = new RecentsAdapter();
        recentSearchRecyclerView.setAdapter(recentsAdapter);
        // Sets line divider in between history results
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getApplicationContext(),
                DividerItemDecoration.VERTICAL);
        recentSearchRecyclerView.addItemDecoration(dividerItemDecoration);
        recentsAdapter.setRecentSearchStringSelectedListener(new RecentsAdapter.RecentSearchStringSelectedListener() {
            @Override
            public void onRecentSearchString(@NonNull String recentSearchString) {
                searchBarEditText.setText(recentSearchString);
                searchViewController.retrieveResults();
                closeSearchHistoryList();
            }
        });

        checkForLocationPermission();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        searchViewController.setView(this);
    }

    @Override
    protected void onPause() {
        searchViewController.disconnectLocationClient();
        searchViewController.removeView();
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        clearSearchMenuItem = menu.findItem(R.id.action_clear);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                searchViewController.retrieveResults();
                progressBar.setVisibility(View.VISIBLE);
                break;
            case R.id.action_clear:
                searchBarEditText.setText("");
                searchResultsAdapter.setSearchResults(Collections.<SearchResult>emptyList());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void displayResults(List<SearchResult> searchResults) {
        progressBar.setVisibility(GONE);
        searchResultsAdapter.setSearchResults(searchResults);
    }

    @Override
    public void displaySearchHistory(List<String> searchHistoryList) {
        recentsAdapter.setRecentSearches(searchHistoryList);
    }

    @Override
    public void closeSearchHistoryList() {
        recentSearchRecyclerView.setVisibility(GONE);
    }

    @Override
    public String getSearchText() {
        return searchBarEditText.getText().toString();
    }

    @Override
    public void handleSearchFailure() {
        progressBar.setVisibility(GONE);
        Snackbar snackbar = Snackbar.make(mainContent, R.string.search_failure_string, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case COARSE_LOCATION_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED) {
                    searchViewController.connectLocationListener();
                }
                break;
        }
    }

    private void checkForLocationPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (permissionCheck != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    COARSE_LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            searchViewController.connectLocationListener();
        }
    }
}
