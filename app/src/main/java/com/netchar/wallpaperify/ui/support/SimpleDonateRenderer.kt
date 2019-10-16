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

package com.netchar.wallpaperify.ui.support

import android.view.View
import com.netchar.common.poweradapter.item.IRecyclerItem
import com.netchar.common.poweradapter.item.ItemRenderer
import com.netchar.wallpaperify.R
import kotlinx.android.synthetic.main.row_purchase.view.*

class SimpleDonateRenderer(private val onClick: (id: Int) -> Unit) : ItemRenderer() {
    override val renderKey: String = SimpleDonationEntry.RENDER_KEY

    override fun layoutRes(): Int {
        return R.layout.row_purchase
    }

    override fun bind(itemView: View, item: IRecyclerItem) {
        val model = item as SimpleDonationEntry
        with(itemView) {
            purchase_txt_title.text = resources.getString(model.titleRes)
            purchase_txt_description.text = resources.getString(model.descriptionRes)
            purchase_txt_amount.text = resources.getString(R.string.donation_item_amount, model.amount)
            setOnClickListener { onClick.invoke(model.id) }
        }
    }
}