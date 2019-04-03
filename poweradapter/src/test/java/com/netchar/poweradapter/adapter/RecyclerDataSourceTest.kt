package com.netchar.poweradapter.adapter

import com.netchar.poweradapter.item.IRecyclerItem
import com.netchar.poweradapter.item.ItemRenderer
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

/**
 * Created by Netchar on 03.04.2019.
 * e.glushankov@gmail.com
 */
internal class RecyclerDataSourceTest {

    private val testItemOne = mockk<IRecyclerItem> {
        every { getRenderKey() } returns "r1"
    }
    private val testItemTwo = mockk<IRecyclerItem> {
        every { getRenderKey() } returns "r2"
    }

    private val renderers: List<ItemRenderer<IRecyclerItem>> = listOf(
        mockk {
            every { renderKey } returns "r1"
            every { layoutRes() } returns 1
        },
        mockk {
            every { renderKey } returns "r2"
            every { layoutRes() } returns 2
        }
    )

    private val dataSource = RecyclerDataSource(renderers).also {
        it.seedData(listOf(testItemOne, testItemTwo))
    }

    @Test
    fun getRendererFor() {
        Assertions.assertEquals(renderers[0], dataSource.getRendererFor(renderers[0].layoutRes()))
        Assertions.assertEquals(renderers[1], dataSource.getRendererFor(renderers[1].layoutRes()))
    }

    @Test
    fun getLayoutResFor() {
        Assertions.assertEquals(dataSource.getLayoutResFor(0), 1)
        Assertions.assertEquals(dataSource.getLayoutResFor(1), 2)
    }

    @Test
    fun getCount() {
        Assertions.assertEquals(2, dataSource.getCount())
    }

    @Test
    fun getItem() {
        Assertions.assertEquals(testItemOne, dataSource.getItem(0))
        Assertions.assertEquals(testItemTwo, dataSource.getItem(1))
    }
}
