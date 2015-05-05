package com.mugen.sample;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import retrofit.RestAdapter;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by imran on 13/11/14.
 */
public class GitHubClient {

    private static final String API_URL = "https://api.github.com";

    static final String DEFAULT_SORT = "stars";
    static final String DEFAULT_ORDER = "desc";

    static public class SearchResult {
        @SerializedName("total_count")
        int totalCount;
        @SerializedName("incomplete_results")
        boolean incompleteResults;
        @SerializedName("items")
        List<Repo> repos;

    }

    static public class Repo {

        public int id;
        public String name;
        public Owner owner;
        @SerializedName("html_url")
        public String htmlUrl;
        public String description;
        @SerializedName("stargazers_count")
        public int starsGazers;
        @SerializedName("watchers")
        public int watchers;
        public int forks;

        public static class Owner {

            String login;
            @SerializedName("avatar_url")
            public String avatarUrl;

        }
    }

    interface GitHub {

        @GET("/search/repositories?per_page=10")
        public SearchResult searchRepos(@Query("q") String query,
                                      @Query("sort") String sortBy,
                                      @Query("order") String order,
                                      @Query("page") int start);
    }

    public static GitHub getClient() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(API_URL)
                .setLogLevel(RestAdapter.LogLevel.BASIC)
                .build();

        return restAdapter.create(GitHub.class);
    }
}
