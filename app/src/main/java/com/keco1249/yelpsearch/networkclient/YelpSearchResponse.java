package com.keco1249.yelpsearch.networkclient;

import java.util.List;

/**
 * Yelp search response model object for gson deserialization. Only use it to instantiate immutable
 * search result model objects.
 */
public class YelpSearchResponse {
    public static class SearchResult {
        public String id;
        public String name;
        public String image_url;
    }

    public List<SearchResult> businesses;
}
