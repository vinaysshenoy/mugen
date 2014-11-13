package com.mugen.attachers;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.mugen.MugenCallbacks;
import com.mugen.ScrollDirection;

/**
 * Class that attaches to a RecyclerView instance and provides Load More functionality
 * <p/>
 * Created by vinaysshenoy on 31/10/14.
 */
public class RecyclerViewAttacher extends BaseAttacher<RecyclerView, RecyclerView.OnScrollListener> {

    private LinearLayoutManager mLinearLayoutManager;

    public RecyclerViewAttacher(final RecyclerView recyclerView, final MugenCallbacks callbacks) {
        super(recyclerView, callbacks);
    }

    @Override
    protected void init() {
        if (mAdapterView.getLayoutManager() instanceof LinearLayoutManager) {
            mLinearLayoutManager = (LinearLayoutManager) mAdapterView.getLayoutManager();
        } else {
            throw new IllegalStateException("Mugen currently supports only LinearLayoutManagers");
        }
        mAdapterView.setOnScrollListener(onScrollListener);
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
                mPrevFirstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();
            } else {
                final int firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();
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

                    final int totalItemCount = mLinearLayoutManager.getItemCount();
                    final int firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();
                    final int visibleItemCount = Math.abs(mLinearLayoutManager.findLastVisibleItemPosition() - firstVisibleItem);
                    final int lastAdapterPosition = totalItemCount - 1;
                    final int lastVisiblePosition = (firstVisibleItem + visibleItemCount) - 1;
                    if (lastVisiblePosition >= (lastAdapterPosition - mTriggerOffset)) {
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
