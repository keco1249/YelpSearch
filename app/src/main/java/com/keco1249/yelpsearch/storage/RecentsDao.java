package com.keco1249.yelpsearch.storage;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface RecentsDao {

    @Query("SELECT * FROM recent_search")
    List<RecentSearch> getAllRecents();

    @Insert
    void insertAll(RecentSearch... recents);

    @Query("DELETE FROM recent_search WHERE search_text LIKE :searchText")
    void deleteBySearch(String searchText);

    @Query("SELECT * FROM recent_search WHERE search_text LIKE :searchText")
    List<RecentSearch> searchBySearchText(String searchText);
}
