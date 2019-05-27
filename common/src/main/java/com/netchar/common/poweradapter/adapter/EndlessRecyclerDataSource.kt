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
        private val onLoadMore: () -> Unit
) : RecyclerDataSource(renderers) {

    private val loadingItem = LoadingItem()

    init {
        renderers.add(LoadingItemRenderer {
            hideRetryItem()
            onLoadMore()
        })
    }

    fun showRetryItem() {
        loadingItem.showRetry()
        notifyItemChanged(loadingItem)
    }

    private fun hideRetryItem() {
        loadingItem.hideRetry()
        notifyItemChanged(loadingItem)
    }

    override fun setData(newData: List<IRecyclerItem>) {
        super.setData(newData.plus(loadingItem))
    }

    override fun attach(adapter: RecyclerView.Adapter<RecyclerViewHolder>) {
        if (adapter is EndlessRecyclerAdapter) {
            adapter.loadMore = onLoadMore
        }
        super.attach(adapter)
    }

    private class LoadingItemRenderer(val onRetryClick: () -> Unit) : ItemRenderer() {
        override val renderKey: String = LoadingItem::class.java.simpleName

        override fun layoutRes(): Int = R.layout.view_recycler_load_more_loading

        override fun bind(itemView: View, item: IRecyclerItem) {
            val loadingItem = item as LoadingItem
            if (loadingItem.isRetryVisible) {
                itemView.load_more_retry_group.toVisible()
                itemView.load_more_item_loading.toGone()
            } else {
                itemView.load_more_retry_group.toGone()
                itemView.load_more_item_loading.toVisible()
            }

            itemView.load_more_retry_group.setOnClickListener {
                onRetryClick()
            }
        }
    }

    private class LoadingItem : IRecyclerItem {
        // needed to every time to calculate new random
        // to make diffUtil work with appropriate insert notification for this item
        override fun getId(): Long = UUID.randomUUID().hashCode().toLong()

        override fun getRenderKey(): String = LoadingItem::class.java.simpleName

        var isRetryVisible: Boolean = false
            private set

        fun showRetry() {
            isRetryVisible = true
        }

        fun hideRetry() {
            isRetryVisible = false
        }
    }

    private fun notifyItemChanged(item: IRecyclerItem) = adapterReference.get()?.notifyItemChanged(data.indexOf(item))
}