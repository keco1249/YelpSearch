package com.keco1249.yelpsearch.storage;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Facade for recent search local storage implementation.
 */
public class RecentsStorage {
    /**
     * Callback for when recent searh strings are read from local storage.
     */
    public interface RecentsReadListener {
        void onRecentsRead(List<String> recentSearchStrings);
    }

    @NonNull
    private final RecentsDatabase recentsDatabase;

    public RecentsStorage(@NonNull Context context) {
        this(Room.databaseBuilder(context, RecentsDatabase.class, "recents_db").build());
    }

    /**
     * For testing purposes only.
     */
    RecentsStorage(@NonNull RecentsDatabase recentsDatabase) {
        this.recentsDatabase = recentsDatabase;
    }

    /**
     * Reads recent searches from local storage.
     * @param recentsReadListener callback for when the read is finished.
     */
    public void readRecents(@NonNull RecentsReadListener recentsReadListener) {
        new ReadRecentsAsyncTask(recentsDatabase.recentsDao(), recentsReadListener).execute();
    }

    /**
     * Write recent search string to local storage.
     */
    public void addRecent(@NonNull String recentSearch) {
        new AddRecentAsyncTask(recentsDatabase.recentsDao()).execute(recentSearch);
    }

    /**
     * Use async tasks to perform local storage operation asynchronously and then handle post execution on
     * the main (ui) thread.
     */
    static class AddRecentAsyncTask extends AsyncTask<String, Void, Void> {
        @NonNull
        private final RecentsDao recentsDao;

        AddRecentAsyncTask(@NonNull RecentsDao recentsDao) {
            this.recentsDao = recentsDao;
        }

        @Override
        protected Void doInBackground(String... recentSearches) {
            for (String recentSearch: recentSearches) {
                if (recentsDao.searchBySearchText(recentSearch).size() > 0) {
                    recentsDao.deleteBySearch(recentSearch);
                }
                recentsDao.insertAll(new RecentSearch(recentSearch));
            }
            return null;
        }
    }

    static class ReadRecentsAsyncTask extends AsyncTask<Void, Void, List<String>> {
        @NonNull
        private final RecentsDao recentsDao;
        @NonNull
        private final RecentsReadListener recentsReadListener;

        ReadRecentsAsyncTask(@NonNull RecentsDao recentsDao, @NonNull RecentsReadListener recentsReadListener) {
            this.recentsDao = recentsDao;
            this.recentsReadListener = recentsReadListener;
        }

        @Override
        protected List<String> doInBackground(Void... voids) {
            List<String> recentSearchStrings = new ArrayList<>();
            for (RecentSearch recentSearch: recentsDao.getAllRecents()) {
                recentSearchStrings.add(recentSearch.searchText);
            }
            return recentSearchStrings;
        }

        @Override
        protected void onPostExecute(List<String> recentSearchStrings) {
            recentsReadListener.onRecentsRead(recentSearchStrings);
        }
    }
}
