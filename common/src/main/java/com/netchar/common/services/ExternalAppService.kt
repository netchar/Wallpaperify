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

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.netchar.common.DEVELOPER_GMAIL
import com.netchar.common.extensions.openWebPage
import javax.inject.Inject

internal class ExternalAppService @Inject constructor(val context: Context) : IExternalAppService {
    override fun sendEmail(subject: String, message: String) {
        val uri = Uri.parse("mailto:")
        val emailIntent = Intent(Intent.ACTION_SENDTO, uri).apply {
            putExtra(Intent.EXTRA_EMAIL, arrayOf(DEVELOPER_GMAIL)) // recipients
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, message)
        }

        val packageManager = context.packageManager
        val resolvingActivities = packageManager.queryIntentActivities(emailIntent, 0)
        val isIntentSafe: Boolean = resolvingActivities.isNotEmpty()

        if (isIntentSafe) {
            val intent: Intent = if (resolvingActivities.count() > 1) {
                Intent.createChooser(emailIntent, "Send email with:")
            } else {
                emailIntent
            }

            context.startActivity(intent)
        } else {
            Toast.makeText(context, "Unable to find appropriate app for send an email.", Toast.LENGTH_LONG).show()
        }
    }

    override fun openWith(app: IExternalAppService.ExternalApp, link: String) {
        openWith(app.packageName, link)
    }

    override fun openWith(packageName: String, link: String) {
        val packageManager = context.packageManager
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)

        if (launchIntent == null) {
            context.openWebPage(link)
        } else {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(launchIntent)
        }
    }
}