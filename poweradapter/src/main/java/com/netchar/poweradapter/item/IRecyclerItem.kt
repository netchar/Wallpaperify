package com.netchar.poweradapter.item

/**
 * Created by Netchar on 02.04.2019.
 * e.glushankov@gmail.com
 */

interface IRecyclerItem {
    fun getId(): Long
    fun getRenderKey(): String
}