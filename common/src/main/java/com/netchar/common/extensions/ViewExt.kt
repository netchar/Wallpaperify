package com.netchar.common.extensions

import android.view.View
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.netchar.common.base.callbacs.IOnDropdownSelectedListener


/**
 * Created by Netchar on 17.03.2019.
 * e.glushankov@gmail.com
 */

fun View.toVisible() {
    visibility = View.VISIBLE
}

fun View.toInvisible() {
    visibility = View.INVISIBLE
}

fun View.toGone() {
    visibility = View.GONE
}

fun View.booleanVisibility(visible: Boolean) = if (visible) toVisible() else toGone()

fun View.inverseBooleanVisibility(visible: Boolean) = booleanVisibility(!visible)

fun View.isVisible() = visibility == View.VISIBLE

fun Fragment.showSnackbar(message: String, timeLength: Int) {
    activity?.let { Snackbar.make(it.findViewById<View>(android.R.id.content), message, timeLength).show() }
}

fun Fragment.showToast(message: String, length: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this.context, message, length).show()
}

inline fun <reified T : View> T.postAction(action: T.() -> Unit) {
    action()
}

fun ImageView.fitWidth(imageWidth: Int, imageHeight: Int) {
    val scaleFactor = imageWidth.toFloat() / imageHeight
    minimumHeight = (resources.displayMetrics.widthPixels / scaleFactor).toInt()
}

fun Spinner.setOnDropdownItemSelectedListener(callback: IOnDropdownSelectedListener) {
    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {
            /* ignore */
        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            callback.onDropdownItemSelected(position, id)
        }
    }
}

fun RecyclerView.detachAdapter() {
    adapter = null
}