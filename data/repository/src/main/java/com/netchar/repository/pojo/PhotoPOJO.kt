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

package com.netchar.repository.pojo

import com.netchar.common.UNSPLASH_UTM_PARAMETERS

data class PhotoPOJO(
        val id: String = "",
        val width: Int = 0,
        val height: Int = 0,
        val color: String = "",
        val likes: Int = 0,
        val downloads: Int = 0,
        val description: String? = "",
        val user: UserPOJO = UserPOJO(),
        val urls: UrlsPOJO = UrlsPOJO(),
        val links: LinksPOJO = LinksPOJO()
) {
    val shareLink = links.html + UNSPLASH_UTM_PARAMETERS
}