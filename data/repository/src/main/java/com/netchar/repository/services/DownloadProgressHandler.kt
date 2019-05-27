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

import android.os.Handler
import android.os.Message
import com.netchar.common.utils.weak

class DownloadProgressHandler(service: DownloadService) : Handler() {
    private val downloadService by weak(service)

    override fun handleMessage(msg: Message) {
        if (msg.what == DownloadService.DOWNLOAD_MANAGER_MESSAGE_ID) {
            downloadService?.updateProgressStatus()
        }
    }
}