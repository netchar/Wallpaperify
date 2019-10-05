/*
 * Copyright © 2019 Eugene Glushankov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netchar.common.poweradapter.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.netchar.common.R
import com.netchar.common.extensions.toGone
import com.netchar.common.extensions.toVisible
import com.netchar.common.poweradapter.item.IRecyclerItem
import com.netchar.common.poweradapter.item.ItemRenderer
import kotlinx.android.synthetic.main.view_recycler_load_more_loading.view.*
import java.util.*

class EndlessRecyclerDataSource(
        renderers: MutableList<ItemRenderer>,
        private val onRetry: () -> Unit,
        private val totalItems: Int = -1
) : RecyclerDataSource(renderers) {
    companion object {
        private val loadingItem = LoadingItem()
    }

    fun setState(state: State) {
        loadingItem.mode = when (state) {
            State.LOADING -> LoadingItemRenderer.MODE_LOADING
            State.ERROR -> LoadingItemRenderer.MODE_RETRY
        }
        notifyItemChanged(loadingItem)
    }

    override fun setData(newData: List<IRecyclerItem>) {
        var items = newData
        if (isEndlessList() || hasMoreItemsToLoad(newData)) {
            items = newData.plus(loadingItem)
        }

        super.setData(items)
    }

    private fun isEndlessList(): Boolean = totalItems == -1

    private fun hasMoreItemsToLoad(items: List<IRecyclerItem>): Boolean {
        return items.count() < totalItems
    }

    override fun attach(adapter: RecyclerView.Adapter<RecyclerViewHolder>) {
        val loadingItemRenderer = LoadingItemRenderer(onRetryClick = {
            setState(State.LOADING)
            onRetry()
        })
        addRenderer(loadingItemRenderer)
        super.attach(adapter)
    }

    private class LoadingItemRenderer(val onRetryClick: () -> Unit) : ItemRenderer() {
        companion object {
            const val MODE_LOADING = 1
            const val MODE_RETRY = 2
        }

        override val renderKey: String = LoadingItem::class.java.simpleName

        override fun layoutRes(): Int = R.layout.view_recycler_load_more_loading

        override fun bind(itemView: View, item: IRecyclerItem) {
            val loadingItem = item as LoadingItem

            when (loadingItem.mode) {
                MODE_LOADING -> {
                    itemView.load_more_retry_group.toGone()
                    itemView.load_more_item_loading.toVisible()
                }
                MODE_RETRY -> {
                    itemView.load_more_retry_group.toVisible()
                    itemView.load_more_item_loading.toGone()
                    itemView.load_more_retry_group.setOnClickListener {
                        onRetryClick()
                    }
                }
            }
        }
    }

    private class LoadingItem : IRecyclerItem {
        // needed to every time to calculate new random
        // to make diffUtil work with appropriate insert notification for this item
        override fun getId(): Long = UUID.randomUUID().hashCode().toLong()

        override fun getRenderKey(): String = LoadingItem::class.java.simpleName

        var mode: Int = LoadingItemRenderer.MODE_LOADING
    }

    private fun notifyItemChanged(item: IRecyclerItem) = adapterReference.get()?.notifyItemChanged(data.indexOf(item))

    enum class State {
        LOADING,
        ERROR
    }
}