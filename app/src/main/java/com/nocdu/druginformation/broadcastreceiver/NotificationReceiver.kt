package com.nocdu.druginformation.broadcastreceiver

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import com.nocdu.druginformation.utill.Constants.ACTION_CANCEL_NOTIFICATION

class NotificationReceiver: BroadcastReceiver() {

    final val TAG:String = "NotificationReceiver"

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.e(TAG,"노티 리시버 동작")
        if (intent?.action == ACTION_CANCEL_NOTIFICATION) {
            Log.e(TAG,"노티 리시버 동작")
            val notificationId = intent.getIntExtra("alarmClick", 0)
            Log.e(TAG,"노티 리시버 아이디 =${notificationId}")
            val intents = Intent(context?.applicationContext, AlarmBroadcastReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context?.applicationContext, notificationId, intents, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
            pendingIntent.cancel()
            //stopSoundAndVibration(context, notificationId)
        }
    }
    private fun stopSoundAndVibration(context: Context?, notificationId:Int) {
        Log.e(TAG,"노티 리시버 stopSoundAndVibration 동작")
        val notificationManager = context?.applicationContext?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notificationId)

        val ringtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        val ringtonePlayer = RingtoneManager.getRingtone(context.applicationContext, ringtone)
        if (ringtonePlayer.isPlaying) {
            Log.e(TAG,"음악이 실행중임")
            ringtonePlayer.stop()
        }else{
            Log.e(TAG,"음악이 실행중이 아님")
        }

        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context?.applicationContext?.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            context?.applicationContext?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        vibrator.cancel()
    }
}