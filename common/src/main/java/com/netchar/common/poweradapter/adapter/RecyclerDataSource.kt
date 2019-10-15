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

import androidx.annotation.LayoutRes
import androidx.annotation.MainThread
import androidx.annotation.VisibleForTesting
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.netchar.common.poweradapter.item.IRecyclerItem
import com.netchar.common.poweradapter.item.ItemRenderer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel.Factory.CONFLATED
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference
import kotlin.coroutines.CoroutineContext

open class RecyclerDataSource(
        private val renderers: MutableList<ItemRenderer>
) : CoroutineScope {
    protected val data = mutableListOf<IRecyclerItem>()
    protected lateinit var adapterReference: WeakReference<RecyclerView.Adapter<RecyclerViewHolder>>
        private set

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    private val diffCallback by lazy(LazyThreadSafetyMode.NONE) {
        RecyclerDiffCallback()
    }

    private val updateActor = actor<List<IRecyclerItem>>(capacity = CONFLATED) {
        for (list in channel) internalUpdate(list)
    }

    fun addRenderer(newRenderer: ItemRenderer) {
        renderers.add(newRenderer)
    }

    @MainThread
    open fun submit(newData: List<IRecyclerItem>) {
        updateActor.offer(newData)
    }

    @MainThread
    private suspend fun internalUpdate(newData: List<IRecyclerItem>) {
        val result = calculateDiffAsync(newData)
        seedData(newData)
        val adapter = adapterReference.get()
        adapter?.let {
            result.dispatchUpdatesTo(it)
        }
    }

    private suspend fun calculateDiffAsync(newData: List<IRecyclerItem>): DiffUtil.DiffResult {
        return withContext(Dispatchers.Default) {
            diffCallback.update(data, newData)
            DiffUtil.calculateDiff(diffCallback)
        }
    }

    fun getRendererFor(@LayoutRes layoutId: Int): ItemRenderer {
        return renderers.find { it.layoutRes() == layoutId } ?: throw IllegalArgumentException("Unable to find appropriate renderer.")
    }

    @LayoutRes
    fun getLayoutResFor(position: Int): Int {
        val renderKey = getItem(position).getRenderKey()
        val renderer = renderers.find { it.renderKey == renderKey } ?: throw IllegalArgumentException("Unable to find renderer for model.rendererKey: $renderKey")
        return renderer.layoutRes()
    }

    fun getCount() = data.size

    fun getItem(position: Int) = data[position]

    open fun attach(adapter: RecyclerView.Adapter<RecyclerViewHolder>) {
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