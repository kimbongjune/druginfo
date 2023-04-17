package com.nocdu.druginformation.ui.view

import android.animation.ObjectAnimator
import android.app.*
import android.app.AlarmManager.AlarmClockInfo
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AnticipateInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.nocdu.druginformation.R
import com.nocdu.druginformation.adapter.ViewPagerAdapter
import com.nocdu.druginformation.broadcastreceiver.AlarmBroadcastReceiver
import com.nocdu.druginformation.data.database.AlarmDatabase
import com.nocdu.druginformation.data.database.DrugSearchDatabase
import com.nocdu.druginformation.data.model.FcmToken
import com.nocdu.druginformation.data.repository.AlarmRepositoryImpl
import com.nocdu.druginformation.data.repository.DrugSearchRepositoryImpl
import com.nocdu.druginformation.databinding.ActivityMainBinding
import com.nocdu.druginformation.ui.viewmodel.AlarmViewModel
import com.nocdu.druginformation.ui.viewmodel.AlarmViewModelProviderFactory
import com.nocdu.druginformation.ui.viewmodel.DrugSearchViewModel
import com.nocdu.druginformation.ui.viewmodel.DrugSearchViewModelProviderFactory
import com.nocdu.druginformation.utill.Constants
import com.nocdu.druginformation.utill.Constants.ALARM_REQUEST_CODE
import com.nocdu.druginformation.utill.Constants.ALARM_REQUEST_TO_BROADCAST
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

class MainActivity : AppCompatActivity() {

    companion object {
        private lateinit var instance: MainActivity

        private lateinit var alarmIntent: Intent

        private lateinit var alarmManager:AlarmManager

        fun getInstance(): MainActivity {
            return instance
        }
    }

    final val TAG:String = "MainActivity"

    var backButtonPressdTime:Long = 0

    lateinit var toast: Toast

    lateinit var viewPager: ViewPager2

    private val binding:ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    lateinit var drugSearchViewModel: DrugSearchViewModel

    lateinit var alarmViewModel: AlarmViewModel

    lateinit var appUpdateManager: AppUpdateManager


    private val tabTitleArray = arrayOf(
        "홈",
        "검색",
        "알람",
        //"내 정보"
        "북마크"
    )

