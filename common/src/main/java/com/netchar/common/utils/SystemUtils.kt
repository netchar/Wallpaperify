package com.netchar.common.utils

import android.app.Activity
import androidx.core.app.ShareCompat


/**
 * Created by Netchar on 08.05.2019.
 * e.glushankov@gmail.com
 */
//
//fun Any.getJsonFromResourceFile(fileName: String): String {
//    val uri = this.javaClass.classLoader?.getResource(fileName)
//    uri?.let {
//        val file = File(uri.path)
//        return String(file.readBytes())
//    } ?: throw Resources.NotFoundException("File not found")
//}

fun Activity.share(url: String, chooserTitle: String) {
    val shareIntent = ShareCompat.IntentBuilder
        .from(this)
        .setType("text/plain")
        .setChooserTitle(chooserTitle)
        .setText(url)
        .intent

    if (shareIntent.resolveActivity(packageManager) != null) {
        startActivity(shareIntent)
    } else {
        // unable to find resolve activity to share url
        // todo: firebase analytics
    }
}