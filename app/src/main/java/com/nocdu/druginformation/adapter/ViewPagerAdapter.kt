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

/**
 *  뷰 페이저를 사용하기 위한 어댑터 클래스
 *  @see MainActivity에서 호출
 *  하위 프래그먼트(HomeFragment, SearchFragment, AlarmFragment, InfoFragment)를 어댑터로 연결한다.
 */
class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    //뷰페이저의 페이지 수를 반환
    override fun getItemCount(): Int {
        return NUM_TABS
    }

    //페이지의 위치에 따라 프래그먼트를 반환
    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return HomeFragment()
            1 -> return SearchFragment()
            2 -> return AlarmFragment()
            3 -> return InfoFragment()
        }
        //기본값으로 홈 프래그먼트 반환
        return HomeFragment()
    }
}