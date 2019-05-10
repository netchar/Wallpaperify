package com.netchar.common.poweradapter.adapter

import androidx.recyclerview.widget.RecyclerView
import com.netchar.common.poweradapter.EndlessRecyclerViewScrollListener


/**
 * Created by Netchar on 27.04.2019.
 * e.glushankov@gmail.com
 */
class EndlessRecyclerAdapter(dataSource: EndlessRecyclerDataSource) : RecyclerAdapter(dataSource) {

    lateinit var loadMore: (() -> Unit)

    private lateinit var endlessScrollListener: EndlessRecyclerViewScrollListener

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        val layoutManager = recyclerView.layoutManager ?: throw IllegalStateException("LayoutManager must be assigned.")

        endlessScrollListener = object : EndlessRecyclerViewScrollListener(layoutManager) {
            override fun loadMore() {
                loadMore.invoke()
            }
        }

        recyclerView.addOnScrollListener(endlessScrollListener)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        recyclerView.removeOnScrollListener(endlessScrollListener)
    }
}