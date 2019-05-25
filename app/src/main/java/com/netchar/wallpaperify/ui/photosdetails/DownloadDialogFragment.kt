package com.netchar.wallpaperify.ui.photosdetails

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.netchar.common.extensions.toast
import com.netchar.wallpaperify.R
import kotlinx.android.synthetic.main.view_dialog_download.*


class DownloadDialogFragment : DialogFragment() {

    var onDialogCancel: (() -> Unit)? = null

    var isDownloadFinished: Boolean = false
        set(value) {
            setProgress(0f)
            field = value
        }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = View.inflate(context, R.layout.view_dialog_download, null)
        return AlertDialog.Builder(activity!!)
            .setTitle(getString(R.string.title_downloading))
            .setNegativeButton(getString(R.string.label_cancel), null)
            .setView(view)
            .create()
    }

    fun setProgress(progress: Float) {
        dialog?.run {
            photo_details_dialog_pv_progress.progress = progress.toInt()
            photo_details_dialog_tv_progress.text = getString(R.string.photo_details_dialog_progress_status, progress.toString())
        }
    }

    override fun dismiss() {
        super.dismiss()

        if (isDownloadFinished) {
            return
        }

        activity?.toast(getString(R.string.message_canceled))
        onDialogCancel?.invoke()
        isDownloadFinished = false
    }
}