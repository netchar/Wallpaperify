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

package com.netchar.repository.pojo

import android.net.Uri

sealed class Progress {

    enum class ErrorCause {
        UNKNOWN,
        UNEXPECTED_PAUSE,
        CANNOT_RESUME,
        DEVICE_NOT_FOUND,
        FILE_ALREADY_EXISTS,
        FILE_ERROR ,
        HTTP_DATA_ERROR,
        INSUFFICIENT_SPACE,
        TOO_MANY_REDIRECTS,
        UNHANDLED_HTTP_CODE
    }

    data class Success(val fileUri: Uri) : Progress()
    data class Error(val cause: ErrorCause, val message: String = "") : Progress()
    data class Downloading(val progressSoFar: Float) : Progress()
    data class Unknown(val status: String) : Progress()
    data class FileExist(val fileUri: Uri) : Progress()
    object Canceled : Progress()
}