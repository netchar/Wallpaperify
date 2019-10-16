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

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.netchar.wallpaperify.R
import kotlinx.android.synthetic.main.dialog_purchase_success.view.*

class DonationSuccessDialogFragment : DialogFragment() {
    companion object {
        private const val KEY_MESSAGE = "key_message"

        fun create(message: String) = DonationSuccessDialogFragment().apply {
            arguments = Bundle().apply {
                putString(KEY_MESSAGE, message)
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = View.inflate(context, R.layout.dialog_purchase_success, null)
        val message = arguments?.getString(KEY_MESSAGE) ?: ""

        view.dialog_purchase_txt_message.text = message

        return AlertDialog.Builder(context!!)
                .setView(view)
                .setPositiveButton(getString(R.string.label_close), null)
                .create()
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        dismissAllowingStateLoss()
    }
}