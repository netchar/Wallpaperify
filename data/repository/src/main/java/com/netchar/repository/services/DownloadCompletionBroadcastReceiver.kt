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

package com.netchar.repository.services

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.netchar.common.utils.weak
import timber.log.Timber

class DownloadCompletionBroadcastReceiver(service: DownloadService) : BroadcastReceiver() {
    private val downloadService by weak(service)

    override fun onReceive(context: Context, intent: Intent) {
        downloadService?.run {
            val intentDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)

            if (intentDownloadId == -1L) {
                unregisterDownloadObservers()
                Timber.w("Unable to EXTRA_DOWNLOAD_ID from Intent")
                return@run
            }

            if (currentDownloadId != intentDownloadId) {
                Timber.w("Wrong download id")
                return@run
            }

            updateProgressStatus()
        }
    }
}