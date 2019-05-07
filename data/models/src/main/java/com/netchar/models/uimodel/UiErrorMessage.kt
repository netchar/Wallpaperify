package com.netchar.models.uimodel

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

inline class Message(@StringRes val messageRes: Int?)

data class ErrorMessage(val isVisible: Boolean, val errorMessage: Message, @DrawableRes val errorImageRes: Int? = 0) {
    companion object {
        fun empty() = ErrorMessage(false, Message(null), null)
    }
}