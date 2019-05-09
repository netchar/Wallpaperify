package com.netchar.wallpaperify.ui.home

import androidx.fragment.app.Fragment
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * Created by Netchar on 09.05.2019.
 * e.glushankov@gmail.com
 */
class HomeFragmentPagerAdapterTest {

    private val fragment1 = mockk<Fragment>()
    private val fragment2 = mockk<Fragment>()

    @Test
    fun getItem() {
        val pager = HomeFragmentPagerAdapter(mockk())

        pager.addFragment(fragment1, "fragment1")
        val item = pager.getItem(0)

        assertEquals(fragment1, item)
    }

    @Test
    fun getCount() {
        val pager = HomeFragmentPagerAdapter(mockk())

        pager.addFragment(fragment1, "fragment1")
        pager.addFragment(fragment2, "fragment2")

        assertEquals(2, pager.count)
    }

    @Test
    fun getPageTitle() {
        val pager = HomeFragmentPagerAdapter(mockk())

        pager.addFragment(fragment1, "fragment1")
        val title = pager.getPageTitle(0)

        assertEquals("fragment1", title)
    }
}