package com.keco1249.yelpsearch;

import android.location.Location;

import com.keco1249.yelpsearch.storage.RecentsStorage;
import com.keco1249.yelpsearch.search.SearchResult;
import com.keco1249.yelpsearch.networkclient.YelpHttpClient;
import com.mapzen.android.lost.api.FusedLocationProviderApi;
import com.mapzen.android.lost.api.LostApiClient;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Sample unit test to show what patterns I usually use when writing tests. Typically, I aim to write
 * unit tests to cover any classes that have any sort of business logic e.g. usually anything that is
 * not a view (Espresso tests for those) or model object.
 */
public class SearchViewControllerTest {
    private YelpHttpClient mockYelpHttpClient;
    private RecentsStorage mockRecentsStorage;
    private LostApiClient mockLostApiClient;
    private FusedLocationProviderApi mockLocationProviderApi;
    private SearchViewController subject;

    @Before
    public void setup() {
        mockYelpHttpClient = mock(YelpHttpClient.class);
        mockRecentsStorage = mock(RecentsStorage.class);
        mockLostApiClient = mock(LostApiClient.class);
        mockLocationProviderApi = mock(FusedLocationProviderApi.class);

        subject = new SearchViewController(mockYelpHttpClient, mockRecentsStorage,
                mockLostApiClient, mockLocationProviderApi);
    }

    @Test
    public void testRetrieveResults_lostApiClientNotConnected() {
        when(mockLostApiClient.isConnected()).thenReturn(false);

        SearchView searchView = mock(SearchView.class);
        when(searchView.getSearchText()).thenReturn("test search");

        subject.setView(searchView);

        subject.retrieveResults();

        verify(mockYelpHttpClient, times(1)).search(
                eq("test search"),
                eq(SearchViewController.DEFAULT_LOCATION_LATITUDE),
                eq(SearchViewController.DEFAULT_LOCATION_LONGITUDE),
                any(YelpHttpClient.SearchResponseListener.class)
        );

        verify(mockRecentsStorage, times(1)).addRecent("test search");
    }

    @Test
    public void testRetrieveResults_lostApiClientConnected() {
        when(mockLostApiClient.isConnected()).thenReturn(true);

        Location location = mock(Location.class);
        when(location.getLatitude()).thenReturn(101.1);
        when(location.getLongitude()).thenReturn(99.9);

        when(mockLocationProviderApi.getLastLocation(mockLostApiClient))
                .thenReturn(location);

        SearchView searchView = mock(SearchView.class);
        when(searchView.getSearchText()).thenReturn("test search");

        subject.setView(searchView);

        subject.retrieveResults();

        verify(mockYelpHttpClient, times(1)).search(
                eq("test search"),
                eq(101.1),
                eq(99.9),
                any(YelpHttpClient.SearchResponseListener.class)
        );

        verify(mockRecentsStorage, times(1)).addRecent("test search");
    }

    @Test
    public void testRetrieveResults_searchResponseListener() {
        when(mockLostApiClient.isConnected()).thenReturn(false);

        SearchView searchView = mock(SearchView.class);
        when(searchView.getSearchText()).thenReturn("test search");

        subject.setView(searchView);

        subject.retrieveResults();

        ArgumentCaptor<YelpHttpClient.SearchResponseListener> responseListenerArgumentCaptor =
                ArgumentCaptor.forClass(YelpHttpClient.SearchResponseListener.class);

        verify(mockYelpHttpClient, times(1)).search(
                eq("test search"),
                eq(SearchViewController.DEFAULT_LOCATION_LATITUDE),
                eq(SearchViewController.DEFAULT_LOCATION_LONGITUDE),
                responseListenerArgumentCaptor.capture()
        );

        YelpHttpClient.SearchResponseListener responseListener = responseListenerArgumentCaptor.getValue();

        List<SearchResult> list = Collections.emptyList();

        // Test response callback
        responseListener.onSearchResultRetrieved(list);

        verify(searchView, times(1)).closeSearchHistoryList();
        verify(searchView, times(1)).displayResults(list);
    }

    /*
        TODO - Write more tests
        * Rest of edge cases in retrieveResults method
        * Test retrieveRecents method
        * Tests for connectLocationListener are probably not necessary but could be if there were more logic in them
    */
}
