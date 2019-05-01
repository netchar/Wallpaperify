package com.netchar.models.apirequest


import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

/**
 * Created by Netchar on 01.05.2019.
 * e.glushankov@gmail.com
 */
internal class PagingTest {

    @Test
    fun `startPage returns init value`() {
        val initPage = 1
        val page = Paging(initPage)

        assertSame(initPage, page.startPage)
    }

    @Test
    fun `nextPage should be incremented by 1`() {
        val initPage = 1
        val page = Paging(initPage)

        val nextPage = page.nextPage()

        assertEquals(nextPage, 2)
    }

    @Test
    fun `prevPage should be decremented by 1`() {
        val initPage = 2
        val page = Paging(initPage)

        val prev = page.prevPage()

        assertEquals(prev, 1)
    }

    @Test
    fun `fromStart should be reset to initial var`() {
        val initPage = 5
        val page = Paging(initPage)

        val fromStart = page.fromStart()

        assertEquals(fromStart, initPage)
    }
}