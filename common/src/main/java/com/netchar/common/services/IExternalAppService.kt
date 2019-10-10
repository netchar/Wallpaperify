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

package com.netchar.common.services

import android.net.Uri

interface IExternalAppService {
    enum class ExternalApp(val appName: String, val packageName: String) {
        INSTAGRAM("Instagram", "com.instagram.android"),
        LINKED_IN("LinkedIn", "com.linkedin.android")
    }

    fun sendEmail(subject: String, message: String)

    fun openUrlInExternalApp(app: ExternalApp, link: String)

    fun openUrlInExternalApp(packageName: String, link: String)

    fun openWebPage(url: String): Boolean
    fun openWebPage(url: Uri): Boolean
}