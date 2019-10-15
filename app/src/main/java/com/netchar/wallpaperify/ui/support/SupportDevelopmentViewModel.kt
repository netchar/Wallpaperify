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

package com.netchar.wallpaperify.ui.support

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.netchar.common.poweradapter.item.IRecyclerItem
import com.netchar.wallpaperify.R
import javax.inject.Inject

class SupportDevelopmentViewModel @Inject constructor() : ViewModel() {
    private val _items = MutableLiveData<List<IRecyclerItem>>()

    init {
        initialize()
    }

    val items: LiveData<List<IRecyclerItem>> get() = _items

    private fun initialize() {
        val elements = listOf(
                SimpleDonationEntry(SimpleDonationEntry.WIFI, R.string.donation_item_title_wifi, R.string.donation_item_description_wifi, R.string.donation_item_amount_wifi),
                SimpleDonationEntry(SimpleDonationEntry.CAR, R.string.donation_item_title_car, R.string.donation_item_description_car, R.string.donation_item_amount_car),
                SimpleDonationEntry(SimpleDonationEntry.CATS, R.string.donation_item_title_cats, R.string.donation_item_description_cats, R.string.donation_item_amount_cats),
                SimpleDonationEntry(SimpleDonationEntry.SOME_FOOD, R.string.donation_item_title_food, R.string.donation_item_description_food, R.string.donation_item_amount_food),
                SimpleDonationEntry(SimpleDonationEntry.DRESSES_FOR_MY_WIFE, R.string.donation_item_tile_wife_dresses, R.string.donation_item_description_wife_dresses, R.string.donation_item_amount_wife_dresses),
                MillionaireDonationEntry(R.string.donation_item_millionaire_title, R.string.donation_item_millionaire_placeholder, R.string.donation_item_millionaire_button_name)
        )

        _items.value = elements
    }

}