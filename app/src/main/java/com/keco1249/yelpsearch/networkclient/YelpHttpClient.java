package com.keco1249.yelpsearch.networkclient;

import android.support.annotation.NonNull;
import android.util.Log;

import com.keco1249.yelpsearch.search.SearchResult;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class YelpHttpClient {

    public interface SearchResponseListener {
        void onSearchResultRetrieved(List<SearchResult> searchResults);
        void onFailure();
    }

    private static final String TAG = "YelpHttpClient";
    private static final String URL = "https://api.yelp.com/v3/";
    private static final String ACCESS_TOKEN = "";
    private static final String AUTHORIZATION_HEADER = "Bearer " + ACCESS_TOKEN;

    @NonNull
    private final YelpService yelpService;

    public YelpHttpClient() {
        this(new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(YelpService.class));
    }

    protected YelpHttpClient(@NonNull YelpService yelpService) {
        this.yelpService = yelpService;
    }

    public void search(@NonNull String term, double latitude, double longitude, @NonNull final SearchResponseListener responseListener) {
        yelpService.search(AUTHORIZATION_HEADER, term, latitude, longitude)
                .enqueue(new Callback<YelpSearchResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<YelpSearchResponse> call, @NonNull Response<YelpSearchResponse> response) {
                        if (response.body() == null) {
                            Log.e(TAG, "Error retrieving response body.");
                            responseListener.onFailure();
                            return;
                        }

                        List<SearchResult> results = new ArrayList<>();
                        for (YelpSearchResponse.SearchResult result : response.body().businesses) {
                            results.add(new SearchResult(result.id, result.name, result.image_url));
                        }
                        responseListener.onSearchResultRetrieved(results);
                    }

                    @Override
                    public void onFailure(@NonNull Call<YelpSearchResponse> call, @NonNull Throwable t) {
                        Log.e(TAG, "Error making search request.", t);
                        responseListener.onFailure();
                    }
                });
    }
}
