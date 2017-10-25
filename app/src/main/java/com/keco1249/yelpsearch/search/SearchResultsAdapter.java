package com.keco1249.yelpsearch.search;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for search result recycler view.
 */
public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.SearchResultViewHolder> {

    static class SearchResultViewHolder extends RecyclerView.ViewHolder {
        private final SearchResultView searchResultView;

        SearchResultViewHolder(SearchResultView searchResultView) {
            super(searchResultView);
            this.searchResultView = searchResultView;
        }
    }

    @NonNull
    private List<SearchResult> searchResults = new ArrayList<>();

    public void setSearchResults(@NonNull List<SearchResult> searchResults) {
        this.searchResults.clear();
        this.searchResults.addAll(searchResults);
        notifyDataSetChanged();
    }

    @Override
    public SearchResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        SearchResultView searchResultView = new SearchResultView(parent.getContext());
        return new SearchResultViewHolder(searchResultView);
    }

    @Override
    public void onBindViewHolder(SearchResultViewHolder holder, int position) {
        holder.searchResultView.setSearchResult(searchResults.get(position));
    }

    @Override
    public int getItemCount() {
        return searchResults.size();
    }
}
