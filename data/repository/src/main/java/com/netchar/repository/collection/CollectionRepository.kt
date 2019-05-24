package com.netchar.repository.collection

import com.netchar.common.utils.CoroutineDispatchers
import com.netchar.models.Collection
import com.netchar.models.apirequest.ApiRequest
import com.netchar.remote.api.CollectionsApi
import com.netchar.repository.IBoundResource
import com.netchar.repository.NetworkBoundResource
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject


/**
 * Created by Netchar on 09.05.2019.
 * e.glushankov@gmail.com
 */
class CollectionRepository @Inject constructor(
        private val collectionsApi: CollectionsApi,
        private val coroutineDispatchers: CoroutineDispatchers
) : ICollectionRepository {

    override fun getCollections(request: ApiRequest.Collections, scope: CoroutineScope): IBoundResource<List<Collection>> {
        return NetworkBoundResource(coroutineDispatchers, apiCall = { collectionsApi.getCollectionsAsync(request.page, request.perPage) }).launchIn(scope)
    }
}