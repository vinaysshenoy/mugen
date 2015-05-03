package com.mugen.attachers;

import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import static android.support.v7.widget.RecyclerView.NO_POSITION;

/**
 * ${CLASS_NAME} com.mugen.attachers.
 */
class RecyclerViewPositionHelper {

    final RecyclerView recyclerView;
    final RecyclerView.LayoutManager layoutManager;

    RecyclerViewPositionHelper(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        this.layoutManager = recyclerView.getLayoutManager();
    }

    public static RecyclerViewPositionHelper createHelper(RecyclerView recyclerView) {
        if (recyclerView == null) {
            throw new NullPointerException("Recycler View is null");
        }
        return new RecyclerViewPositionHelper(recyclerView);
    }

    public int getItemCount() {
        return layoutManager == null ? 0 : layoutManager.getItemCount();
    }

    public int findFirstVisibleItemPosition() {
        final View child = findOneVisibleChild(0, layoutManager.getChildCount(), false, true);
        return child == null ? NO_POSITION : recyclerView.getChildAdapterPosition(child);
    }

    public int findFirstCompletelyVisibleItemPosition() {
        final View child = findOneVisibleChild(0, layoutManager.getChildCount(), true, false);
        return child == null ? NO_POSITION : recyclerView.getChildAdapterPosition(child);
    }

    public int findLastVisibleItemPosition() {
        final View child = findOneVisibleChild(layoutManager.getChildCount() - 1, -1, false, true);
        return child == null ? NO_POSITION : recyclerView.getChildAdapterPosition(child);
    }

    public int findLastCompletelyVisibleItemPosition() {
        final View child = findOneVisibleChild(layoutManager.getChildCount() - 1, -1, true, false);
        return child == null ? NO_POSITION : recyclerView.getChildAdapterPosition(child);
    }

    View findOneVisibleChild(int fromIndex, int toIndex, boolean completelyVisible,
                             boolean acceptPartiallyVisible) {
        OrientationHelper helper;
        if (layoutManager.canScrollVertically()) {
            helper = OrientationHelper.createVerticalHelper(layoutManager);
        } else {
            helper = OrientationHelper.createHorizontalHelper(layoutManager);
        }

        final int start = helper.getStartAfterPadding();
        final int end = helper.getEndAfterPadding();
        final int next = toIndex > fromIndex ? 1 : -1;
        View partiallyVisible = null;
        for (int i = fromIndex; i != toIndex; i += next) {
            final View child = layoutManager.getChildAt(i);
            final int childStart = helper.getDecoratedStart(child);
            final int childEnd = helper.getDecoratedEnd(child);
            if (childStart < end && childEnd > start) {
                if (completelyVisible) {
                    if (childStart >= start && childEnd <= end) {
                        return child;
                    } else if (acceptPartiallyVisible && partiallyVisible == null) {
                        partiallyVisible = child;
                    }
                } else {
                    return child;
                }
            }
        }
        return partiallyVisible;
    }
}
