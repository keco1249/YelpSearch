package com.keco1249.yelpsearch.recents;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for recent search recycler view.
 */
public class RecentsAdapter extends RecyclerView.Adapter<RecentsAdapter.RecentsViewHolder> {

    /**
     * Callback method for when recent search string is selected in the recycler view
     */
    public interface RecentSearchStringSelectedListener {
        void onRecentSearchString(@NonNull String recentSearchString);
    }

    public static class RecentsViewHolder extends RecyclerView.ViewHolder {
        private final RecentSearchView recentSearchView;

        public RecentsViewHolder(RecentSearchView recentSearchView) {
            super(recentSearchView);
            this.recentSearchView = recentSearchView;
        }
    }

    @NonNull
    private final List<String> recentSearches = new ArrayList<>();
    @Nullable
    private RecentSearchStringSelectedListener listener;

    public void setRecentSearches(@NonNull List<String> recentSearches) {
        this.recentSearches.clear();
        this.recentSearches.addAll(recentSearches);
        notifyDataSetChanged();
    }

    public void setRecentSearchStringSelectedListener(
            @Nullable RecentSearchStringSelectedListener recentSearchStringSelectedListener) {
        listener = recentSearchStringSelectedListener;
    }

    @Override
    public RecentsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecentSearchView recentSearchView = new RecentSearchView(parent.getContext());
        recentSearchView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        return new RecentsViewHolder(recentSearchView);
    }

    @Override
    public void onBindViewHolder(RecentsViewHolder holder, int position) {
        final String recentSearchString = recentSearches.get(position);
        holder.recentSearchView.textView.setText(recentSearchString);
        holder.recentSearchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onRecentSearchString(recentSearchString);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recentSearches.size();
    }
}
