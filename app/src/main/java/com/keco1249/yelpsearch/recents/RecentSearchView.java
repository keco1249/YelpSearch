package com.keco1249.yelpsearch.recents;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.keco1249.yelpsearch.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * View used to display a recent search
 */
public class RecentSearchView extends LinearLayout {
    @BindView(R.id.searchResultTextView)
    TextView textView;

    public RecentSearchView(Context context) {
        this(context, null);
    }

    public RecentSearchView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecentSearchView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.recent_search, this, true);
        ButterKnife.bind(this);
    }

    public void setRecentSearchString(@NonNull String searchString) {
        textView.setText(searchString);
    }
}
