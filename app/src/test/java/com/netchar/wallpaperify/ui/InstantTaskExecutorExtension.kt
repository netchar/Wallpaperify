package com.netchar.wallpaperify.ui

import androidx.arch.core.executor.ArchTaskExecutor
import androidx.arch.core.executor.TaskExecutor
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext

/**
 * Created by Netchar on 06.04.2019.
 * e.glushankov@gmail.com
 */

/**
 * Allows to mock android acrch Task Executor to work with LiveData in unit tests. Instant run.
 */
class InstantTaskExecutorExtension : BeforeAllCallback {
    override fun beforeAll(context: ExtensionContext?) {
        ArchTaskExecutor.getInstance()
            .setDelegate(object : TaskExecutor() {
            override fun executeOnDiskIO(runnable: Runnable) {
                runnable.run()
            }

            override fun postToMainThread(runnable: Runnable) {
                runnable.run()
            }

            override fun isMainThread(): Boolean = true
        })
    }
}