package com.netchar.wallpaperify.ui.photosdetails

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.netchar.common.extensions.showToast
import com.netchar.wallpaperify.R
import kotlinx.android.synthetic.main.view_dialog_download.*


class DownloadDialogFragment : DialogFragment() {

    var onDialogCancel: (() -> Unit)? = null

    var isDownloadFinished: Boolean = false
        set(value) {
            setProgress(0f)
            field = value
        }

    lateinit var v: View

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(context).inflate(R.layout.view_dialog_download, null, false)

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

        showToast(getString(R.string.message_canceled))
        onDialogCancel?.invoke()
        isDownloadFinished = false
    }
}