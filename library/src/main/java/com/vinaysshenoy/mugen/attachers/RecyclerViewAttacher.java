package com.vinaysshenoy.mugen.attachers;

import android.support.v7.widget.RecyclerView;

/**
 * Class that attaches to a RecyclerView instance and provides Load More functionality
 * <p/>
 * Created by vinaysshenoy on 31/10/14.
 */
public class RecyclerViewAttacher extends BaseAttacher<RecyclerView> {

    public RecyclerViewAttacher(final RecyclerView recyclerView) {
        super(recyclerView);
    }
}
