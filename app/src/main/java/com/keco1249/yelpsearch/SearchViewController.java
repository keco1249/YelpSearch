package com.keco1249.yelpsearch;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.keco1249.yelpsearch.storage.RecentsStorage;
import com.keco1249.yelpsearch.search.SearchResult;
import com.keco1249.yelpsearch.networkclient.YelpHttpClient;
import com.mapzen.android.lost.api.FusedLocationProviderApi;
import com.mapzen.android.lost.api.LocationServices;
import com.mapzen.android.lost.api.LostApiClient;

import java.util.List;

/**
 * Handles the business logic and interactions between the model and the view. By setting and removing the view we avoid
 * leaking any memory on a rotation. We also keep the view from knowing anything about the model which
 * in our case is represented by the DB and the REST API.
 */
public class SearchViewController {
    public static final double DEFAULT_LOCATION_LATITUDE = 40.01765;
    public static final double DEFAULT_LOCATION_LONGITUDE = -105.2797;
    @NonNull
    private final YelpHttpClient yelpHttpClient;
    @NonNull
    private final RecentsStorage recentsStorage;
    @NonNull
    private final LostApiClient lostApiClient;
    @NonNull
    private final FusedLocationProviderApi locationProviderApi;
    @Nullable
    private SearchView searchView;

    public SearchViewController(Context context) {
        this(new YelpHttpClient(), new RecentsStorage(context.getApplicationContext()),
                new LostApiClient.Builder(context).build(), LocationServices.FusedLocationApi);
    }

    /**
     * For testing purposes only.
     */
    SearchViewController(@NonNull YelpHttpClient yelpHttpClient,
                                   @NonNull RecentsStorage recentsStorage,
                                   @NonNull LostApiClient lostApiClient,
                                   @NonNull FusedLocationProviderApi locationProviderApi) {
        this.yelpHttpClient = yelpHttpClient;
        this.recentsStorage = recentsStorage;
        this.lostApiClient = lostApiClient;
        this.locationProviderApi = locationProviderApi;
    }

    public void setView(SearchView searchView) {
        this.searchView = searchView;
    }

    public void removeView() {
        this.searchView = null;
    }

    public void retrieveResults() {
        if (searchView == null) {
            return;
        }

        String searchText = searchView.getSearchText();

        double locationLatitude = DEFAULT_LOCATION_LATITUDE;
        double locationLongitude = DEFAULT_LOCATION_LONGITUDE;

        if (lostApiClient.isConnected()) {
            // Permission is checked in the view and the location client does not connect if unless
            // the permission is granted
            @SuppressLint("MissingPermission")
            Location lastLocation = locationProviderApi.getLastLocation(lostApiClient);
            locationLatitude = lastLocation.getLatitude();
            locationLongitude = lastLocation.getLongitude();
        }

        yelpHttpClient.search(searchText, locationLatitude, locationLongitude, new YelpHttpClient.SearchResponseListener() {
            @Override
            public void onSearchResultRetrieved(List<SearchResult> searchResults) {
                if (searchView != null) {
                    searchView.closeSearchHistoryList();
                    searchView.displayResults(searchResults);
                }
            }

            @Override
            public void onFailure() {
                // TODO Show toast with failure message
            }
        });

        // Store search text as recent search
        recentsStorage.addRecent(searchText);
    }

    public void retrieveRecents() {
        recentsStorage.readRecents(new RecentsStorage.RecentsReadListener() {
            @Override
            public void onRecentsRead(List<String> recentSearchStrings) {
                if (searchView != null) {
                    searchView.displaySearchHistory(recentSearchStrings);
                }
            }
        });
    }

    public void connectLocationListener() {
        lostApiClient.connect();
    }

    public void disconnectLocationClient() {
        lostApiClient.disconnect();
    }
}
