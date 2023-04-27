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
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

/**
 *  앱이 실행될 때 최초로 실행하는 액티비티
 *  스플래시 스크린을 표시해준다.
 *  ViewModel과 Alarm 관련 인스턴스를 생성한다.
 */
class MainActivity : AppCompatActivity() {

    //fragment에서 Alarm을 등록하거나 삭제할 때 사용하기 위한 companion object
    companion object {
        //MainActivity의 인스턴스를 저장하기 위한 변수
        private lateinit var instance: MainActivity

        //Alarm을 등록할 때 사용하기 위한 PendingIntent
        private lateinit var alarmIntent: Intent

        //Alarm을 등록할 때 사용하기 위한 AlarmManager
        private lateinit var alarmManager:AlarmManager

        //MainActivity의 인스턴스를 반환하는 메소드
        fun getInstance(): MainActivity {
            return instance
        }
    }

    //DataStore 객체를 선언한다.
    private val Context.dataStore by preferencesDataStore(name = "app_settings")
    //앱이 최초로 실행되는지 확인하기 위한 변수
    private val FIRST_LAUNCH = booleanPreferencesKey("first_launch")
    //구글 playconsole과 연동하기 위한 AppUpdateManager 객체를 선언한다.
    lateinit var appUpdateManager: AppUpdateManager

    final val TAG:String = "MainActivity"

    //뒤로가기 버튼을 눌렀을 때의 시간을 저장하기 위한 변수
    var backButtonPressdTime:Long = 0

    //Toast 객체를 선언한다.
    lateinit var toast: Toast

    //ViewPager2 객체를 선언한다.
    lateinit var viewPager: ViewPager2

    //ViewBinding 객체를 선언한다.
    private val binding:ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    //의약품 관련 ViewModel 객체를 선언한다.
    lateinit var drugSearchViewModel: DrugSearchViewModel

    //알람 관련 ViewModel 객체를 선언한다.
    lateinit var alarmViewModel: AlarmViewModel

    //ViewPager에서 사용 할 텍스트 리스트를 선언한다.
    private val tabTitleArray = arrayOf(
        "홈",
        "검색",
        "알람",
        //"내 정보"
        "북마크"
    )

    //ViewPager에서 사용 할 아이콘 리스트를 선언한다.
    private val tabIconArray = arrayOf(
        R.drawable.ic_outline_home_24,
        R.drawable.ic_baseline_search_24,
        R.drawable.ic_baseline_access_time_24,
        R.drawable.ic_baseline_star_border_24,
        //R.drawable.ic_outline_info_24
    )

    //액티비티가 실행되면 최초로 실행되는 함수
    override fun onCreate(savedInstanceState: Bundle?) {
        //스플래시 스크린을 표시해준다.
        installSplashScreen()
        super.onCreate(savedInstanceState)
        //안드로이드 기본 액션바를 대신해 커스텀한 액션바를 사용한다. 단, 테마에 NO_ACTION_BAR를 상속받는 테마를 사용해야한다.
        setSupportActionBar(binding.toolbar)
        //액션바의 타이틀을 표시하지 않는다.
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
        //액티비티에 레이아웃을 표시한다.
        setContentView(binding.root)

        //MainActivity의 인스턴스를 저장한다.
        instance = this
        //알람매니저 변수에 시스템 서비스로부터 알람매니저 객체를 가져와 할당한다.
        alarmManager = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        //viewPager 변수에 뷰페이저를 할당한다.
        viewPager = binding.viewPager

        //viewPager의 어댑터를 설정한다.
        viewPager.adapter = ViewPagerAdapter(supportFragmentManager, lifecycle)

        //appUpdateManager의 인스턴스를 생성한다.
        appUpdateManager = AppUpdateManagerFactory.create(this)

        //FireBase 인스턴스를 생성한다. 생성 시 앱의 최초 실행여부를 파라미터로 받는다. datastore의 저장된 값을 가져오기 위해선 코루틴을 사용해야한다.
        lifecycleScope.launch {
            initFirebase(checkFirstLaunch())
        }

        //의약품 관련 데이터베이스 인스턴스를 생성한다.
        val database = DrugSearchDatabase.getInstance(this)
        //의약품 관련 Repository 인스턴스를 생성한다.
        val drugSearchRepository = DrugSearchRepositoryImpl(database)
        //의약품 관련 ViewModelProviderFactory 인스턴스를 생성한다.
        val factory = DrugSearchViewModelProviderFactory(drugSearchRepository)
        //위에서 선언한 객체를 사용해 의약품 관련 ViewModel 인스턴스를 생성한다.
        drugSearchViewModel = ViewModelProvider(this, factory)[DrugSearchViewModel::class.java]

        //알람 관련 데이터베이스 인스턴스를 생성한다.
        val alarmDatabase = AlarmDatabase.getDatabase(this)
        //알람 관련 Repository 인스턴스를 생성한다.
        var alarmRepository = AlarmRepositoryImpl(alarmDatabase)
        //알람 관련 ViewModelProviderFactory 인스턴스를 생성한다.
        var alarmFactory = AlarmViewModelProviderFactory(alarmRepository)
        //위에서 선언한 객체를 사용해 알람 관련 ViewModel 인스턴스를 생성한다.
        alarmViewModel = ViewModelProvider(this, alarmFactory)[AlarmViewModel::class.java]

        //탭 레이아웃에 ViewPager를 연동한다,
        setUpTabLayoutWithViewpager()
    }

