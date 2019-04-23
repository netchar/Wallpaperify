package com.netchar.wallpaperify.ui.home


import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.appbar.AppBarLayout
import com.netchar.wallpaperify.R
import com.netchar.wallpaperify.ui.base.BaseFragment
import com.netchar.wallpaperify.ui.collections.CollectionsFragment
import com.netchar.wallpaperify.ui.latest.LatestFragment
import com.netchar.wallpaperify.ui.trending.TrandingFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : BaseFragment() {

    private var activityAppBarElevation: Float = 0f

    override val layoutResId: Int = R.layout.fragment_home

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewpager = view.findViewById<ViewPager>(R.id.pager)
        viewpager.adapter = MyFragmentPagerAdapter(childFragmentManager).also {
            it.addFragment(LatestFragment.newInstance(), "Latest")
            it.addFragment(TrandingFragment(), "Tranding")
            it.addFragment(CollectionsFragment(), "Collections")
        }
        tabs.setupWithViewPager(viewpager)
//        activityAppBarElevation = ViewCompat.getElevation(baseActivity.main_appbar)
//        ViewCompat.setElevation(baseActivity.main_appbar, 0f)
    }

    override fun onDestroyView() {
        super.onDestroyView()
//        ViewCompat.setElevation(baseActivity.main_appbar, activityAppBarElevation)
    }

    class MyFragmentPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {
        private val fragments = LinkedHashMap<Fragment, String>()

        override fun getItem(position: Int): Fragment? {
            return fragments.keys.elementAt(position)
        }

        override fun getCount(): Int {
            return fragments.keys.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            fragments[fragment] = title
        }

        override fun getPageTitle(position: Int): CharSequence {
            return fragments.values.elementAt(position)
        }
    }
}
