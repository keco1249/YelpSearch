package com.keco1249.yelpsearch.storage;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = RecentSearch.class, version = 1)
public abstract class RecentsDatabase extends RoomDatabase {
    public abstract RecentsDao recentsDao();
}
