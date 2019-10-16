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
import com.netchar.common.extensions.toast
import com.netchar.common.poweradapter.item.IRecyclerItem
import com.netchar.common.poweradapter.item.ItemRenderer
import com.netchar.wallpaperify.R
import kotlinx.android.synthetic.main.row_purchase_millionaire.view.*

class MillionaireRenderer(private val onClick: (amount: Float) -> Unit) : ItemRenderer() {
    override val renderKey: String = MillionaireDonationEntry.RENDER_KEY

    override fun layoutRes(): Int {
        return R.layout.row_purchase_millionaire
    }

    override fun bind(itemView: View, item: IRecyclerItem) {
        val model = item as MillionaireDonationEntry
        with(itemView) {
            purchase_millionaire_title.text = resources.getString(model.description)
            purchase_millionaire_et_amount.hint = resources.getString(model.placeholder)
            purchase_millionaire_btn_apply.text = resources.getString(model.buttonName)
            purchase_millionaire_btn_apply.setOnClickListener {
                if (purchase_millionaire_et_amount.text.isNotEmpty()) {
                    try {
                        val amount = purchase_millionaire_et_amount.text.toString().toFloat()
                        onClick.invoke(amount)
                    } catch (e: NumberFormatException) {
                        context.toast("Unable to parse your million dollars")
                    }
                }
            }
        }
    }
}