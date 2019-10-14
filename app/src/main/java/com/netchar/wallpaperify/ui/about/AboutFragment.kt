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
import android.webkit.WebView
import androidx.appcompat.app.AlertDialog
import androidx.core.text.toSpannable
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.netchar.common.URL_UNSPLASH
import com.netchar.common.base.BaseFragment
import com.netchar.common.extensions.applyWindowInsets
import com.netchar.common.extensions.injectViewModel
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
        about_txt_app_info.text = spannableAppInfoText.apply { Linkify.addLinks(this, Pattern.compile("unsplash.com"), URL_UNSPLASH) }

        Glide.with(this)
            .load(R.drawable.img_developer)
            .transform(CircleCrop())
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(about_img_author_image)

        about_txt_privacy_policy.setOnClickListener { licenceDialog.show() }
        about_txt_external_libraries_licences.setOnClickListener { librariesLicenseDialog.show() }
        about_ctxt_instagram.setOnClickListener { viewModel.openDeveloperInstagramAccount() }
        about_txt_linkedin.setOnClickListener { viewModel.openDeveloperLinkedInAccount() }
        about_txt_gmail_address.setOnClickListener { viewModel.sendEmail() }
    }

    private val librariesLicenseDialog: AlertDialog by lazy {
        fun createDialogView(): View? {
            val context = this.context ?: return null
            return RecyclerView(context).apply {
                layoutManager = LinearLayoutManager(context)
                setHasFixedSize(true)
                adapter = LicenceAdapter { viewModel.openExternalLicenceFor(it) }.also { it.submitList(viewModel.getLibraries()) }
            }
        }

        AlertDialog.Builder(baseContext).apply {
            setCancelable(false)
            setTitle("Libraries")
            setView(createDialogView())
            setPositiveButton(getString(R.string.label_ok)) { dialog, _ -> dialog.dismiss() }
        }.create()
    }

    private val licenceDialog: AlertDialog by lazy {
        fun createView(): View {
            return WebView(activity).apply { loadUrl("file:///android_asset/privacy_policy.html") }
        }

        AlertDialog.Builder(baseContext).apply {
            setView(createView())
            setPositiveButton(getString(R.string.label_ok)) { dialog, _ -> dialog.dismiss() }
        }.create()
    }
}
