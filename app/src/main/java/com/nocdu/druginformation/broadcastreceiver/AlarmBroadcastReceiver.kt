package com.nocdu.druginformation.broadcastreceiver

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
import android.util.Log
import androidx.core.app.NotificationCompat
import com.nocdu.druginformation.R
import com.nocdu.druginformation.utill.Constants.ACTION_CANCEL_NOTIFICATION


class AlarmBroadcastReceiver : BroadcastReceiver(){

    final val TAG:String = "AlarmBroadcastReceiver"

    override fun onReceive(context: Context, intent: Intent) {
        // alarm to vibrator and soundcode here
        Log.e(TAG, "알람 리시버 동작")
        if(intent.extras?.get("alarmRequestCode") != null){
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

            val newIntent = Intent(context, AlarmBroadcastReceiver::class.java)
            newIntent.action = ACTION_CANCEL_NOTIFICATION
            newIntent.putExtra("alarmClick", 2)
            newIntent.putExtra("alarmRequestCode", 2)

            val pendingIntent = PendingIntent.getBroadcast(context, 2, newIntent, PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_MUTABLE)

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
            notificationManager.notify(2, notification.build())
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
}