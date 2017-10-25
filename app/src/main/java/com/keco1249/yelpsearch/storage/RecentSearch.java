package com.keco1249.yelpsearch.storage;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "recent_search")
public class RecentSearch {

    @PrimaryKey
    @ColumnInfo(name = "search_text")
    public String searchText;

    public RecentSearch(String searchText) {
        this.searchText = searchText;
    }
}
