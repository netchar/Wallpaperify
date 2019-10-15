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

import android.os.Bundle
import android.view.View
import com.netchar.common.base.BaseFragment
import com.netchar.common.extensions.applyWindowInsets
import com.netchar.common.extensions.injectViewModel
import com.netchar.common.poweradapter.adapter.RecyclerAdapter
import com.netchar.common.poweradapter.adapter.RecyclerDataSource
import com.netchar.wallpaperify.R
import com.netchar.wallpaperify.di.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_feedback.*
import javax.inject.Inject

class FeedbackFragment : BaseFragment() {
    @Inject
    lateinit var factory: ViewModelFactory
    private lateinit var viewModel: FeedbackViewModel
    private val renderer = FeedbackRenderer(::onClick)
    private val dataSource: RecyclerDataSource = RecyclerDataSource(mutableListOf(renderer))

    override val layoutResId: Int = R.layout.fragment_feedback

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = injectViewModel(factory)

        toolbar.applyWindowInsets()
        feedback_recycler.setHasFixedSize(true)
        feedback_recycler.adapter = RecyclerAdapter(dataSource)

        viewModel.items.observe { items ->
            dataSource.submit(items)
        }
    }

    private fun onClick(id: Int) {
        when (id) {
            FeedbackItem.GET_IN_TOUCH -> viewModel.sendEmail()
            FeedbackItem.REPORT_BUG -> viewModel.openBugTrackingPage()
            FeedbackItem.ROADMAP -> viewModel.openRoadmapPage()
        }
    }
}