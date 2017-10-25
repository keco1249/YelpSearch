package com.keco1249.yelpsearch.ui;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 *  Allows us to set a margin around the items in a recyclerview. See this stackoverflow answer for
 *  more details https://stackoverflow.com/questions/28531996/android-recyclerview-gridlayoutmanager-column-spacing
 */
public class ItemMarginDecoration extends RecyclerView.ItemDecoration {
    private final int margin;

    public ItemMarginDecoration(int margin) {
        this.margin = margin;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.right = margin;
        outRect.bottom = margin;
        outRect.left = margin;

        // Add top margin only for the first item to avoid double space between items
        outRect.top = parent.getChildLayoutPosition(view) == 0 ? margin : 0;
    }
}