    private val tabIconArray = arrayOf(
        R.drawable.ic_outline_home_24,
        R.drawable.ic_baseline_search_24,
        R.drawable.ic_baseline_access_time_24,
        R.drawable.ic_baseline_star_border_24,
        //R.drawable.ic_outline_info_24
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            splashScreen.setOnExitAnimationListener { splashScreenView  ->
//                val slideUp = ObjectAnimator.ofFloat(
//                    splashScreenView,
//                    View.TRANSLATION_Y,
//                    0f,
//                    -splashScreenView.height.toFloat()
//                )
//                slideUp.interpolator = AnticipateInterpolator()
//                slideUp.duration = 300L
//
//                // Call SplashScreenView.remove at the end of your custom animation.
//                slideUp.doOnEnd { splashScreenView.remove() }
//
//                // Run your animation.
//                slideUp.start()
//            }
//        }
        setContentView(binding.root)

        instance = this
        alarmManager = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        viewPager = binding.viewPager

        viewPager.adapter = ViewPagerAdapter(supportFragmentManager, lifecycle)

        appUpdateManager = AppUpdateManagerFactory.create(this)


        initFirebase()

        val database = DrugSearchDatabase.getInstance(this)
        val drugSearchRepository = DrugSearchRepositoryImpl(database)
        val factory = DrugSearchViewModelProviderFactory(drugSearchRepository)
        drugSearchViewModel = ViewModelProvider(this, factory)[DrugSearchViewModel::class.java]

        val alarmDatabase = AlarmDatabase.getDatabase(this)
        var alarmRepository = AlarmRepositoryImpl(alarmDatabase)
        var alarmFactory = AlarmViewModelProviderFactory(alarmRepository)
        alarmViewModel = ViewModelProvider(this, alarmFactory)[AlarmViewModel::class.java]

        setUpTabLayoutWithViewpager()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val data = intent?.getIntExtra("alarmClick", 0)
        if(data != null){
            Log.e(TAG, "알람 취소sss data = ${data}")
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(data)

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                // 설정 아이콘 클릭 시 수행할 작업 작성
                //Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show()
                openSettingFragment()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
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

    private fun initFirebase() {
        val notificationManager = applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (notificationManager?.areNotificationsEnabled() == false) {
            Log.e(TAG, "!!! - 노티피케이션 설정 안되어있음")
            val builder = AlertDialog.Builder(this)
            builder
                .setTitle(R.string.notification_permission_message_title)
                .setMessage(R.string.notification_permission_message)
                .setCancelable(false)
                .setPositiveButton(R.string.go_to_notification_settings) { dialog, which ->
                    // 노티피케이션 설정 화면으로 이동
                    val intent = Intent()
                    intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
                    intent.putExtra("android.provider.extra.APP_PACKAGE", this.packageName)
                    this.startActivity(intent)
                }
                .setNegativeButton("취소") { dialog, which -> dialog.cancel() }
            val alert = builder.create()

            alert.setOnShowListener {
                val positiveButton = alert.getButton(DialogInterface.BUTTON_POSITIVE)
                //positive 버튼의 텍스트 색상을 파란색으로 변경한다.
                positiveButton.setTextColor(ContextCompat.getColor(this, R.color.soft_blue))

                //DatePickerDialog의 negative 버튼을 가져온다.
                val negativeButton = alert.getButton(DialogInterface.BUTTON_NEGATIVE)
                //negative 버튼의 텍스트 색상을 빨간색으로 변경한다.
                negativeButton.setTextColor(ContextCompat.getColor(this, R.color.soft_red))
            }
            alert.show()
        }else {
            Log.e(TAG, "!!! - 노티피케이션 설정 되어있음")

            FirebaseApp.initializeApp(this)
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    lifecycleScope.launch {
                        if (alarmViewModel.getAllToken.await().isNotEmpty()) {
                            alarmViewModel.updateToken(alarmViewModel.getAllToken.await()[0].apply {
                                token = task.result.toString()
                            })
                        } else {
                            alarmViewModel.addToken(FcmToken(token = task.result))
                        }
                    }
                    Log.e(TAG, "token = ${task.result}")
                }
            }

            val channelId = Constants.DEFAULT_NOTIFICATION_CHANNEL_ID
            val channelName = Constants.DEFAULT_NOTIFICATION_CHANNEL_NAME
            val channelDesc = Constants.DEFAULT_NOTIFICATION_CHANNEL_DESC
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationChannel =
                    NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)

                // Configure the notification channel
                notificationChannel.description = channelDesc
                notificationChannel.enableLights(true)
                notificationChannel.vibrationPattern = longArrayOf(200, 100, 200)
                notificationChannel.enableVibration(true)
                notificationManager.createNotificationChannel(notificationChannel)
            }
            FirebaseMessaging.getInstance().subscribeToTopic("custom")
                .addOnSuccessListener { //                    Toast.makeText(getApplicationContext(), "custom topic 구독", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "!!! - custom topic 구독")
                }
            FirebaseMessaging.getInstance().subscribeToTopic("notify")
                .addOnSuccessListener { //                    Toast.makeText(getApplicationContext(), "notify topic 구독", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "!!! - notify topic 구독")
                }
        }
    }

    fun setAlarm(alarmList:List<Triple<Int,Int,Int>>, alarmId:Int){
        alarmIntent = Intent(applicationContext, AlarmBroadcastReceiver::class.java).apply {
            Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
            putExtra(ALARM_REQUEST_CODE, alarmId)
            action = ALARM_REQUEST_TO_BROADCAST
        }

        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            alarmId,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        var alarmTimes = mutableListOf<Calendar>()
        for(i in 0 until alarmList.size){
            calendar.set(Calendar.DAY_OF_WEEK, alarmList[i].first)
            calendar.set(Calendar.HOUR_OF_DAY, alarmList[i].second)
            calendar.set(Calendar.MINUTE, alarmList[i].third)
            alarmTimes.add(calendar.clone() as Calendar)
        }

        alarmTimes.forEach { alarmTime ->
            val time = alarmTime

            // 현재 시간보다 이전인 경우 다음 주에 알람 설정
            if (Calendar.getInstance().after(time)) {
                time.add(Calendar.DATE, 7)
            }
            Log.e(TAG,"알람 등록, ${time.timeInMillis}")

            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                time.timeInMillis,
                //AlarmManager.INTERVAL_DAY * 7,
                pendingIntent
            )
        }

        Log.e(TAG,"등록한 알람의 아이디 = ${alarmId}")
    }

    fun removeAlarm(alarmId:Int){
        alarmIntent = Intent(applicationContext, AlarmBroadcastReceiver::class.java).apply {
            Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
            putExtra(ALARM_REQUEST_CODE, alarmId)
            action = ALARM_REQUEST_TO_BROADCAST
        }

        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            alarmId,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        pendingIntent.cancel()
        alarmManager.cancel(pendingIntent)

        Log.e(TAG,"지운 알람의 아이디 = ${alarmId}")
    }

    private fun openSettingFragment(){
        val settingFragment: Fragment = SettingFragment()
        val transaction = supportFragmentManager?.beginTransaction()
        transaction?.setCustomAnimations(
            R.anim.slide_in_bottom,
            R.anim.slide_out_bottom,
            R.anim.slide_in_bottom,
            R.anim.slide_out_bottom)
        transaction?.replace(R.id.mainActivity, settingFragment)
        transaction?.addToBackStack("SettingFragment")
        transaction?.commit()
    }
}