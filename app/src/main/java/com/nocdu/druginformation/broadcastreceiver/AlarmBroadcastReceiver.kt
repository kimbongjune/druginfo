package com.nocdu.druginformation.broadcastreceiver

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_BOOT_COMPLETED
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.nocdu.druginformation.R
import com.nocdu.druginformation.data.api.RetrofitInstance
import com.nocdu.druginformation.data.database.AlarmDatabase
import com.nocdu.druginformation.service.AlarmService
import com.nocdu.druginformation.ui.view.MainActivity
import com.nocdu.druginformation.utill.Constants
import com.nocdu.druginformation.utill.Constants.ACQUIRE_WAKE_LOCK
import com.nocdu.druginformation.utill.Constants.ACTION_START_SOUND_AND_VIBRATION
import com.nocdu.druginformation.utill.Constants.ACTION_STOP_SOUND_AND_VIBRATION
import com.nocdu.druginformation.utill.Constants.ALARM_BODY
import com.nocdu.druginformation.utill.Constants.ALARM_DATABASE_KEY
import com.nocdu.druginformation.utill.Constants.ALARM_REQUEST_CODE
import com.nocdu.druginformation.utill.Constants.ALARM_REQUEST_TO_BROADCAST
import com.nocdu.druginformation.utill.Constants.ALARM_TITLE
import com.nocdu.druginformation.utill.Constants.convertTo24HoursFormat
import com.nocdu.druginformation.utill.Constants.createAlarmId
import com.nocdu.druginformation.utill.Constants.dateToMillisecondTime
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 *  알람을 받아서 알람을 울리는 리시버
 *  action에 따라서 단말기의 리부트시 알람을 조회 및 재등록 하거나
 *  알람이 실행 되면 AlarmService를 실행시키고 notification을 띄운다.
 */
class AlarmBroadcastReceiver : BroadcastReceiver(){

    final val TAG:String = "AlarmBroadcastReceiver"

    //notification 기본 제목
    private var title:String = ALARM_TITLE
    //notification 기본 내용
    private var body:String = ALARM_BODY

    override fun onReceive(context: Context, intent: Intent) {
        Log.e(TAG, "알람 리시버 동작")

        //단말기가 재부팅 될 때 알람을 재등록
        if(intent.action == ACTION_BOOT_COMPLETED){

            //알람을 재등록 하기 위해 단말기를 깨운다.
            val wakeLock = acquireWakeLock(context.applicationContext, PowerManager.PARTIAL_WAKE_LOCK)

            //Toast.makeText(context.applicationContext, "재부팅 되었음", Toast.LENGTH_SHORT).show()

            //database에 있는 알람을 조회하여 재등록한다.
            reRegistrationAlarm(context)
            //단말기를 재부팅 하면서 실행한 wakeLock 객체의 자원을 해제
            wakeLock?.release()
        }else if(intent.action == ALARM_REQUEST_TO_BROADCAST){
            val id:Int = if(intent.extras?.get(ALARM_REQUEST_CODE) != null) {intent.extras?.get(ALARM_REQUEST_CODE).toString().toInt()} else {0}
            val databaseId:Int = if(intent.extras?.get(ALARM_DATABASE_KEY) != null) {intent.extras?.get(ALARM_DATABASE_KEY).toString().toInt()} else {0}
            //알람을 울리기 위해 단말기를 깨운다.
            val wakeLock = acquireWakeLock(context.applicationContext, PowerManager.PARTIAL_WAKE_LOCK)

            //database에 있는 알람을 조회하여 재등록한다.
            val alarmDate:Pair<String,String> = reRegistrationAlarm(context, databaseId)

            //notification manager를 선언한다.
            val notificationManager = context.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            //만약 알람 권한이 허용 되어있다면
            if (notificationManager?.areNotificationsEnabled() == true) {
                //notification의 channelId를 가져온다.
                val channelId = Constants.DEFAULT_NOTIFICATION_CHANNEL_ID
                //notification의 channelName을 가져온다.
                val channelName = Constants.DEFAULT_NOTIFICATION_CHANNEL_NAME

                //notification에 이벤트를 담을 서비스 클래스를 인텐트로 선언한다.
                val newIntent = Intent(context.applicationContext, AlarmService::class.java)
                //notification에 이벤트를 담을 서비스 클래스의 액션을 지정한다.
                newIntent.action = ACTION_STOP_SOUND_AND_VIBRATION
                //notification에 이벤트를 담을 서비스 클래스의 알람 아이디를 지정한다. 해당 아이디를 이용해 notification을 삭제한다.
                newIntent.putExtra(ALARM_REQUEST_CODE, id)
                newIntent.putExtra(ALARM_DATABASE_KEY, databaseId)

                //알람 발생시 실행 시킬 서비스 클래스를 실행시킬 pendingIntent를 선언한다. notification을 슬라이드하거나 클릭시 실행된다.
                val pendingIntent = PendingIntent.getService(context.applicationContext, id, newIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)

                //notification builder를 선언한다.
                val notification = NotificationCompat.Builder(context.applicationContext, channelId)
                    .setSmallIcon(R.mipmap.ic_launcher_drugingo)
                    .setAutoCancel(true)
                    .setContentTitle(alarmDate.first)
                    .setContentText("의약품 (${alarmDate.second}) 복용시간 입니다.")
                    .setContentIntent(pendingIntent)
                    .setDeleteIntent(pendingIntent)
                //notification을 클릭하거나 슬라이드 하여 삭제시 발생했던 소리와 진동을 멈춘다.

                //오레오 버전 이상일 경우 notification channel을 생성한다.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
                    notificationManager.createNotificationChannel(channel)
                }
                //notification을 띄운다.
                notificationManager.notify(id, notification.build())

                //알람을 울리기 위한 서비스 클래스를 실행한다.
                val serviceIntent = Intent(context, AlarmService::class.java)
                serviceIntent.putExtra(ALARM_REQUEST_CODE, id)
                serviceIntent.putExtra(ALARM_DATABASE_KEY, databaseId)
                serviceIntent.action =  ACTION_START_SOUND_AND_VIBRATION

                //오레오 버전 이상일 경우 서비스클래스를 startForegroundService를 이용해서 실행해야 한다.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(serviceIntent);
                } else {
                    context.startService(serviceIntent);
                }
            } else {
                //알람 권한이 허용되지 않았을 경우 토스트 메시지를 띄운다.
                Toast.makeText(context.applicationContext, "알림 권한이 허용되지 않아 알람을 울릴 수 없습니다.", Toast.LENGTH_SHORT).show()
            }

