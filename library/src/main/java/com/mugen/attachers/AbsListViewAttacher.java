package com.mugen.attachers;

import android.widget.AbsListView;

import com.mugen.MugenCallbacks;
import com.mugen.ScrollDirection;

/**
 * Class that attaches to a AbsListView instance and performs Load More functionality
 * <p></p>
 * Created by vinaysshenoy on 31/10/14.
 */
public class AbsListViewAttacher extends BaseAttacher<AbsListView, AbsListView.OnScrollListener> {

    public AbsListViewAttacher(final AbsListView absListView, final MugenCallbacks callbacks) {
        super(absListView, callbacks);
    }

    @Override
    protected void init() {
        mAdapterView.setOnScrollListener(onScrollListener);
    }

    private final AbsListView.OnScrollListener onScrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

            mCurScrollingDirection = null;

            if (mOnScrollListener != null) {
                mOnScrollListener.onScrollStateChanged(view, scrollState);
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            if (mCurScrollingDirection == null) { //User has just started a scrolling motion
            /*
             * Doesn't matter what we set as, the actual setting will happen in
             * the next call to this method
             */
                mCurScrollingDirection = ScrollDirection.SAME;
                mPrevFirstVisibleItem = firstVisibleItem;
            } else {
                if (firstVisibleItem > mPrevFirstVisibleItem) {
                    //User is scrolling up
                    mCurScrollingDirection = ScrollDirection.UP;
                } else if (firstVisibleItem < mPrevFirstVisibleItem) {
                    //User is scrolling down
                    mCurScrollingDirection = ScrollDirection.DOWN;
                } else {
                    mCurScrollingDirection = ScrollDirection.SAME;
                }
                mPrevFirstVisibleItem = firstVisibleItem;
            }

            if (mIsLoadMoreEnabled
                    && (mCurScrollingDirection == ScrollDirection.UP)) {
                //We only need to paginate if user scrolling near the end of the list

                if (!mMugenCallbacks.isLoading()
                        && !mMugenCallbacks.hasLoadedAllItems()) {
                    //Only trigger a load more if a load operation is NOT happening AND all the items have not been loaded
                    final int lastAdapterPosition = totalItemCount - 1;
                    final int lastVisiblePosition = (firstVisibleItem + visibleItemCount) - 1;
                    if (lastVisiblePosition >= (lastAdapterPosition - mLoadMoreOffset)) {
                        mMugenCallbacks.onLoadMore();
                    }

                }
            }
            if (mOnScrollListener != null) {
                mOnScrollListener
                        .onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
            }
        }
    };

}
