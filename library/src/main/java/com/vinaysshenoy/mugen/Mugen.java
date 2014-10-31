package com.vinaysshenoy.mugen;

import android.support.v7.widget.RecyclerView;
import android.widget.AbsListView;

import com.vinaysshenoy.mugen.attachers.AbsListViewAttacher;
import com.vinaysshenoy.mugen.attachers.RecyclerViewAttacher;

/**
 * Helper class to detect whenever an {@link android.widget.AbsListView} has to given a Load
 * More implementation.
 *
 * @author vinaysshenoy 31/10/14
 */
public class Mugen {

    private static final String TAG = "Mugen";

    private Mugen() {
        //Default constructor to prevent initialization
    }

    /**
     * Creates a Attacher for AbsListView implementations
     *
     * @param absListView The List for which load more functionality is needed
     */
    public AbsListViewAttacher with(final AbsListView absListView) {
        return new AbsListViewAttacher(absListView);
    }

    /**
     * Creates a Attacher for RecyclerView implementations
     *
     * @param recyclerView The List for which load more functionality is needed
     */
    public RecyclerViewAttacher with(final RecyclerView recyclerView) {
        return new RecyclerViewAttacher(recyclerView);
    }

}
