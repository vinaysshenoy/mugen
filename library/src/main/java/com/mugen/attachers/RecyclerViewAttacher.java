package com.mugen.attachers;

import android.support.v7.widget.RecyclerView;

import com.mugen.MugenCallbacks;
import com.mugen.ScrollDirection;

/**
 * Class that attaches to a RecyclerView instance and provides Load More functionality
 * <p></p>
 * Created by vinaysshenoy on 31/10/14.
 */
public class RecyclerViewAttacher extends BaseAttacher<RecyclerView, RecyclerView.OnScrollListener> {

    RecyclerViewPositionHelper mRecyclerViewHelper;

    public RecyclerViewAttacher(final RecyclerView recyclerView, final MugenCallbacks callbacks) {
        super(recyclerView, callbacks);
        mRecyclerViewHelper = RecyclerViewPositionHelper.createHelper(recyclerView);
    }

    @Override
    protected void init() {
        mAdapterView.addOnScrollListener(onScrollListener);
    }

    private final RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            mCurScrollingDirection = null;

            if (mOnScrollListener != null) {
                mOnScrollListener.onScrollStateChanged(recyclerView, newState);
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (mCurScrollingDirection == null) { //User has just started a scrolling motion
            /*
             * Doesn't matter what we set as, the actual setting will happen in
             * the next call to this method
             */
                mCurScrollingDirection = ScrollDirection.SAME;
                mPrevFirstVisibleItem = mRecyclerViewHelper.findFirstVisibleItemPosition();
            } else {
                final int firstVisibleItem = mRecyclerViewHelper.findFirstVisibleItemPosition();
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

                    final int totalItemCount = mRecyclerViewHelper.getItemCount();
                    final int firstVisibleItem = mRecyclerViewHelper.findFirstVisibleItemPosition();
                    final int visibleItemCount = Math.abs(mRecyclerViewHelper.findLastVisibleItemPosition() - firstVisibleItem);
                    final int lastAdapterPosition = totalItemCount - 1;
                    final int lastVisiblePosition = (firstVisibleItem + visibleItemCount) - 1;
                    if (lastVisiblePosition >= (lastAdapterPosition - mLoadMoreOffset)) {
                        mMugenCallbacks.onLoadMore();
                    }

                }
            }
            if (mOnScrollListener != null) {
                mOnScrollListener.onScrolled(recyclerView, dx, dy);
            }
        }
    };
}
