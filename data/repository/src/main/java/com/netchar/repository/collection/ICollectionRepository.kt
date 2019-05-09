package com.netchar.repository.collection

import com.netchar.models.Collection
import com.netchar.models.apirequest.ApiRequest
import com.netchar.repository.IBoundResource
import kotlinx.coroutines.CoroutineScope


/**
 * Created by Netchar on 09.05.2019.
 * e.glushankov@gmail.com
 */
interface ICollectionRepository {
    fun getCollections(request: ApiRequest.Collections, scope: CoroutineScope): IBoundResource<List<Collection>>
}