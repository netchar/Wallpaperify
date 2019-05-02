package com.netchar.wallpaperify.ui.latest

import android.content.res.Resources
import com.netchar.wallpaperify.ui.di.DaggerTestAppComponent
import com.netchar.wallpaperify.ui.di.TestAppComponent
import com.squareup.okhttp.mockwebserver.MockResponse
import com.squareup.okhttp.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import java.io.File


/**
 * Created by Netchar on 01.05.2019.
 * e.glushankov@gmail.com
 */

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class BaseMockServerTest {

    private lateinit var testAppComponent: TestAppComponent

    private lateinit var mockServer: MockWebServer

    @BeforeAll
    open fun setUp() {
        this.startMockServer()
        this.configureDi()
    }

    @AfterAll
    open fun tearDown() {
        this.stopMockServer()
    }

    open fun configureDi() {
        testAppComponent = DaggerTestAppComponent.builder()
            .baseUrl(mockServer.url("/").toString())
            .build()
        onSetupDaggerComponent(testAppComponent)
    }

    abstract fun onSetupDaggerComponent(component: TestAppComponent)

    private fun startMockServer() {
        mockServer = MockWebServer()
        mockServer.start()
    }

    private fun stopMockServer() {
        mockServer.shutdown()
    }

    open fun mockHttpResponse(fileName: String, responseCode: Int) = mockServer.enqueue(
        MockResponse()
            .setResponseCode(responseCode)
            .setBody(getJson(fileName))
    )

    fun getJson(fileName: String): String {
        val uri = this.javaClass.classLoader?.getResource(fileName)
        uri?.let {
            val file = File(uri.path)
            return String(file.readBytes())
        } ?: throw Resources.NotFoundException("File not found")
    }
}