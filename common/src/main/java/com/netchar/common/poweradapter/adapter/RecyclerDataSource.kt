package com.netchar.common.poweradapter.adapter

import androidx.annotation.LayoutRes
import androidx.annotation.MainThread
import androidx.annotation.VisibleForTesting
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.netchar.common.poweradapter.item.IRecyclerItem
import com.netchar.common.poweradapter.item.ItemRenderer
import java.lang.ref.WeakReference

/**
 * Created by Netchar on 02.04.2019.
 * e.glushankov@gmail.com
 */

class RecyclerDataSource(private val renderers: List<ItemRenderer>) {
    private val data = arrayListOf<IRecyclerItem>()
    private lateinit var adapterReference: WeakReference<RecyclerView.Adapter<RecyclerViewHolder>>

    @MainThread
    fun setData(newData: List<IRecyclerItem>) {
        val diffResult = DiffUtil.calculateDiff(RecyclerDiffCallback(data, newData))
        seedData(newData)
        val adapter = adapterReference.get()
        adapter?.let {
            diffResult.dispatchUpdatesTo(it)
        }
    }

    fun getRendererFor(@LayoutRes layoutId: Int): ItemRenderer {
        return renderers.find { it.layoutRes() == layoutId } ?: throw IllegalArgumentException("Unable to find appropriate renderer.")
    }

    @LayoutRes
    fun getLayoutResFor(position: Int): Int {
        val renderKey = data[position].getRenderKey()
        val renderer = renderers.find { it.renderKey == renderKey } ?: throw IllegalArgumentException("Unable to find renderer for model.rendererKey: $renderKey")
        return renderer.layoutRes()
    }

    fun getCount() = data.size

    fun getItem(position: Int) = data[position]

    fun attach(adapter: RecyclerView.Adapter<RecyclerViewHolder>) {
        adapterReference = WeakReference(adapter)
    }

    /**
     * Allows us to set data without invoking DiffUtil which would throw an exception during unit testing.
     */
    @VisibleForTesting
    internal fun seedData(data: List<IRecyclerItem>) {
        this.data.clear()
        this.data.addAll(data)
    }
}