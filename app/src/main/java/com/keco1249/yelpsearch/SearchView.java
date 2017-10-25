package com.keco1249.yelpsearch;

import com.keco1249.yelpsearch.search.SearchResult;

import java.util.List;

/**
 * Defines the ui operations needed by the app to separate concerns between the Activity/View/Fragment
 * the controller and the model.
 */
public interface SearchView {
    void displayResults(List<SearchResult> searchResults);

    void displaySearchHistory(List<String> searchHistoryList);

    void closeSearchHistoryList();

    String getSearchText();
}
