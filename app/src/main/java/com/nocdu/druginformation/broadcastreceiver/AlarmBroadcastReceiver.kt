package com.nocdu.druginformation.broadcastreceiver

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationCompat
import com.nocdu.druginformation.R
import com.nocdu.druginformation.data.api.RetrofitInstance
import com.nocdu.druginformation.data.database.AlarmDatabase
import com.nocdu.druginformation.ui.view.MainActivity
import com.nocdu.druginformation.utill.Constants.ACTION_CANCEL_NOTIFICATION
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*


class AlarmBroadcastReceiver : BroadcastReceiver(){

    final val TAG:String = "AlarmBroadcastReceiver"

    override fun onReceive(context: Context, intent: Intent) {
        // alarm to vibrator and soundcode here
        Log.e(TAG, "알람 리시버 동작")
        if(intent.extras?.get("alarmRequestCode") != null){
            val id:Int = intent.extras?.get("alarmRequestCode").toString().toInt()
            val wakeLock = acquireWakeLock(context, PowerManager.PARTIAL_WAKE_LOCK)
            // 알람 울림에 필요한 리소스 로드
//            val ringtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
//            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
//                vibratorManager.defaultVibrator
//            } else {
//                context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
//            }
//
//            // 액션 실행
//            val ringtonePlayer = RingtoneManager.getRingtone(context, ringtone)
//            ringtonePlayer.play()
//
//            val pattern = longArrayOf(1000, 1000, 1000, 1000, 1000)
//            val vibrationEffect = VibrationEffect.createWaveform(pattern, 0)
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                vibrator.vibrate(VibrationEffect.createWaveform(pattern, VibrationEffect.DEFAULT_AMPLITUDE))
//            } else {
//                vibrator.vibrate(vibrationEffect)
//            }

            reRegistrationAlarm(context,id)
            //removeAlarm(context,id)

            GlobalScope.launch {
                AlarmDatabase.getDatabase(context).alarmDao().getAlarm(id).apply {
                    if (this.lowStockAlert) {
                        this.stockQuantity = this.stockQuantity - this.dailyRepeatTime
                        if (this.stockQuantity <= this.minStockQuantity) {
                            //TODO FCM 보내기
                            RetrofitInstance.api.sendFcm(AlarmDatabase.getDatabase(context).tokenDao().getAllToken()[0].token)
                        }
                        //TODO 테스트 필요
                        AlarmDatabase.getDatabase(context).alarmDao().updateAlarm(this)
                    }
                }
            }
//            GlobalScope.launch {
//                MainActivity.getInstance().alarmViewModel.getAlarm(id).await().apply {
//                    if(this.lowStockAlert){
//                        this.stockQuantity = this.stockQuantity - this.dailyRepeatTime
//                        if(this.stockQuantity <= this.minStockQuantity){
//                            //TODO FCM 보내기
//                            //MainActivity.getInstance().alarmViewModel.sendFcm(MainActivity.getInstance().alarmViewModel.getAllToken.await()[0].token)
//                        }
//                        //TODO 테스트 필요
//                        MainActivity.getInstance().alarmViewModel.updateAlarm(this)
//                    }
//                }
//            }

            val newIntent = Intent(context, NotificationReceiver::class.java)
            newIntent.action = ACTION_CANCEL_NOTIFICATION
            newIntent.putExtra("alarmClick", id)
            newIntent.putExtra("alarmRequestCode", id)

            val pendingIntent = PendingIntent.getBroadcast(context, id, newIntent, PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_MUTABLE)

            val notification = NotificationCompat.Builder(context, "default")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setContentTitle("알람")
                .setContentText("알람이 울렸습니다")
                .setContentIntent(pendingIntent)

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                val channel = NotificationChannel("default", "Notice", NotificationManager.IMPORTANCE_HIGH)
                notificationManager.createNotificationChannel(channel)
            }
            notificationManager.notify(id, notification.build())
            wakeLock?.release()
        }

    }

    private fun acquireWakeLock(context: Context?, flags: Int): PowerManager.WakeLock? {
        val powerManager = context?.getSystemService(Context.POWER_SERVICE) as PowerManager?
        return powerManager?.run {
            newWakeLock(flags or PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.ON_AFTER_RELEASE, "AlarmReceiver:WakeLock")
                .apply {
                    acquire()
                }
        }
    }

    private fun reRegistrationAlarm(context: Context, intents: Intent, id:Int){
        val calendar = Calendar.getInstance().apply {
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.DAY_OF_WEEK)
        calendar.set(Calendar.HOUR_OF_DAY, Calendar.HOUR_OF_DAY)
        calendar.set(Calendar.MINUTE, Calendar.MINUTE)
        calendar.add(Calendar.DATE, 7)

        val intent = Intent(context, AlarmBroadcastReceiver::class.java).apply {
            Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
            putExtra("alarmRequestCode", id)
            action = "com.example.alarm"
        }

        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            //AlarmManager.INTERVAL_DAY * 7,
            pendingIntent
        )

        Log.e(TAG,"리 세팅 알람")
    }

    fun reRegistrationAlarm(context: Context, alarmId:Int){
        var alarmIntent:Intent = Intent(context.applicationContext, AlarmBroadcastReceiver::class.java).apply {
            Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
            putExtra("alarmRequestCode", alarmId)
            action = "com.example.alarm"
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

    fun removeAlarm(context: Context, alarmId:Int){
        var alarmIntent:Intent = Intent(context.applicationContext, AlarmBroadcastReceiver::class.java).apply {
            Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
            putExtra("alarmRequestCode", alarmId)
            action = "com.example.alarm"
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context.applicationContext,
            alarmId,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        var alarmManager = context.applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        pendingIntent.cancel()
        alarmManager.cancel(pendingIntent)

        Log.e(TAG,"지운 알람의 아이디 = ${alarmId}")
    }
}