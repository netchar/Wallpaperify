package com.netchar.common.views

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView
import com.netchar.common.extensions.detachAdapter
import com.netchar.common.poweradapter.EndlessRecyclerViewScrollListener

class EndlessRecyclerView : RecyclerView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)

    private lateinit var endlessScrollListener: EndlessRecyclerViewScrollListener

    lateinit var onLoadMore: () -> Unit

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val layoutManager = layoutManager ?: throw IllegalStateException("LayoutManager must be assigned.")

        endlessScrollListener = object : EndlessRecyclerViewScrollListener(layoutManager) {
            override fun loadMore() {
                onLoadMore.invoke()
            }
        }
        addOnScrollListener(endlessScrollListener)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        removeOnScrollListener(endlessScrollListener)
        detachAdapter()
    }
}