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

import com.netchar.common.poweradapter.item.IRecyclerItem

data class FeedbackItem(
        val id: Int,
        val titleRes: Int,
        val descriptionRes: Int,
        val imageRes: Int
) : IRecyclerItem {
    companion object {
        const val GET_IN_TOUCH = 0
        const val REPORT_BUG = 1
        const val ROADMAP = 2
        const val RENDER_KEY = "row"
    }

    override fun getId(): Long {
        return titleRes.hashCode().toLong()
    }

    override fun getRenderKey(): String {
        return RENDER_KEY
    }
}