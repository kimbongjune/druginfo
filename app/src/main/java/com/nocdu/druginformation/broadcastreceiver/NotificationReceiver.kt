package com.nocdu.druginformation.broadcastreceiver

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import com.nocdu.druginformation.utill.Constants.ACTION_CANCEL_NOTIFICATION

class NotificationReceiver: BroadcastReceiver() {

    private lateinit var mediaPlayer: MediaPlayer

    final val TAG:String = "NotificationReceiver"

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.e(TAG,"노티 리시버 동작")
        if (intent?.action == ACTION_CANCEL_NOTIFICATION) {
            Log.e(TAG,"노티 리시버 동작")
            val notificationId = intent.getIntExtra("alarmClick", 0)
            Log.e(TAG,"노티 리시버 아이디 =${notificationId}")
            stopSoundAndVibration(context, notificationId)
        }
    }
    private fun stopSoundAndVibration(context: Context?, notificationId:Int) {
        Log.e(TAG,"노티 리시버 stopSoundAndVibration 동작")
        val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notificationId)

        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.cancel()
    }
}