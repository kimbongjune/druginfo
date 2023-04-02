package com.nocdu.druginformation.ui.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayoutMediator
import com.nocdu.druginformation.R
import com.nocdu.druginformation.adapter.ViewPagerAdapter
import com.nocdu.druginformation.data.database.DrugSearchDatabase
import com.nocdu.druginformation.data.repository.DrugSearchRepositoryImpl
import com.nocdu.druginformation.databinding.ActivityMainBinding
import com.nocdu.druginformation.ui.viewmodel.DrugSearchViewModel
import com.nocdu.druginformation.ui.viewmodel.DrugSearchViewModelProviderFactory

class MainActivity : AppCompatActivity() {

    final val TAG:String = "MainActivity"

    var backButtonPressdTime:Long = 0

    lateinit var toast: Toast

    private val binding:ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    lateinit var drugSearchViewModel: DrugSearchViewModel

    private val tabTitleArray = arrayOf(
        "홈",
        "검색",
        "알람",
        //"내 정보"
        "즐겨찾기"
    )

    private val tabIconArray = arrayOf(
        R.drawable.ic_outline_home_24,
        R.drawable.ic_baseline_search_24,
        R.drawable.ic_baseline_access_time_24,
        R.drawable.ic_baseline_star_border_24,
        //R.drawable.ic_outline_info_24
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val viewPager = binding.viewPager
        val tabLayout = binding.tabLayout

        viewPager.adapter = ViewPagerAdapter(supportFragmentManager, lifecycle)

        val database = DrugSearchDatabase.getInstance(this)
        val drugSearchRepository = DrugSearchRepositoryImpl(database)
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

    override fun onBackPressed() {
        if(supportFragmentManager.backStackEntryCount == 0){
            if(System.currentTimeMillis() - backButtonPressdTime >= 2000){
                backButtonPressdTime = System.currentTimeMillis()
                toast = Toast.makeText(this, "\'뒤로\' 버튼을 한 번 더 누르시면 종료됩니다", Toast.LENGTH_SHORT)
                toast.show()
                Log.e(TAG, "first backPressed")
            }else{
                Log.e(TAG, "second backPressed")
                toast.cancel()
                finish()
            }
        }else{
            super.onBackPressed()
        }
    }


    private fun setUpTabLayoutWithViewpager(){
        TabLayoutMediator(binding.tabLayout, binding.viewPager){ tab, postion ->
            tab.text = tabTitleArray[postion]
            tab.icon = getDrawable(tabIconArray[postion])
        }.attach()
    }

}