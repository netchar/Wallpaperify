package com.netchar.common.views

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class CompoundDrawableTextView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = android.R.attr.textViewStyle
) : AppCompatTextView(context, attrs, defStyleAttr), IChangebleCompoundDrawable {

    private val compoundDrawableHelper: CompoundDrawableHelper = CompoundDrawableHelper(context, attrs, defStyleAttr, 0)

    init {
        compoundDrawableHelper.apply(this)
    }

    override fun getCompoundDrawableHeight(): Int {
        return compoundDrawableHelper.getCompoundDrawableHeight()
    }

    override fun getCompoundDrawableWidth(): Int {
        return compoundDrawableHelper.getCompoundDrawableWidth()
    }

    override fun setDrawableStartVectorId(id: Int) {
        compoundDrawableHelper.setDrawableStartVectorId(id)
        compoundDrawableHelper.apply(this)
    }

    override fun setDrawableEndVectorId(id: Int) {
        compoundDrawableHelper.setDrawableEndVectorId(id)
        compoundDrawableHelper.apply(this)
    }

    override fun setDrawableTopVectorId(id: Int) {
        compoundDrawableHelper.setDrawableTopVectorId(id)
        compoundDrawableHelper.apply(this)
    }

    override fun setDrawableBottomVectorId(id: Int) {
        compoundDrawableHelper.setDrawableBottomVectorId(id)
        compoundDrawableHelper.apply(this)
    }
}