package com.netchar.common.poweradapter.adapter

import androidx.recyclerview.widget.RecyclerView
import com.netchar.common.poweradapter.EndlessRecyclerOnScrollListener


/**
 * Created by Netchar on 27.04.2019.
 * e.glushankov@gmail.com
 */
class EndlessRecyclerAdapter(dataSource: EndlessRecyclerDataSource) : RecyclerAdapter(dataSource) {

    var loadMore: (() -> Unit)? = null

    private val endlessScrollListener by lazy {
        object : EndlessRecyclerOnScrollListener() {
            override fun onLoadMore() {
                loadMore?.invoke()
            }
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.addOnScrollListener(endlessScrollListener)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        recyclerView.removeOnScrollListener(endlessScrollListener)
        if (loadMore != null) {
            loadMore = null
        }
    }
}