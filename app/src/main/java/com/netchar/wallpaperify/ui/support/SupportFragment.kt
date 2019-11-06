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

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.forEach
import com.netchar.common.base.BaseFragment
import com.netchar.common.extensions.addVerticalDivider
import com.netchar.common.extensions.applyWindowInsets
import com.netchar.common.extensions.closeSoftKeyboard
import com.netchar.common.extensions.injectViewModel
import com.netchar.common.poweradapter.adapter.RecyclerAdapter
import com.netchar.common.poweradapter.adapter.RecyclerDataSource
import com.netchar.wallpaperify.R
import com.netchar.wallpaperify.di.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_support.*
import org.solovyev.android.checkout.*
import javax.inject.Inject


class SupportFragment : BaseFragment() {

    @Inject
    lateinit var factory: ViewModelFactory

    @Inject
    lateinit var billing: Billing

    lateinit var checkout: ActivityCheckout

    private lateinit var viewModel: SupportDevelopmentViewModel

    private val renderers = mutableListOf(
            SimpleDonateRenderer(::onSimpleDonate),
            MillionaireRenderer(::onMillionaireDonate)
    )

    private val source = RecyclerDataSource(renderers)

    override val layoutResId: Int = R.layout.fragment_support

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = injectViewModel(factory)
        toolbar.applyWindowInsets()

        focus_trick.performClick()
        support_development_recycler.setHasFixedSize(true)
        support_development_recycler.adapter = RecyclerAdapter(source)
        support_development_recycler.addVerticalDivider()

//        viewModel.items.observe { entries ->
//            source.submit(entries)
//        }

        subscribeOnClickOutside(view)

        checkout = Checkout.forActivity(activity as Activity, billing)
        checkout.start()

        val request = Inventory.Request.create()
        request.loadAllPurchases()
        request.loadSkus(ProductTypes.IN_APP, "wallpaperify.purchase.inapp.wifi")
        checkout.loadInventory(request, object : Inventory.Callback {
            override fun onLoaded(products: Inventory.Products) {
                val product = products[ProductTypes.IN_APP]
                if (product.supported) {

                }
            }
        })
    }

    private fun onMillionaireDonate(amount: Float) {
        DonationSuccessDialogFragment.create(getString(R.string.message_dialog_donation_success)).show(childFragmentManager, "millionaire_donation$amount")
    }

    private fun onSimpleDonate(id: Int) {
        DonationSuccessDialogFragment.create(getString(R.string.message_dialog_donation_success)).show(childFragmentManager, "donation$id")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        checkout.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unsubscribeOnClickOutside()
        checkout.stop()
    }

    private fun subscribeOnClickOutside(view: View) {
        (view as ViewGroup).forEach { v ->
            v.setOnClickListener {
                focus_trick.requestFocus()
                v.closeSoftKeyboard()
            }
        }
    }

    private fun unsubscribeOnClickOutside() {
        (view as ViewGroup).forEach {
            it.setOnClickListener(null)
        }
    }

}
