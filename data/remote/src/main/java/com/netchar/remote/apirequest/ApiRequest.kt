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

package com.netchar.remote.apirequest


/**
 * Created by Netchar on 09.05.2019.
 * e.glushankov@gmail.com
 */
class ApiRequest {

    enum class Order(val value: String) {
        LATEST("latest"),
        OLDEST("oldest"),
        POPULAR("popular");

        companion object {
            fun getBy(position: Int) = values()[position]
        }
    }

    data class Photos(val page: Int = 1, val order: Order = Order.LATEST, val perPage: Int = 30)
    data class Collections(val page: Int = 1, val perPage: Int = 15)
    data class Collection(val id: Int, val page: Int = 1, val perPage: Int = 15)
}