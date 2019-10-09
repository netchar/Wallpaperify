package com.netchar.wallpaperify.ui.about

import android.os.Bundle
import android.text.util.Linkify
import android.view.View
import androidx.core.text.toSpannable
import com.netchar.common.UNSPLASH_URL
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
        fragmentToolbar?.applyWindowInsets()
        setupViews()
    }

    private fun setupViews() {
        val spannableAppInfoText = viewModel.getAppInfoText().toSpannable()

        about_txt_version.text = getString(R.string.label_version, viewModel.getVersionName())
        about_txt_app_info.text = spannableAppInfoText.apply { Linkify.addLinks(this, Pattern.compile("unsplash.com"), UNSPLASH_URL) }
    }
}
