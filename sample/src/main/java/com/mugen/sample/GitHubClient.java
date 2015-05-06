package com.mugen.sample;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import retrofit.RestAdapter;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Github Client
 * <p/>
 * Define github for retrofit
 */
public class GitHubClient {

    //github api url
    private static final String API_URL = "https://api.github.com";

    //sort the github repos by stars in desc order
    static final String DEFAULT_SORT = "stars";
    static final String DEFAULT_ORDER = "desc";

    //Search result github response
    static public class SearchResult {
        @SerializedName("total_count")
        int totalCount;
        @SerializedName("incomplete_results")
        boolean incompleteResults;
        @SerializedName("items")
        List<Repo> repos;

    }

    //Github repo information
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

    //Retorfit github interface
    interface GitHub {

        @GET("/search/repositories?per_page=10")
        SearchResult searchRepos(@Query("q") String query,
                                 @Query("sort") String sortBy,
                                 @Query("order") String order,
                                 @Query("page") int start);
    }

    /**
     * Get github client.
     *
     * @return Github retrofit interface
     */
    public static GitHub getClient() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(API_URL)
                .setLogLevel(RestAdapter.LogLevel.BASIC)
                .build();

        return restAdapter.create(GitHub.class);
    }
}
