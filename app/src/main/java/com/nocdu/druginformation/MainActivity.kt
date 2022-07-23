package com.nocdu.druginformation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.nocdu.druginformation.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    final val TAG:String = "MainActivity"
    var backButtonPressdTime:Long = 0

    lateinit var toggle : ActionBarDrawerToggle
    private lateinit var tabLayout: TabLayout
    lateinit var toast: Toast

    class MyFragmentPagerAdapter(activity: FragmentActivity): FragmentStateAdapter(activity){
        val fragments : List<Fragment>
        init {
            fragments = listOf(HomeFragment(), SearchFragment(), AlarmFragment(), InfoFragment())
        }

        override fun getItemCount(): Int {
            return fragments.size
        }

        override fun createFragment(position: Int): Fragment {
            return fragments[position]
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toggle = ActionBarDrawerToggle(this, binding.drawer, R.string.drawer_opened, R.string.drawer_closed)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toggle.syncState()

        val adapter = MyFragmentPagerAdapter(this)
        binding.viewPager.adapter = adapter
        binding.viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        tabLayout = binding.tabLayout
        tabLayout.tabRippleColor = null
        TabLayoutMediator(tabLayout, binding.viewPager){ tab, position ->
            when(position){
                0 -> tab.text = "홈"
                1 -> tab.text = "검색"
                2 -> tab.text = "알람"
                3 -> tab.text = "내 정보"
            }
        }.attach()
        setUpTabIcons()
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
        //super.onBackPressed()
        Log.e(TAG, "press BackButton")
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
    }

    fun setUpTabIcons(){
        tabLayout.getTabAt(0)?.setIcon(R.drawable.ic_outline_home_24)
        tabLayout.getTabAt(1)?.setIcon(R.drawable.ic_baseline_search_24)
        tabLayout.getTabAt(2)?.setIcon(R.drawable.ic_baseline_access_time_24)
        tabLayout.getTabAt(3)?.setIcon(R.drawable.ic_outline_info_24)
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        val infrater = menuInflater
//        infrater.inflate(R.menu.menu_main, menu)
//
//        val menuItem = menu?.findItem(R.id.menu_search)
//        val searchView = menuItem?.actionView as SearchView
//
//        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                Log.e("TAG", "typingText = ${query}")
//                return true
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//                return true
//            }
//
//        })
//        return true
//    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}