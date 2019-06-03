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

package com.netchar.wallpaperify.ui.photos

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.netchar.common.extensions.withArgs
import com.netchar.remote.apirequest.ApiRequest
import com.netchar.repository.pojo.FilterOptions
import com.netchar.wallpaperify.R
import kotlinx.android.synthetic.main.dialog_photo_filter.*

class PhotosFilterDialogFragment : BottomSheetDialogFragment() {
    private var options: FilterOptions? = null

    var listener: ((options: FilterOptions) -> Unit)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.dialog_photo_filter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            if (it.containsKey(KEY_SORT_BY)) {
                val sorted = it.getSerializable(KEY_SORT_BY) as ApiRequest.Order
                val checkId = orderToResourceIdMap[sorted]
                if (checkId != null) {
                    photos_filter_chipgroup_sort.check(checkId)
                }
            }
        }

        photos_filter_chipgroup_sort.setOnCheckedChangeListener { _, id ->
            val sortBy = when (id) {
                R.id.photos_filter_sort_option_latest -> ApiRequest.Order.LATEST
                R.id.photos_filter_sort_option_oldest -> ApiRequest.Order.OLDEST
                R.id.photos_filter_sort_option_popular -> ApiRequest.Order.POPULAR
                OPTION_UNSELECTED -> null
                else -> throw UnsupportedOperationException("Unsupported sorting option")
            }

            options = FilterOptions(sortBy)
        }
    }

//    override fun getTheme(): Int {
//        return R.style.Widget_AppTheme_BottomSheet
//    }


    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        options?.let {
            listener?.invoke(it)
            listener = null
        }
    }

    companion object {
        private const val KEY_SORT_BY = "key_sort_by"
        private const val OPTION_UNSELECTED = -1

        private val orderToResourceIdMap = hashMapOf(
                ApiRequest.Order.LATEST to R.id.photos_filter_sort_option_latest,
                ApiRequest.Order.OLDEST to R.id.photos_filter_sort_option_oldest,
                ApiRequest.Order.POPULAR to R.id.photos_filter_sort_option_popular
        )

        fun getInstance(sortBy: ApiRequest.Order? = null) = PhotosFilterDialogFragment().withArgs {
            sortBy?.let {
                putSerializable(KEY_SORT_BY, sortBy)
            }
        }
    }
}