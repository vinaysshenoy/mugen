package com.mugen.sample;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mugen.Mugen;
import com.mugen.MugenCallbacks;
import com.mugen.ScrollDirection;
import com.mugen.attachers.BaseAttacher;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

/**
 * Repo Fragment to load Android related github repos.
 * <p/>
 * Sorted by most popular.
 */
public class ReposFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    interface OnLoadingListener {
        void onLoadingStarted();

        void onLoadingFinished();
    }

    //query android repos
    private String query = "android";
    private String language = "java";
    private String queryString = "%s+language:%s";

    private BaseAttacher mBaseAttacher;
    int currentPage = 1;
    boolean isLoading = false;

    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView mRecyclerView;
    RepoAdapter mRepoAdapter;
    OnLoadingListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnLoadingListener) {
            mListener = (OnLoadingListener) activity;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL,
                false);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mRepoAdapter = new RepoAdapter());
        loadData(query, language, currentPage);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBaseAttacher = Mugen.with(mRecyclerView, new MugenCallbacks() {
            @Override
            public void onLoadMore() {
                if (currentPage <= 5) {
                    loadData(query, language, currentPage + 1);
                }
            }

            @Override
            public void scrollDirection(ScrollDirection direction) {
                switch (direction) {
                    case UP:
                        // Scrolling up
                        Log.d("Scroll", "UP");
                        break;
                    case DOWN:
                        // Scrolling down
                        Log.d("Scroll", "DOWN");
                        break;
                    default:
                        // No Scroll
                        break;
                }
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }

            @Override
            public boolean hasLoadedAllItems() {
                return false;
            }
        }).start();

    }

    private void loadData(final String query, final String language, final int page) {
        new AsyncTask<Integer, Void, List<GitHubClient.Repo>>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (mListener != null) {
                    //to demo loading..
                    mListener.onLoadingStarted();
                }
            }

            @Override
            protected List<GitHubClient.Repo> doInBackground(Integer... params) {
                String q = String.format(Locale.ENGLISH,
                        queryString,
                        query,
                        language);
                isLoading = true;
                return GitHubClient.getClient()
                        .searchRepos(q,
                                GitHubClient.DEFAULT_SORT,
                                GitHubClient.DEFAULT_ORDER,
                                params[0]).repos;
            }

            @Override
            protected void onPostExecute(List<GitHubClient.Repo> repos) {
                isLoading = false;
                if (repos != null) {
                    mRepoAdapter.onNext(repos, page);
                }

                if (mListener != null) {
                    //to demo loading finished..
                    mListener.onLoadingFinished();
                }

                currentPage = page;
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }.execute(page);
    }

    @Override
    public void onRefresh() {
        loadData(query, language, 1);
    }

    private static class RepoAdapter extends RecyclerView.Adapter<RepoHolder> {

        LinkedHashMap<Integer, List<GitHubClient.Repo>> repoMap;
        List<GitHubClient.Repo> repoList;

        public RepoAdapter() {
            repoMap = new LinkedHashMap<Integer, List<GitHubClient.Repo>>();
            repoList = new ArrayList<GitHubClient.Repo>();
        }

        @Override
        public RepoHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_repo, viewGroup, false);
            return new RepoHolder(view);
        }

        @Override
        public void onBindViewHolder(RepoHolder repoHolder, int i) {
            GitHubClient.Repo repo = getItem(i);
            if (repo == null) {
                return;
            }
            repoHolder.loadRepo(repo);
        }

        private GitHubClient.Repo getItem(int position) {
            int listSize = 0;

            if (repoList.size() > position) {
                return repoList.get(position);
            }

            repoList = new ArrayList<GitHubClient.Repo>();
            for (List<GitHubClient.Repo> list : repoMap.values()) {
                repoList.addAll(list);
                listSize = listSize + list.size();
                if (listSize > position) {
                    break;
                }
            }
            if (repoList.size() > 0) {
                return repoList.get(position);
            }
            return null;
        }

        @Override
        public int getItemCount() {
            int count = 0;
            for (List<GitHubClient.Repo> list : repoMap.values()) {
                count = count + list.size();
            }
            return count;
        }

        public void onNext(List<GitHubClient.Repo> repos, int page) {
            if (repos == null) {
                return;
            }
            repoMap.put(page, repos);
            notifyDataSetChanged();
        }
    }

    private static class RepoHolder extends RecyclerView.ViewHolder {

        ImageView imageAvatar;
        TextView textRepo;
        TextView textUser;
        TextView textStars;
        TextView textForks;

        public RepoHolder(View itemView) {
            super(itemView);
            imageAvatar = (ImageView) itemView.findViewById(R.id.imageView_avatar);
            textRepo = (TextView) itemView.findViewById(R.id.textView_repo_name);
            textUser = (TextView) itemView.findViewById(R.id.textView_user_name);
            textStars = (TextView) itemView.findViewById(R.id.textView_stars);
            textForks = (TextView) itemView.findViewById(R.id.textView_forks);
            ((ImageView) itemView.findViewById(R.id.imageView_triangle)).
                    setColorFilter(itemView
                            .getContext()
                            .getResources()
                            .getColor(R.color.blue_light));
        }

        public void loadRepo(GitHubClient.Repo repo) {
            textRepo.setText(repo.name);
            textUser.setText(repo.owner.login);
            textStars.setText(repo.starsGazers + "");
            textForks.setText(repo.forks + "");

            String imgUrl = repo.owner.avatarUrl;
            if (imgUrl != null && !imgUrl.equals("")) {
                Picasso.with(imageAvatar.getContext())
                        .load(imgUrl)
                        .resize(200, 200)
                        .error(R.drawable.ic_github_placeholder)
                        .placeholder(R.drawable.ic_github_placeholder)
                        .centerCrop()
                        .into(imageAvatar);
            }
        }
    }
}
