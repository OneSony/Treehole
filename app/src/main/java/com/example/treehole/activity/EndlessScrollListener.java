package com.example.treehole.activity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public abstract class EndlessScrollListener extends RecyclerView.OnScrollListener {
    // The minimum amount of items to have below the current scroll position before loading more.
    private int visibleThreshold = 5;
    // The current offset index of data you have loaded.
    private int currentPage = 0;
    // True if we are still waiting for the last set of data to load.
    private boolean isLoading = true;
    // The total number of items in the dataset after the last load.
    private int previousTotalItemCount = 0;

    private LinearLayoutManager mLinearLayoutManager;

    public EndlessScrollListener(LinearLayoutManager linearLayoutManager) {
        this.mLinearLayoutManager = linearLayoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        // Get the total number of items in the dataset
        int totalItemCount = mLinearLayoutManager.getItemCount();

        // Get the last visible item position
        int lastVisibleItemPosition = mLinearLayoutManager.findLastVisibleItemPosition();

        // If we are still waiting for data to load and the total item count has changed
        if (isLoading && totalItemCount > previousTotalItemCount) {
            isLoading = false;
            previousTotalItemCount = totalItemCount;
        }

        // If we are not currently loading data and we have reached the visible threshold
        if (!isLoading && (lastVisibleItemPosition + visibleThreshold) > totalItemCount) {
            // Increment the current page and load more data
            currentPage++;
            onLoadMore(currentPage);
            isLoading = true;
        }
    }

    public abstract void onLoadMore(int page);
}
