package com.netchar.poweradapter.adapter

import androidx.annotation.LayoutRes
import androidx.annotation.MainThread
import androidx.annotation.VisibleForTesting
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.netchar.poweradapter.RecyclerDiffCallback
import com.netchar.poweradapter.item.IRecyclerItem
import com.netchar.poweradapter.item.ItemRenderer
import java.lang.ref.WeakReference


/**
 * Created by Netchar on 02.04.2019.
 * e.glushankov@gmail.com
 */

class RecyclerDataSource(private val renderers: Map<String, ItemRenderer<IRecyclerItem>>) {

    private val viewTypeToRendererKeyMap = hashMapOf<Int, String>()
    private val data = arrayListOf<IRecyclerItem>()
    private lateinit var adapterReference: WeakReference<RecyclerView.Adapter<RecyclerViewHolder>>

    init {
        viewTypeToRendererKeyMap.putAll(renderers.map { it.value.layoutRes() to it.key })
    }

    @MainThread
    fun setData(newData: List<IRecyclerItem>) {
        val diffResult = DiffUtil.calculateDiff(RecyclerDiffCallback(data, newData))
        data.clear()
        data.addAll(newData)
        adapterReference.get()?.let {
            diffResult.dispatchUpdatesTo(it)
        }
    }

    fun rendererForType(viewType: Int): ItemRenderer<IRecyclerItem> {
        return renderers[viewTypeToRendererKeyMap[viewType]] as ItemRenderer<IRecyclerItem>
    }

    @LayoutRes
    fun getLayoutResFor(position: Int): Int {
        val renderKey = data[position].renderKey()
        val itemRenderer = renderers[renderKey]
        return itemRenderer?.layoutRes() ?: throw IllegalArgumentException("Layout Resource not bound.")
    }

    fun getCount(): Int {
        return data.size
    }

    fun getItem(position: Int): IRecyclerItem {
        return data[position]
    }

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