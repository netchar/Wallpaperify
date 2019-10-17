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

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.netchar.common.MAIL_ADDRESS_DEVELOPER_ACCOUNT
import com.netchar.common.URL_BUG_TRACKING
import com.netchar.common.URL_ROADMAP
import com.netchar.common.services.IExternalAppService
import com.netchar.wallpaperify.R
import javax.inject.Inject

class FeedbackViewModel @Inject constructor(
        private val externalApp: IExternalAppService
) : ViewModel() {
    private val _items = MutableLiveData<List<FeedbackItem>>()

    init {
        initialize()
    }

    val items: LiveData<List<FeedbackItem>> get() = _items

    fun sendEmail() {
        externalApp.composeEmail(MAIL_ADDRESS_DEVELOPER_ACCOUNT, "Wallpaperify Feedback")
    }

    fun openBugTrackingPage() {
        externalApp.openWebPage(URL_BUG_TRACKING)
    }

    fun openRoadmapPage() {
        externalApp.openWebPage(URL_ROADMAP)
    }

    private fun initialize() {
        val elements = listOf(
                FeedbackItem(FeedbackItem.GET_IN_TOUCH, R.string.item_title_feedback_get_in_touch, R.string.item_description_feedback_get_in_touch, R.drawable.ic_gmail),
                FeedbackItem(FeedbackItem.REPORT_BUG, R.string.item_title_feedback_report_bug, R.string.item_description_feedback_report_bug, R.drawable.ic_bug_report),
                FeedbackItem(FeedbackItem.ROADMAP, R.string.item_title_feedback_roadmap, R.string.item_description_feedback_roadmap, R.drawable.ic_strategy)
        )
        _items.value = elements
    }
}