    //액션바에 Menu를 추가하기 위한 함수 xml로 설정한 메뉴를 액션바에 추가한다.
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    //액션바의 메뉴를 클릭했을 때 수행할 작업을 정의한다.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                // 설정 아이콘 클릭 시 수행할 작업 작성
                //Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show()
                //설정 프래그먼트를 띄워준다.
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

    //뒤로가기 버튼을 눌렀을 때 수행할 작업을 정의한다.
    override fun onBackPressed() {
        //프래그먼트 스택이 없을 때
        if(supportFragmentManager.backStackEntryCount == 0){
            //뒤로가기 버튼을 눌렀을 때 만약 2초 이내에 한 번 더 누르면 앱을 종료한다.
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

    //탭 레이아웃에 ViewPager를 연동하는 함수
    private fun setUpTabLayoutWithViewpager(){
        TabLayoutMediator(binding.tabLayout, binding.viewPager){ tab, postion ->
            tab.text = tabTitleArray[postion]
            tab.icon = getDrawable(tabIconArray[postion])
        }.attach()
    }

    //firebase 인스턴스를 생성하는 함수, 토큰을 부여받고 채널을 구독하여 알람 권한을 요청한다.
    private suspend fun initFirebase(checkFirstLaunch:Boolean) {
        val notificationManager = applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        //알람 권한이 없고 앱이 최초 실행이 아닐 때
        if (notificationManager?.areNotificationsEnabled() == false && !checkFirstLaunch) {
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
            //firebase 인스턴스를 생성한다.
            FirebaseApp.initializeApp(this)
            //토큰의 발급이 완료 되면 동작하는 리스너
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                //토큰 발급이 성공했을 때
                if (task.isSuccessful) {
                    lifecycleScope.launch {
                        //데이터베이스에 토큰이 이미 있다면
                        if (alarmViewModel.getAllToken.await().isNotEmpty()) {
                            //토큰의 값을 업데이트한다.
                            alarmViewModel.updateToken(alarmViewModel.getAllToken.await()[0].apply {
                                token = task.result.toString()
                            })
                            //데이터베이스에 토큰이 없다면
                        } else {
                            //데이터베이스에 토큰을 인서트 한다.
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
            //custom 토픽을 구독한다.
            FirebaseMessaging.getInstance().subscribeToTopic("custom")
                .addOnSuccessListener { //                    Toast.makeText(getApplicationContext(), "custom topic 구독", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "!!! - custom topic 구독")
                }
            //notify 토픽을 구독한다.
            FirebaseMessaging.getInstance().subscribeToTopic("notify")
                .addOnSuccessListener { //                    Toast.makeText(getApplicationContext(), "notify topic 구독", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "!!! - notify topic 구독")
                }
        }
    }

    //알람을 설정하는 함수
    fun setAlarm(alarmList:List<Triple<Int,Int,Int>>, alarmId:Int){
        //알람을 등록하기위한 브로드캐스트 리시버를 인텐트에 담는다.
        alarmIntent = Intent(applicationContext, AlarmBroadcastReceiver::class.java).apply {
            //앱 배터리 최적화를 무시하기 위해 선언하였다.
            Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
            //알람을 등록하기 위해 알람 아이디를 인텐트에 담는다.
            putExtra(ALARM_REQUEST_CODE, alarmId)
            //알람을 등록하기 위해 액션을 인텐트에 담는다.
            action = ALARM_REQUEST_TO_BROADCAST
        }

        //알람을 등록하기 위한 pendingIntent를 선언한다.
        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            alarmId,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        //알람 날짜 및 시간을 담기위한 Calendar 객체를 선언한다.
        val calendar = Calendar.getInstance().apply {
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        //Calendar 객체를 알람을 여러개 등록하기 위해 배열로 선언하였다.
        var alarmTimes = mutableListOf<Calendar>()

        //파라미터로 전달받은 Tiple List 알람 리스트를 반복문을 통해 Calendar 객체에 담는다.
        for(i in 0 until alarmList.size){
            calendar.set(Calendar.DAY_OF_WEEK, alarmList[i].first)
            calendar.set(Calendar.HOUR_OF_DAY, alarmList[i].second)
            calendar.set(Calendar.MINUTE, alarmList[i].third)
            alarmTimes.add(calendar.clone() as Calendar)
        }

        //배열로 선언한 Calendar 객체를 반복문을 통해 알람을 등록한다.
        alarmTimes.forEach { alarmTime ->
            val time = alarmTime

            // 현재 시간보다 이전인 경우 다음 주에 알람 설정
            if (Calendar.getInstance().after(time)) {
                time.add(Calendar.DATE, 7)
            }
            Log.e(TAG,"알람 등록, ${time.timeInMillis}")

            //알람을 등록한다. 정확한 시간에 울리기 위해 setExactAndAllowWhileIdle를 사용하였다.
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                time.timeInMillis,
                //AlarmManager.INTERVAL_DAY * 7,
                pendingIntent
            )
        }

        Log.e(TAG,"등록한 알람의 아이디 = ${alarmId}")
    }

    //알람을 제거하는 함수
    fun removeAlarm(alarmId:Int){
        //알람을 삭제하기위한 브로드캐스트 리시버를 인텐트에 담는다.
        alarmIntent = Intent(applicationContext, AlarmBroadcastReceiver::class.java).apply {
            //앱 배터리 최적화를 무시하기 위해 선언하였다.
            Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
            //알람을 삭제하기 위해 알람 아이디를 인텐트에 담는다.
            putExtra(ALARM_REQUEST_CODE, alarmId)
            //알람을 등록하기 위해 액션을 인텐트에 담는다.
            action = ALARM_REQUEST_TO_BROADCAST
        }

        //알람을 삭제하기 위한 pendingIntent를 선언한다.
        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            alarmId,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
        //pendingIntent 객체의 이벤트 수행을 취소한다.
        pendingIntent.cancel()
        //알람을 취소한다.
        alarmManager.cancel(pendingIntent)

        Log.e(TAG,"지운 알람의 아이디 = ${alarmId}")
    }

    //설정 프래그먼트를 실행하는 메서드
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

    //앱이 처음 실행 되었는지 체크하는 메서드
    private suspend fun checkFirstLaunch() : Boolean {
        val isFirstLaunch = dataStore.data.first()[FIRST_LAUNCH] ?: true

        if (isFirstLaunch) {
            Log.e(TAG,"앱이 처음 실행되었음")
            dataStore.edit { settings ->
                settings[FIRST_LAUNCH] = false
            }
        } else {
            Log.e(TAG,"앱이 이전에 실행된 적 있음")
            // 앱이 이전에 실행된 적이 있습니다. 원하는 작업을 수행하세요.
        }
        return isFirstLaunch
    }
}