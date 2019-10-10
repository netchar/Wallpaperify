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

package com.netchar.wallpaperify.ui.about

import android.os.Bundle
import android.text.util.Linkify
import android.view.View
import androidx.core.text.toSpannable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.netchar.common.UNSPLASH_URL
import com.netchar.common.base.BaseFragment
import com.netchar.common.extensions.applyWindowInsets
import com.netchar.common.extensions.injectViewModel
import com.netchar.common.extensions.toast
import com.netchar.wallpaperify.R
import com.netchar.wallpaperify.di.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_about.*
import java.util.regex.Pattern
import javax.inject.Inject

class AboutFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: AboutViewModel

    override val layoutResId: Int = R.layout.fragment_about

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = injectViewModel(viewModelFactory)
        toolbar.applyWindowInsets()
        setupViews()
    }

    private fun setupViews() {
        val spannableAppInfoText = getString(R.string.fragment_about_app_info_text).toSpannable()

        about_txt_version.text = getString(R.string.label_version, viewModel.getVersionName())
        about_txt_app_info.text = spannableAppInfoText.apply { Linkify.addLinks(this, Pattern.compile("unsplash.com"), UNSPLASH_URL) }

        Glide.with(this)
                .load(R.drawable.img_developer)
                .transform(CircleCrop())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(about_img_author_image)

        about_txt_privacy_policy.setOnClickListener { toast("privacy") }
        about_txt_external_libraries_licences.setOnClickListener { toast("licences") }
        about_ctxt_instagram.setOnClickListener { viewModel.redirectToInstagramAcc() }
        about_txt_linkedin.setOnClickListener { viewModel.redirectToLinkedInAcc() }
        about_txt_gmail_address.setOnClickListener { viewModel.sendEmail() }
    }
}
