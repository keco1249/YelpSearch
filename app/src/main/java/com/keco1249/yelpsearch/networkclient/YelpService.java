package com.keco1249.yelpsearch.networkclient;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

/**
 * Retrofit service definition for Yelp's fusion search api.
 */
public interface YelpService {
    @GET("businesses/search")
    Call<YelpSearchResponse> search(@Header("Authorization") String authorization, @Query("term") String term,
                                    @Query("latitude") double latitude, @Query("longitude") double longitude);
}
