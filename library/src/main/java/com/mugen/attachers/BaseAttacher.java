package com.mugen.attachers;

import com.mugen.MugenCallbacks;
import com.mugen.ScrollDirection;

/**
 * Base class for attachers
 * <p></p>
 * Created by vinaysshenoy on 31/10/14.
 */
public abstract class BaseAttacher<AdapterView, OnScrollListener> {

    public static final int DEFAULT_LOAD_OFFSET = 2;

    protected AdapterView mAdapterView;

    protected OnScrollListener mOnScrollListener;

    /**
     * Whether load more is enabled
     */
    protected boolean mIsLoadMoreEnabled;

    /**
     * The position offset from the bottom of the list at which the load event
     * must be triggered
     */
    protected int mLoadMoreOffset = DEFAULT_LOAD_OFFSET;

    /**
     * Callbacks for trigereing the load events
     */
    protected MugenCallbacks mMugenCallbacks;

    /**
     * Current scrolling direction
     */
    protected ScrollDirection mCurScrollingDirection;

    /**
     * Holds the last first visible item. Used to calculate the scroll direction
     */
    protected int mPrevFirstVisibleItem;

    /**
     * Construct an instance of an attacher which is connected to a particular AdapterView
     *
     * @param adapterView The View with which to do load more
     * @param callbacks   The callbacks which will receive the callback events
     */
    public BaseAttacher(final AdapterView adapterView, final MugenCallbacks callbacks) {
        mAdapterView = adapterView;
        mMugenCallbacks = callbacks;
    }

    /**
     * Whether the load more is enabled
     */
    public boolean isLoadMoreEnabled() {
        return mIsLoadMoreEnabled;
    }

    /**
     * Used to enable or disable the load more functionality
     */
    public void setLoadMoreEnabled(final boolean enabled) {
        mIsLoadMoreEnabled = enabled;
    }

    /**
     * Gets the trigger offset. This is the position from the
     * bottom at which the load more event is triggered
     */
    public int getLoadMoreOffset() {
        return mLoadMoreOffset;
    }

    /**
     * Sets the trigger. This is the position from the bottom at which
     * the load more event is triggered
     */
    public void setLoadMoreOffset(final int loadMoreOffset) {
        mLoadMoreOffset = loadMoreOffset;
    }

    /**
     * Gets the external on scroll listener that will receive scroll events
     */
    public OnScrollListener getOnScrollListener() {
        return mOnScrollListener;
    }

    /**
     * Sets the external on scroll listener that will receive scroll events
     */
    public void setOnScrollListener(final OnScrollListener onScrollListener) {
        mOnScrollListener = onScrollListener;
    }

    /**
     * Sets the trigger offset for when to trigger load more. For example, if the offset is 2, then the
     * load more event is triggered when there are two items towards the end
     *
     * @param offset The offset from the end at which to trigger the load more
     */
    public BaseAttacher loadMoreOffset(final int offset) {
        mLoadMoreOffset = offset;
        return this;
    }

    /**
     * Sets an external OnScrollListener to receive scrol events
     *
     * @param onScrollListener The OnScrollListener which will receive events
     */
    public BaseAttacher scrollListener(final OnScrollListener onScrollListener) {
        mOnScrollListener = onScrollListener;
        return this;
    }

    /**
     * Begin load more on the attached Adapter View
     *
     * @throws java.lang.IllegalStateException If any configuration is incorrect
     */
    public BaseAttacher start() {

        if(mAdapterView == null) {
            throw new IllegalStateException("Adapter View cannot be null");
        }

        if(mMugenCallbacks == null) {
            throw new IllegalStateException("MugenCallbacks cannot be null");
        }

        if(mLoadMoreOffset <= 0) {
            throw new IllegalStateException("Trigger Offset must be > 0");
        }
        mIsLoadMoreEnabled = true;
        init();
        return this;
    }

    protected abstract void init();
}
