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

package com.netchar.wallpaperify.ui.feedback

import android.view.View
import com.netchar.common.poweradapter.item.IRecyclerItem
import com.netchar.common.poweradapter.item.ItemRenderer
import com.netchar.wallpaperify.R
import kotlinx.android.synthetic.main.row_two_lines.view.*

class FeedbackRenderer(val onClick: (id: Int) -> Unit) : ItemRenderer() {
    override val renderKey: String = FeedbackItem.RENDER_KEY

    override fun layoutRes(): Int {
        return R.layout.row_two_lines
    }

    override fun bind(itemView: View, item: IRecyclerItem) {
        val model = item as FeedbackItem

        with(itemView) {
            txt_title.text = resources.getString(model.titleRes)
            txt_description.text = resources.getString(model.descriptionRes)
            iv_image.setImageResource(model.imageRes)
            setOnClickListener { onClick.invoke(model.id) }
        }
    }
}