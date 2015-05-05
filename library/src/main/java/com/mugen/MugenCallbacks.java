package com.mugen;

/**
 * Callbacks for load more methods
 *
 * @author Vinay S Shenoy
 */
public interface MugenCallbacks {

    /**
     * Callback for when the next set of items should be loaded
     */
    void onLoadMore();

    /**
     * Callback for whether a load operation is currently ongoing
     *
     * @return <code>true</code> if a load operation is happening,
     * <code>false</code> otherwise. If <code>true</code>, load more
     * event won't be triggered
     */
    boolean isLoading();

    /**
     * Callback for whether all items have been loaded
     *
     * @return <code>true</code> if all items have been loaded,
     * <code>false</code> otherwise. If <code>true</code>, load more
     * event won't be triggered
     */
    boolean hasLoadedAllItems();
}