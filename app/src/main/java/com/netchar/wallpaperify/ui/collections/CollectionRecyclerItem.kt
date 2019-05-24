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

package com.netchar.wallpaperify.ui.collections

import com.netchar.common.poweradapter.item.IRecyclerItem
import com.netchar.repository.pojo.CollectionPOJO

data class CollectionRecyclerItem(val data: CollectionPOJO) : IRecyclerItem {
    override fun getId(): Long = data.hashCode().toLong()
    override fun getRenderKey(): String = CollectionRenderer::class.java.name
}