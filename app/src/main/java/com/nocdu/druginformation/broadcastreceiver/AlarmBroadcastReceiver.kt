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
import com.nocdu.druginformation.utill.Constants.ALARM_REQUEST_CODE
import com.nocdu.druginformation.utill.Constants.ALARM_REQUEST_TO_BROADCAST
import com.nocdu.druginformation.utill.Constants.ALARM_TITLE
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class AlarmBroadcastReceiver : BroadcastReceiver(){

    final val TAG:String = "AlarmBroadcastReceiver"

    private var title:String = ALARM_TITLE
    private var body:String = ALARM_BODY

    override fun onReceive(context: Context, intent: Intent) {
        Log.e(TAG, "알람 리시버 동작")

        if(intent.action == ACTION_BOOT_COMPLETED){

            val wakeLock = acquireWakeLock(context.applicationContext, PowerManager.PARTIAL_WAKE_LOCK)

            //Toast.makeText(context.applicationContext, "재부팅 되었음", Toast.LENGTH_SHORT).show()

            GlobalScope.launch {
                var alarmTimes = mutableListOf<Triple<Int,Int,Int>>()
                AlarmDatabase.getDatabase(context).alarmDao().getAllAlarms().forEach { alarmWithDosetime ->
                    for(i in 0 until alarmWithDosetime.alarm.alarmDateInt.size){
                        for(j in 0 until alarmWithDosetime.doseTime.size){
                            val hourAndMinute = convertTo24HoursFormat(alarmWithDosetime.doseTime[j].time)
                            val hour = hourAndMinute.first
                            val minute = hourAndMinute.second
                            alarmTimes.add(Triple(alarmWithDosetime.alarm.alarmDateInt[i], hour, minute))
                        }
                    }
                    removeAlarm(context, alarmWithDosetime.alarm.id).apply {
                        setAlarm(context,alarmTimes, alarmWithDosetime.alarm.id)
                    }
                }
            }
            wakeLock?.release()
        }

        if(intent.action == ALARM_REQUEST_TO_BROADCAST){
            val id:Int = if(intent.extras?.get(ALARM_REQUEST_CODE) != null) {intent.extras?.get(ALARM_REQUEST_CODE).toString().toInt()} else {0}
            val wakeLock = acquireWakeLock(context.applicationContext, PowerManager.PARTIAL_WAKE_LOCK)

            reRegistrationAlarm(context,id)

            GlobalScope.launch {
                AlarmDatabase.getDatabase(context).alarmDao().getAlarm(id).apply {
                    setTitle(this.title)
                    setBody(this.medicines)
                    if (this.lowStockAlert) {
                        this.stockQuantity = this.stockQuantity - this.dailyRepeatTime
//                        if (this.stockQuantity <= this.minStockQuantity) {
//                            //TODO FCM 보내기
//                            RetrofitInstance.api.sendFcm(AlarmDatabase.getDatabase(context).tokenDao().getAllToken()[0].token)
//                        }
//                        //TODO 테스트 필요
//                        AlarmDatabase.getDatabase(context).alarmDao().updateAlarm(this)
                    }
                }
            }

            val channelId = Constants.DEFAULT_NOTIFICATION_CHANNEL_ID
            val channelName = Constants.DEFAULT_NOTIFICATION_CHANNEL_NAME

            val newIntent = Intent(context.applicationContext, AlarmService::class.java)
            newIntent.action = ACTION_STOP_SOUND_AND_VIBRATION
            newIntent.putExtra(ALARM_REQUEST_CODE, id)

            val pendingIntent = PendingIntent.getService(context.applicationContext, id, newIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)

            val notification = NotificationCompat.Builder(context.applicationContext, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentText("의약품 (${body}) 복용시간 입니다.")
                .setContentIntent(pendingIntent)
                .setDeleteIntent(pendingIntent)

            val notificationManager = context.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
                notificationManager.createNotificationChannel(channel)
            }
            notificationManager.notify(id, notification.build())

            val serviceIntent = Intent(context, AlarmService::class.java)
            serviceIntent.putExtra(ALARM_REQUEST_CODE, id)
            serviceIntent.action =  ACTION_START_SOUND_AND_VIBRATION
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent);
            } else {
                context.startService(serviceIntent);
            }

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

    private fun reRegistrationAlarm(context: Context, alarmId:Int){
        var alarmIntent:Intent = Intent(context.applicationContext, AlarmBroadcastReceiver::class.java).apply {
            Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
            putExtra(ALARM_REQUEST_CODE, alarmId)
            action = ALARM_REQUEST_TO_BROADCAST
        }

        var alarmManager = context.applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val pendingIntent = PendingIntent.getBroadcast(
            context.applicationContext,
            alarmId,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        calendar.set(Calendar.DAY_OF_WEEK, Calendar.DAY_OF_WEEK)
        calendar.set(Calendar.HOUR_OF_DAY, Calendar.HOUR_OF_DAY)
        calendar.set(Calendar.MINUTE, Calendar.MINUTE)
        calendar.add(Calendar.DATE, 7)

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            //AlarmManager.INTERVAL_DAY * 7,
            pendingIntent
        )
    }

    fun setAlarm(context: Context, alarmList:List<Triple<Int,Int,Int>>, alarmId:Int){
        var alarmIntent:Intent = Intent(context.applicationContext, AlarmBroadcastReceiver::class.java).apply {
            Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
            putExtra(ALARM_REQUEST_CODE, alarmId)
            action = ALARM_REQUEST_TO_BROADCAST
        }

        var alarmManager = context.applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val pendingIntent = PendingIntent.getBroadcast(
            context.applicationContext,
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

    fun removeAlarm(context: Context, alarmId:Int){
        var alarmIntent:Intent = Intent(context.applicationContext, AlarmBroadcastReceiver::class.java).apply {
            Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
            putExtra(ALARM_REQUEST_CODE, alarmId)
            action = ALARM_REQUEST_TO_BROADCAST
        }

        var alarmManager = context.applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val pendingIntent = PendingIntent.getBroadcast(
            context.applicationContext,
            alarmId,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        pendingIntent.cancel()
        alarmManager.cancel(pendingIntent)

        Log.e(TAG,"지운 알람의 아이디 = ${alarmId}")
    }

    fun convertTo24HoursFormat(timeString: String):Pair<Int,Int>{
        val pattern = "hh:mm"
        val format = SimpleDateFormat(pattern, Locale.getDefault())
        val date = format.parse(timeString.substring(3))
        val calendar = Calendar.getInstance().apply { time = date }

        if (timeString.startsWith("오후")) {
            calendar.add(Calendar.HOUR, 12)
        }

        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        return Pair(hour,minute)
    }

    fun setTitle(title:String){
        this.title = title
    }

    fun setBody(body:String){
        this.body = body
    }
}