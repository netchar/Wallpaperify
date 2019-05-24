package com.netchar.common.poweradapter

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

@Suppress("unused")
abstract class EndlessRecyclerViewScrollListener(private val layoutManager: RecyclerView.LayoutManager) : RecyclerView.OnScrollListener() {
    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private var visibleThreshold = 5

    // The total number of items in the dataset after the last load
    private var previousTotalItemCount = 0

    // True if we are still waiting for the last set of data to load.
    private var loading = false

    init {
        when (layoutManager) {
            is GridLayoutManager -> {
                visibleThreshold *= layoutManager.spanCount
            }
            is StaggeredGridLayoutManager -> {
                visibleThreshold *= layoutManager.spanCount
            }
        }
    }

    private fun getLastVisibleItem(lastVisibleItemPositions: IntArray): Int {
        var maxSize = 0
        for (i in lastVisibleItemPositions.indices) {
            if (i == 0) {
                maxSize = lastVisibleItemPositions[i]
            } else if (lastVisibleItemPositions[i] > maxSize) {
                maxSize = lastVisibleItemPositions[i]
            }
        }
        return maxSize
    }

    // This happens many times a second during a scroll, so be wary of the code place here.
    override fun onScrolled(view: RecyclerView, dx: Int, dy: Int) {
        val adapter = view.adapter ?: return

        // allows to handle only by scroll down
        if (dy < 0) return

        val totalItemCount = layoutManager.itemCount

        if (isDataSetReset(totalItemCount)) {
            resetState()
        }

        if (isLoadingFinished(totalItemCount)) {
            loading = false
            previousTotalItemCount = totalItemCount
        }

        if (isReadyToLoadMore(totalItemCount, adapter.itemCount)) {
            loadMore()
            loading = true
        }
    }

    private fun isReadyToLoadMore(totalItemCount: Int, itemCount: Int): Boolean {
        return !loading
                && isVisibleThresholdReached(totalItemCount)
                && isEnoughItemsToScroll(itemCount)
    }


    private fun isVisibleThresholdReached(totalItemCount: Int): Boolean {
        val lastVisibleItemPosition = getLastVisibleItemPosition()
        return lastVisibleItemPosition + visibleThreshold > totalItemCount
    }

    private fun isEnoughItemsToScroll(itemCount: Int): Boolean {
        return itemCount > visibleThreshold
    }

    private fun isLoadingFinished(totalItemCount: Int): Boolean {
        return loading && isDataSetChanged(totalItemCount)
    }

    private fun isDataSetChanged(totalItemCount: Int): Boolean {
        return totalItemCount > previousTotalItemCount
    }

    private fun getLastVisibleItemPosition(): Int {
        return when (layoutManager) {
            is StaggeredGridLayoutManager -> {
                val lastVisibleItemPositions = layoutManager.findLastVisibleItemPositions(null)
                // get maximum element within the list
                getLastVisibleItem(lastVisibleItemPositions)
            }
            is GridLayoutManager -> {
                layoutManager.findLastVisibleItemPosition()
            }
            is LinearLayoutManager -> layoutManager.findLastVisibleItemPosition()
            else -> 0
        }
    }

    private fun isDataSetReset(totalItemCount: Int) = totalItemCount < previousTotalItemCount

    private fun resetState() {
        this.previousTotalItemCount = 0
        this.loading = true
    }

    abstract fun loadMore()
}