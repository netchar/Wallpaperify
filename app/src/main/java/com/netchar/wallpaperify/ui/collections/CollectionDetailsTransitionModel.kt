package com.netchar.wallpaperify.ui.collections

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
data class CollectionDetailsTransitionModel(
        val authorNameViewTransitionName: String,
        val totalCountViewTransitionName: String,
        val titleViewTransitionName: String
) : Parcelable