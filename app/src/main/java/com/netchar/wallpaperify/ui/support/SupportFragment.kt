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

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.forEach
import com.netchar.common.base.BaseFragment
import com.netchar.common.extensions.*
import com.netchar.common.poweradapter.adapter.RecyclerAdapter
import com.netchar.common.poweradapter.adapter.RecyclerDataSource
import com.netchar.common.poweradapter.item.IRecyclerItem
import com.netchar.common.poweradapter.item.ItemRenderer
import com.netchar.wallpaperify.R
import com.netchar.wallpaperify.di.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_support.*
import kotlinx.android.synthetic.main.row_purchase.view.*
import kotlinx.android.synthetic.main.row_purchase_millionaire.view.*
import javax.inject.Inject

class SimpleDonateRenderer(private val onClick: (id: Int) -> Unit) : ItemRenderer() {
    override val renderKey: String = SimpleDonationEntry.RENDER_KEY

    override fun layoutRes(): Int {
        return R.layout.row_purchase
    }

    override fun bind(itemView: View, item: IRecyclerItem) {
        val model = item as SimpleDonationEntry
        with(itemView) {
            purchase_txt_title.text = resources.getString(model.titleRes)
            purchase_txt_description.text = resources.getString(model.descriptionRes)
            purchase_txt_amount.text = resources.getString(R.string.donation_item_amount, model.amount)
            setOnClickListener { onClick.invoke(model.id) }
        }
    }
}

class MillionaireRenderer(private val onClick: (amount: Float) -> Unit) : ItemRenderer() {
    override val renderKey: String = MillionaireDonationEntry.RENDER_KEY

    override fun layoutRes(): Int {
        return R.layout.row_purchase_millionaire
    }

    override fun bind(itemView: View, item: IRecyclerItem) {
        val model = item as MillionaireDonationEntry
        with(itemView) {
            purchase_millionaire_title.text = resources.getString(model.description)
            purchase_millionaire_et_amount.hint = resources.getString(model.placeholder)
            purchase_millionaire_btn_apply.text = resources.getString(model.buttonName)
            purchase_millionaire_btn_apply.setOnClickListener {
                if (purchase_millionaire_et_amount.text.isNotEmpty()) {
                    try {
                        val amount = purchase_millionaire_et_amount.text.toString().toFloat()
                        onClick.invoke(amount)
                    } catch (e: NumberFormatException) {
                        context.toast("Unable to parse your million dollars")
                    }
                }
            }
        }
    }
}


class SupportFragment : BaseFragment() {

    @Inject
    lateinit var factory: ViewModelFactory

    private lateinit var viewModel: SupportDevelopmentViewModel

    private val renderers = mutableListOf(
            SimpleDonateRenderer(::onSimpleDonate),
            MillionaireRenderer(::onMillionaireDonate)
    )

    private val source = RecyclerDataSource(renderers)

    override val layoutResId: Int = R.layout.fragment_support

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        viewModel = injectViewModel(factory)
        toolbar.applyWindowInsets()

        focus_trick.performClick()
        support_development_recycler.setHasFixedSize(true)
        support_development_recycler.adapter = RecyclerAdapter(source)
        support_development_recycler.addVerticalDivider()

        viewModel.items.observe { entries ->
            source.submit(entries)
        }

//        (view as ViewGroup).forEach { v ->
//            v.setOnClickListener {
//                focus_trick.performClick()
//                v.closeSoftKeyboard()
//            }
//        }
    }

    private fun onMillionaireDonate(amount: Float) {

    }

    private fun onSimpleDonate(id: Int) {

    }

    override fun onDestroyView() {
        super.onDestroyView()
//        (view as ViewGroup).forEach {
//            it.setOnClickListener(null)
//        }
    }

}
