package com.netchar.common.utils

import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerDrawable


/**
 * Created by Netchar on 09.05.2019.
 * e.glushankov@gmail.com
 */

class ShimmerFactory {
    companion object {
        private const val SHIMMER_HIGHLIGHT_ALPHA = 0.85f
        private const val SHIMMER_BASE_ALPHA = 0.8f
        private const val SHIMMER_AUTOSTART = true
        private const val SHIMMER_DURATION = 1500L
        private const val SHIMMER_INTENSITY = 0.2f

        fun getShimmer(
                highLightAlpha: Float = SHIMMER_HIGHLIGHT_ALPHA,
                baseAlpha: Float = SHIMMER_BASE_ALPHA,
                autoStart: Boolean = SHIMMER_AUTOSTART,
                duration: Long = SHIMMER_DURATION,
                intensity: Float = SHIMMER_INTENSITY
        ): ShimmerDrawable {
            return ShimmerDrawable().apply {
                Shimmer.AlphaHighlightBuilder()
                    .setHighlightAlpha(highLightAlpha)
                    .setBaseAlpha(baseAlpha)
                    .setAutoStart(autoStart)
                    .setDuration(duration)
                    .setIntensity(intensity)
                    .build()
                    .also { setShimmer(it) }
            }
        }
    }
}

