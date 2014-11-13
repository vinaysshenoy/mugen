package com.mugen.sample;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mugen.Mugen;
import com.mugen.MugenCallbacks;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MainActivity extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        private String query = "android";
        private String language = "java";
        private String queryString = "%s+language:%s";
        int currentPage = 1;

        RecyclerView mRecyclerView;
        RepoAdapter mRepoAdapter;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
            LinearLayoutManager manager = new LinearLayoutManager(getActivity(),
                    LinearLayoutManager.VERTICAL,
                    false);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(manager);
            mRecyclerView.setAdapter(mRepoAdapter = new RepoAdapter(null));
            loadData(query, language, currentPage);
            return rootView;
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            Mugen.with(mRecyclerView, new MugenCallbacks() {
                @Override
                public void onLoadMore() {
                    loadData(query, language, currentPage + 1);
                }

                @Override
                public boolean isLoading() {
                    return true;
                }

                @Override
                public boolean hasLoadedAllItems() {

                    return false;
                }
            });

        }

        private void loadData(final String query, final String language, final int page) {
            new AsyncTask<Integer, Void, List<GitHubClient.Repo>>() {

                @Override
                protected List<GitHubClient.Repo> doInBackground(Integer... params) {
                    String q = String.format(Locale.ENGLISH,
                            queryString,
                            query,
                            language);
                    return GitHubClient.getClient()
                            .searchRepos(q,
                                    GitHubClient.DEFAULT_SORT,
                                    GitHubClient.DEFAULT_ORDER,
                                    params[0]).repos;
                }

                @Override
                protected void onPostExecute(List<GitHubClient.Repo> repos) {
                    mRepoAdapter.onNext(repos);
                    currentPage = page;
                }
            }.execute(page);
        }
    }

    private static class RepoAdapter extends RecyclerView.Adapter<RepoHolder> {

        List<GitHubClient.Repo> repoList;

        public RepoAdapter(List<GitHubClient.Repo> repos) {
            if (repos == null) {
                repos = new ArrayList<GitHubClient.Repo>();
            }
            this.repoList = repos;
        }

        @Override
        public RepoHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_repo, viewGroup, false);
            return new RepoHolder(view);
        }

        @Override
        public void onBindViewHolder(RepoHolder repoHolder, int i) {
            GitHubClient.Repo repo = repoList.get(i);
            repoHolder.textName.setText(repo.name);
        }

        @Override
        public int getItemCount() {
            return repoList.size();
        }

        public void onNext(List<GitHubClient.Repo> repos) {
            if (repos == null) {
                return;
            }
            if (repoList == null) {
                repoList = new ArrayList<GitHubClient.Repo>();
            }
            repoList.addAll(repos);
            notifyDataSetChanged();
        }
    }

    private static class RepoHolder extends RecyclerView.ViewHolder {

        TextView textName;

        public RepoHolder(View itemView) {
            super(itemView);
            textName = (TextView) itemView.findViewById(R.id.textView_name);
        }
    }
}
