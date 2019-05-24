package com.netchar.models.apirequest


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
}