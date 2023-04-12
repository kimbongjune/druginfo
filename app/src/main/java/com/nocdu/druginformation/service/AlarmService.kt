package com.nocdu.druginformation.service

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.*
import android.util.Log
import com.nocdu.druginformation.ui.view.MainActivity
import com.nocdu.druginformation.utill.Constants
import com.nocdu.druginformation.utill.Constants.ACTION_START_SOUND_AND_VIBRATION
import com.nocdu.druginformation.utill.Constants.ACTION_STOP_SOUND_AND_VIBRATION

class AlarmService : Service() {

    private val TAG = "AlarmService"
    companion object {
        private lateinit var ringtone: Uri

        private lateinit var ringtonePlayer: Ringtone

        private lateinit var vibrator: Vibrator
    }

    override fun onCreate() {
        ringtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        ringtonePlayer = RingtoneManager.getRingtone(this, ringtone)

        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = this.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            this.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.e(TAG,"onStartCommand")

        val id:Int = intent.extras?.get(Constants.ALARM_REQUEST_CODE).toString().toInt()
        if(intent.action == ACTION_START_SOUND_AND_VIBRATION) {
            Log.e(TAG,"ACTION_START_SOUND_AND_VIBRATION")
            startSoundAndVibration()
        }else if (intent.action == ACTION_STOP_SOUND_AND_VIBRATION) {
            Log.e(TAG,"ringtonePlayer.isPlaying = ${ringtonePlayer.isPlaying}")
            Log.e(TAG,"virbrator.hasVibrator = ${vibrator.hasVibrator()}")
            stopSoundAndVibration(id)
            stopSelf()
            onDestroy()
        }
        return START_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        ringtonePlayer?.stop()
        vibrator?.cancel()
    }

    private fun stopSoundAndVibration(notificationId:Int) {
        // 액션 실행
        ringtonePlayer.stop()
        vibrator.cancel()

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notificationId)
    }

    private fun startSoundAndVibration() {
        // 액션 실행
        ringtonePlayer.play()

        val pattern = longArrayOf(1000, 1000, 1000, 1000, 1000)
        val vibrationEffect = VibrationEffect.createWaveform(pattern, 0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(vibrationEffect)
        }
    }
}