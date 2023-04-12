package com.nocdu.druginformation.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.nocdu.druginformation.ui.view.AlarmFragment
import com.nocdu.druginformation.ui.view.HomeFragment
import com.nocdu.druginformation.ui.view.InfoFragment
import com.nocdu.druginformation.ui.view.SearchFragment
import com.nocdu.druginformation.utill.Constants.NUM_TABS

class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return NUM_TABS
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return HomeFragment()
            1 -> return SearchFragment()
            2 -> return AlarmFragment()
            3 -> return InfoFragment()
        }
        return HomeFragment()
    }
}