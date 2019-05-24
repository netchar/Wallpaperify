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

package com.netchar.repository.photos

class DownloadManager {

//    fun cursorToDownloadedFile(cursor: Cursor): DownloadedFile {
//
//        val id = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_ID))
//        val title = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE))
//        val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
//        val reason = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_REASON))
//        val totalSize = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
//        val downloadedSize = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
//        val lastModifiedAt = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_LAST_MODIFIED_TIMESTAMP))
//
//        return DownloadedFile(id, title, status, reason, totalSize, downloadedSize, lastModifiedAt)
//    }
//
//    fun getDownloadedFile(context: Context, id: Long): DownloadedFile {
//
//        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
//
//        val query = DownloadManager.Query()
//        query.setFilterById(id)
//
//        val cursor = downloadManager.query(query)
//        if (cursor.moveToFirst()) {
//            return cursorToDownloadedFile(cursor)
//        } else {
//            return DownloadedFile.cancelled(id)
//        }
//    }

}