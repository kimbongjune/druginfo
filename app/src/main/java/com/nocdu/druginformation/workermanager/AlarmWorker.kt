package com.nocdu.druginformation.workermanager

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.nocdu.druginformation.service.AlarmService
import com.nocdu.druginformation.utill.Constants

/**
 *  알람 워커매니저 클래스
 *  알람이 울려 서비스가 동작하면 예약작업을 수행하는 클래스
 *  사용자의 노티피케이션 권한이 없거나 기타 사유로 인해 소리와 진동이 취소되지 않을 경우를 대비하여 작성
 *  알람이 발생하고 1분 뒤에 알람의 소리와 진동을 취소한다.
 */
class AlarmWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    final val TAG:String = "AlarmWorker"

    //실제 작업을 수행하는 함수
    override fun doWork(): Result {
        Log.e(TAG,"AlarmWorker doWork")
        return try {
            //inputData로 전달받은 아이디를 가져온다.
            val id  = inputData.getInt("id", 0)
            //알람 서비스 Intent를 생성한다.
            val serviceIntent = Intent(applicationContext, AlarmService::class.java)
            //알람 서비스에 전달할 데이터를 넣는다.
            serviceIntent.putExtra(Constants.ALARM_REQUEST_CODE, id)
            //알람 서비스에 알람을 취소하라는 액션을 넣는다.
            serviceIntent.action = Constants.ACTION_STOP_SOUND_AND_VIBRATION
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                //applicationContext.startForegroundService(serviceIntent)
//            } else {
//                applicationContext.startService(serviceIntent)
//            }
            //알람 서비스를 시작한다.
            applicationContext.startService(serviceIntent)
            Log.e(TAG,"AlarmWorker doWork success")
            Result.success()
        }catch (e: Exception){
            Log.e(TAG,"AlarmWorker doWork failure")
            Result.failure()
        }
    }
}