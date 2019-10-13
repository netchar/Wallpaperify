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

package com.netchar.common.extensions

import androidx.appcompat.widget.Toolbar
import androidx.core.view.updatePadding

fun Toolbar.applyWindowInsets() = setOnApplyWindowInsetsListener { toolbar, windowInsets ->
    toolbar.updatePadding(top = windowInsets.systemWindowInsetTop, bottom = 0)
    // windowInsets.consumeSystemWindowInsets() - will stop propagating them to other children
    windowInsets
}