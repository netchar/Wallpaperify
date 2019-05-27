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

package com.netchar.repository


import com.netchar.remote.apirequest.Paging
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