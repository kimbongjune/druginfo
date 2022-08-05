package com.nocdu.druginformation.ui.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayoutMediator
import com.nocdu.druginformation.R
import com.nocdu.druginformation.adapter.ViewPagerAdapter
import com.nocdu.druginformation.data.repository.DrugSearchRepositoryImpl
import com.nocdu.druginformation.databinding.ActivityMainBinding
import com.nocdu.druginformation.ui.viewmodel.DrugSearchViewModel
import com.nocdu.druginformation.ui.viewmodel.DrugSearchViewModelProviderFactory

class MainActivity : AppCompatActivity() {

    final val TAG:String = "MainActivity"

    private val binding:ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    lateinit var drugSearchViewModel: DrugSearchViewModel

    private val tabTitleArray = arrayOf(
        "홈",
        "검색",
        "알람",
        "내 정보"
    )

    private val tabIconArray = arrayOf(
        R.drawable.ic_outline_home_24,
        R.drawable.ic_baseline_search_24,
        R.drawable.ic_baseline_access_time_24,
        R.drawable.ic_outline_info_24
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val viewPager = binding.viewPager
        val tabLayout = binding.tabLayout

        viewPager.adapter = ViewPagerAdapter(supportFragmentManager, lifecycle)

        val drugSearchRepository = DrugSearchRepositoryImpl()
        val factory = DrugSearchViewModelProviderFactory(drugSearchRepository)
        drugSearchViewModel = ViewModelProvider(this, factory)[DrugSearchViewModel::class.java]

        setUpTabLayoutWithViewpager()
    }
    override fun onStop() {
        super.onStop()
        Log.e(TAG, "${TAG} is onStoped")
    }

    override fun onPause() {
        super.onPause()
        Log.e(TAG, "${TAG} is onPaused")
    }

    override fun onStart() {
        super.onStart()
        Log.e(TAG, "${TAG} is onStarted")
    }


    override fun onDestroy() {
        super.onDestroy()
        Log.e(TAG, "${TAG} is onDestroyed")
    }

    override fun finish() {
        super.finish()
        Log.e(TAG, "${TAG} is finished")
    }


    private fun setUpTabLayoutWithViewpager(){
        TabLayoutMediator(binding.tabLayout, binding.viewPager){ tab, postion ->
            tab.text = tabTitleArray[postion]
            tab.icon = getDrawable(tabIconArray[postion])
        }.attach()
    }

}