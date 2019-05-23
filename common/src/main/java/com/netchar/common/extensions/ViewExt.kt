package com.netchar.common.extensions

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
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

fun TextView.goneIfEmpty() = if (this.text.isNullOrEmpty()) this.toGone() else this.toVisible()

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

fun View.closeSoftKeyboard() {
    val imm = this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(this.windowToken, 0)
}

fun View.requestApplyInsetsWhenAttached() {
    if (isAttachedToWindow) {
        // We're already attached, just request as normal
        requestApplyInsets()
    } else {
        // We're not attached to the hierarchy, add a listener to
        // request when we are
        addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                v.removeOnAttachStateChangeListener(this)
                v.requestApplyInsets()
            }

            override fun onViewDetachedFromWindow(v: View) = Unit
        })
    }
}