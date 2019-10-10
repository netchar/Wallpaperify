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

package com.netchar.common

const val UNSPLASH_URL = "https://unsplash.com/"
const val UNSPLASH_UTM_PARAMETERS = "?utm_source=wallpaperify&utm_medium=referral&utm_campaign=api-credit"

const val DEVELOPER_INSTAGRAM_URL = "https://www.instagram.com/e.glushankov/"
const val DEVELOPER_LINKEDIN_URL = "https://www.linkedin.com/in/glushankov"
const val DEVELOPER_GMAIL = "e.glushankov@gmail.com"

fun String.connectUnsplashUtmParameters() = this + UNSPLASH_UTM_PARAMETERS