            //알람을 울리기 위해 실행한 wakeLock 객체의 자원을 해제한다.
            wakeLock?.release()
        }

    }

    private fun acquireWakeLock(context: Context?, flags: Int): PowerManager.WakeLock? {
        val powerManager = context?.applicationContext?.getSystemService(Context.POWER_SERVICE) as PowerManager?
        return powerManager?.run {
            newWakeLock(flags or PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.ON_AFTER_RELEASE, ACQUIRE_WAKE_LOCK)
                .apply {
                    acquire()
                }
        }
    }

    //알람을 재등록하는 메서드
    private fun reRegistrationAlarm(context: Context, id:Int = 0):Pair<String,String>{
        //database에 있는 알람을 조회하여 for문을 돌린다.
        GlobalScope.launch {
            //setAlarm 메서드에서 사용할 Triple 객체를 담을 리스트
            var alarmTimes = mutableListOf<Triple<Int,Int,Int>>()
            AlarmDatabase.getDatabase(context).alarmDao().getAllAlarms()
                .forEach { alarmWithDosetime ->
                    Log.e(TAG,"@@@@@@@?????????????${alarmWithDosetime}")
                    //알람으로 등록한 요일을 순회하며 알람을 재등록한다.
                    for (i in 0 until alarmWithDosetime.alarm.alarmDateInt.size) {
                        //알람으로 등록한 시간을 순회하며 알람을 재등록한다.
                        for (j in 0 until alarmWithDosetime.doseTime.size) {
                            //알람으로 등록한 시간이 a HH:mm 형식으로 되어있어 각각의 시간과 분을 분리한다.
                            val hourAndMinute =
                                convertTo24HoursFormat(alarmWithDosetime.doseTime[j].time)
                            //Pair 객체에서 시간을 가져온다.
                            val hour = hourAndMinute.first
                            //Pair 객체에서 분을 가져온다.
                            val minute = hourAndMinute.second
                            //Triple 객체를 리스트에 담는다.
                            alarmTimes.add(
                                Triple(
                                    alarmWithDosetime.alarm.alarmDateInt[i],
                                    hour,
                                    minute
                                )
                            )
                        }
                    }
                    Log.e(TAG,"!!!!!!!!!!!!!!!!!!!!!!!!!!!!!${alarmWithDosetime.alarm.id}")
                    AlarmDatabase.getDatabase(context).alarmDao().getAlarm(alarmWithDosetime.alarm.id).apply {
                        //알람을 삭제하기 위한 알람 매니저를 선언한다.
                        var alarmManager = context.applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                        //알람의 개수만큼 반복문을 돌기위한 변수
                        val alarmSize:Int = this.alarmDate.size * this.dailyDosage
                        val alarmUpdateTime:Int = dateToMillisecondTime(this.updateTime)
                        for (i in 0 until alarmSize) {
                            //알람을 삭제하기위한 브로드캐스트 리시버를 인텐트에 담는다.
                            var alarmIntent:Intent = Intent(context.applicationContext, AlarmBroadcastReceiver::class.java).apply {
                                //앱 배터리 최적화를 무시하기 위해 선언하였다.
                                Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                                //알람을 삭제하기 위해 알람 아이디를 인텐트에 담는다.
                                putExtra(ALARM_DATABASE_KEY, alarmWithDosetime.alarm.id)
                                putExtra(ALARM_REQUEST_CODE, createAlarmId(alarmWithDosetime.alarm.id, i, alarmUpdateTime))
                                //알람을 등록하기 위해 액션을 인텐트에 담는다.
                                action = ALARM_REQUEST_TO_BROADCAST
                            }

                            //알람을 삭제하기 위한 pendingIntent를 선언한다.
                            val pendingIntent = PendingIntent.getBroadcast(
                                context.applicationContext,
                                createAlarmId(alarmWithDosetime.alarm.id, i, alarmUpdateTime),
                                alarmIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                            )
                            //pendingIntent 객체의 이벤트 수행을 취소한다.
                            pendingIntent.cancel()
                            //알람을 취소한다.
                            alarmManager.cancel(pendingIntent)

                            Log.e(TAG,"지운 알람의 아이디 = ${createAlarmId(alarmWithDosetime.alarm.id, i, alarmUpdateTime)}")
                        }
                        setAlarm(context,
                            alarmTimes,
                            alarmWithDosetime.alarm.id,
                            dateToMillisecondTime(alarmWithDosetime.alarm.updateTime))
                    }
                }
        }
        return if(id != 0){
            updateStock(context,id)
        }else{
            Pair(ALARM_TITLE, ALARM_BODY)
        }
    }

    //알람을 등록하는 메소드
    private fun setAlarm(context: Context, alarmList:List<Triple<Int,Int,Int>>, alarmId:Int, alarmUpdateTime:Int){

        //알람을 등록하기 위한 알람 매니저를 선언한다.
        var alarmManager = context.applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        //알람 날짜 및 시간을 담기위한 Calendar 객체를 선언한다.
        val calendar = Calendar.getInstance().apply {
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        //Calendar 객체를 알람을 여러개 등록하기 위해 배열로 선언하였다.
        var alarmTimes = mutableListOf<Calendar>()

        //파라미터로 전달받은 Tiple List 알람 리스트를 반복문을 통해 Calendar 객체에 담는다.
        for(i in 0 until alarmList.size){
            //알람의 요일을 Calendar 객체에 담는다.
            calendar.set(Calendar.DAY_OF_WEEK, alarmList[i].first)
            //알람의 시간을 Calendar 객체에 담는다.
            calendar.set(Calendar.HOUR_OF_DAY, alarmList[i].second)
            //알람의 분을 Calendar 객체에 담는다.
            calendar.set(Calendar.MINUTE, alarmList[i].third)
            //배열로 선언한 캘린더 객체에 담는다.
            alarmTimes.add(calendar.clone() as Calendar)
        }

        //배열로 선언한 Calendar 객체를 반복문을 통해 알람을 등록한다.
        alarmTimes.forEachIndexed { index, alarmTime ->

            //알람을 등록하기위한 브로드캐스트 리시버를 인텐트에 담는다.
            var alarmIntent:Intent = Intent(context.applicationContext, AlarmBroadcastReceiver::class.java).apply {
                //앱 배터리 최적화를 무시하기 위해 선언하였다.
                Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                //알람을 등록하기 위해 알람 아이디를 인텐트에 담는다.
                putExtra(ALARM_DATABASE_KEY, alarmId)
                putExtra(ALARM_REQUEST_CODE, createAlarmId(alarmId, index, alarmUpdateTime))
                //알람을 등록하기 위해 액션을 인텐트에 담는다.
                action = ALARM_REQUEST_TO_BROADCAST
            }

            //알람을 등록하기 위한 pendingIntent를 선언한다.
            val pendingIntent = PendingIntent.getBroadcast(
                context.applicationContext,
                createAlarmId(alarmId, index, alarmUpdateTime),
                alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

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
            Log.e(TAG,"등록한 알람의 아이디 = ${createAlarmId(alarmId, index, alarmUpdateTime)}")
        }
    }

    //알람을 삭제하는 메소드
//    private fun removeAlarm(context: Context, alarmId:Int){
//        GlobalScope.launch {
//            //알람의 id를 이용해 database에서 알람을 조회한다.
//            AlarmDatabase.getDatabase(context).alarmDao().getAlarm(alarmId).apply {
//                //알람을 삭제하기 위한 알람 매니저를 선언한다.
//                var alarmManager = context.applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//                //알람의 개수만큼 반복문을 돌기위한 변수
//                val alarmSize:Int = this.alarmDate.size * this.dailyDosage
//                val alarmUpdateTime:Int = dateToMillisecondTime(this.updateTime)
//                for (i in 0 until alarmSize) {
//                    //알람을 삭제하기위한 브로드캐스트 리시버를 인텐트에 담는다.
//                    var alarmIntent:Intent = Intent(context.applicationContext, AlarmBroadcastReceiver::class.java).apply {
//                        //앱 배터리 최적화를 무시하기 위해 선언하였다.
//                        Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
//                        //알람을 삭제하기 위해 알람 아이디를 인텐트에 담는다.
//                        putExtra(ALARM_REQUEST_CODE, createAlarmId(alarmId, i, alarmUpdateTime))
//                        //알람을 등록하기 위해 액션을 인텐트에 담는다.
//                        action = ALARM_REQUEST_TO_BROADCAST
//                    }
//
//                    //알람을 삭제하기 위한 pendingIntent를 선언한다.
//                    val pendingIntent = PendingIntent.getBroadcast(
//                        context.applicationContext,
//                        createAlarmId(alarmId, i, alarmUpdateTime),
//                        alarmIntent,
//                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//                    )
//                    //pendingIntent 객체의 이벤트 수행을 취소한다.
//                    pendingIntent.cancel()
//                    //알람을 취소한다.
//                    alarmManager.cancel(pendingIntent)
//
//                    Log.e(TAG,"지운 알람의 아이디 = ${alarmId}")
//                }
//            }
//        }
//    }

    //코루틴 스코프 내에서 변수의 재할당이 불가능하여 선언한 notification title setter 메소드
    private fun setTitle(title:String){
        this.title = title
    }

    //코루틴 스코프 내에서 변수의 재할당이 불가능하여 선언한 notification body setter 메소드
    private fun setBody(body:String){
        this.body = body
    }

    //알람이 발생 시 의약품 미리 알림을 설정 했다면 의약품 재고량을 차감하고 최소 보유량 도달시 FCM을 전송하는 메서드
    //코루틴 스코프 내에서 변수 할당이 불가능 한 이유로 인해 Pair(의약품 제목, 의약품 명)를 반환함
    private fun updateStock(context: Context, id:Int):Pair<String,String>{
        GlobalScope.launch {
            //울린 알람의 id를 이용해 database에서 알람을 조회한다.
            Log.e(TAG,"@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@${id}")
            AlarmDatabase.getDatabase(context).alarmDao().getAlarm(id).let {
                Log.e(TAG,"data = $it")
                //코루틴 스코프 내에서 변수의 재할당이 불가능해 setter 메서드로 database에 등록한 알람 제목을 할당한다.
                setTitle(it.title)
                //코루틴 스코프 내에서 변수의 재할당이 불가능해 setter 메서드로 database에 등록한 알람 내용(약 이름)을 할당한다.
                setBody(it.medicines)
                //의약품 미리 알림 여부가 true일 때
                if (it.lowStockAlert) {
                    //재고량을 일회 섭취량 만큼 감소시킨다.
                    it.stockQuantity = it.stockQuantity - it.dailyRepeatTime
                    //재고량이 최소 재고량 이하일 때
                    if (it.stockQuantity <= it.minStockQuantity) {
                        //TODO FCM 보내기
                        //FCM을 보낸다.
                        RetrofitInstance.api.sendFcm(AlarmDatabase.getDatabase(context).tokenDao().getAllToken()[0].token, "의약품 재고량이 부족합니다.", "\"${it.title}\" 알람 의 ${it.medicines} 의약품 재고량이 부족합니다.\n잔여재고량 : ${it.stockQuantity}개")
                    }
                    //TODO 테스트 필요
                    //재고량을 갱신한다.
                    AlarmDatabase.getDatabase(context).alarmDao().updateAlarm(it)
                }
            }
        }
        return Pair(this.title, this.body)
    }
}