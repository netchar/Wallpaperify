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

package com.netchar.wallpaperify.ui.photosdetails

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.netchar.wallpaperify.R
import kotlinx.android.synthetic.main.dialog_download.*


class DownloadDialogFragment : DialogFragment() {

    var onDialogCancel: (() -> Unit)? = null

    var isDownloadFinished: Boolean = false
        set(value) {
            setProgress(0f)
            field = value
        }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = View.inflate(context, R.layout.dialog_download, null)
        return AlertDialog.Builder(activity!!)
                .setTitle(getString(R.string.title_downloading))
                .setNegativeButton(getString(R.string.label_cancel), null)
                .setView(view)
                .create()
    }

    fun setProgress(progress: Float) {
        dialog?.run {
            photo_details_dialog_pv_progress.isIndeterminate = false
            photo_details_dialog_pv_progress.progress = progress.toInt()
            photo_details_dialog_tv_progress.text = getString(R.string.photo_details_dialog_progress_status, progress.toString())
        }
    }

    override fun show(manager: FragmentManager, tag: String?) {
        if (!isAdded) {
            super.show(manager, tag)
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        if (!isDownloadFinished) {
            onDialogCancel?.invoke()
        }
        isDownloadFinished = false
    }

    override fun dismiss() {
        if (isAdded) {
            super.dismiss()
        }
    }
}