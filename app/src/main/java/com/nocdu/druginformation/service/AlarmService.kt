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
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.nocdu.druginformation.ui.view.MainActivity
import com.nocdu.druginformation.utill.Constants
import com.nocdu.druginformation.utill.Constants.ACTION_START_SOUND_AND_VIBRATION
import com.nocdu.druginformation.utill.Constants.ACTION_STOP_SOUND_AND_VIBRATION
import com.nocdu.druginformation.workermanager.AlarmWorker
import java.util.concurrent.TimeUnit

/**
 *  알람이 울릴 때 동작하는 서비스
 *  백그라운드에서 브로드캐스트리시버가 실행시킨다.
 *  알람의 소리와 진동을 발생시키고 브로드캐스트리시버가 발생시킨 노티피케이션의 이벤트를 처리한다.
 */
class AlarmService : Service() {

    private val TAG = "AlarmService"

    //알람의 소리와 진동을 발생시키기 위한 변수를 static하게 선언하였다.
    companion object {
        private lateinit var ringtone: Uri

        private lateinit var ringtonePlayer: Ringtone

        private lateinit var vibrator: Vibrator
    }

    //서비스가 생성될 때 실행되는 함수
    override fun onCreate() {
        //알람의 소리를 담는 변수 단말기의 기본 알람음을 저장한다. 동일한 객체로 재생 및 취소를 하기위해 onCreate에서 값을 할당하였다.
        ringtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        //알람의 소리를 재생 및 취소 하기 위한 변수 동일한 객체로 재생 및 취소를 하기위해 onCreate에서 값을 할당하였다.
        ringtonePlayer = RingtoneManager.getRingtone(this, ringtone)

        //알람의 진동을 발생시키기 위한 변수 동일한 객체로 재생 및 취소를 하기위해 onCreate에서 값을 할당하였다.
        //안드로이드 12버전부터는 VibratorManager를 사용해야한다.
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = this.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            this.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        super.onCreate()
    }

    //서비스에 intent를 이용한 데이터가 전달되어 실행될 때 실행되는 함수
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.e(TAG,"onStartCommand")

        //노티피케이션 발생시 전달된 알람의 id를 가져온다.
        val id:Int = intent.extras?.get(Constants.ALARM_REQUEST_CODE).toString().toInt()
        //액션에 따른 이벤트처리 알람의 소리와 진동을 실행한다.
        if(intent.action == ACTION_START_SOUND_AND_VIBRATION) {
            Log.e(TAG,"ACTION_START_SOUND_AND_VIBRATION")
            val workManager = WorkManager.getInstance(this)
            val inputData = Data.Builder()
                .putInt("id", id)
                .build()
            val constraints = Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .build()
            val soundCancelWork = OneTimeWorkRequestBuilder<AlarmWorker>()
                .setConstraints(constraints)
                .setInputData(inputData)
                .setInitialDelay(1, TimeUnit.MINUTES) // 1분 후에 예약
                .build()
            workManager.enqueue(soundCancelWork)
            startSoundAndVibration()
            //발생시킨 알람의 소리와 진동을 취소한다.
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

    //서비스가 종료될 때 실행되는 함수 알람의 소리와 진동을 취소한다.
    override fun onDestroy() {
        super.onDestroy()
        ringtonePlayer?.stop()
        vibrator?.cancel()
    }

    //알람의 소리와 진동, 노티피케이션 을 취소하는 함수 아이디를 이용해 노티피케이션을 취소한다.
    private fun stopSoundAndVibration(notificationId:Int) {
        //소리를 중지한다.
        ringtonePlayer?.stop()
        //진동을 중지한다.
        vibrator?.cancel()

        //노티피케이션객체를 가져온다.
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        //노티피케이션을 취소한다.
        notificationManager.cancel(notificationId)
    }

    //알람의 소리와 진동을 실행하는 함수
    private fun startSoundAndVibration() {
        //알람의 소리를 재생한다.
        ringtonePlayer.play()

        //알람의 진동 패턴을 설정한다.
        val pattern = longArrayOf(1000, 1000, 1000, 1000, 1000)
        //알람의 진동 패턴을 이용해 반복 패턴을 생성한다.
        val vibrationEffect = VibrationEffect.createWaveform(pattern, VibrationEffect.EFFECT_CLICK)
        //알람의 진동을 실행한다.
        //안드로이드 12버전부터는 VibratorManager를 사용해야한다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, VibrationEffect.EFFECT_CLICK))
        } else {
            vibrator.vibrate(vibrationEffect)
        }
    }
}