package com.keco1249.yelpsearch.search;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import com.keco1249.yelpsearch.R;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchResultView extends CardView {

    @BindView(R.id.searchResultImageView)
    ImageView imageView;

    @BindView(R.id.searchResultTextView)
    TextView textView;

    public SearchResultView(Context context) {
        this(context, null);
    }

    public SearchResultView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchResultView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.search_result, this, true);
        ButterKnife.bind(this);
    }

    public void setSearchResult(@NonNull SearchResult searchResult) {
        if (searchResult.getImageUrl() != null && searchResult.getImageUrl().length() > 0) {
            Picasso.with(getContext()).load(searchResult.getImageUrl()).into(imageView);
        }
        textView.setText(searchResult.getName());
    }
}
