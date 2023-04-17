package com.nocdu.druginformation.workermanager

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.nocdu.druginformation.service.AlarmService
import com.nocdu.druginformation.utill.Constants

class AlarmWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    final val TAG:String = "AlarmWorker"

    override fun doWork(): Result {
        Log.e(TAG,"AlarmWorker doWork")
        return try {
            val id  = inputData.getInt("id", 0)
            val serviceIntent = Intent(applicationContext, AlarmService::class.java)
            serviceIntent.putExtra(Constants.ALARM_REQUEST_CODE, id)
            serviceIntent.action = Constants.ACTION_STOP_SOUND_AND_VIBRATION
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                //applicationContext.startForegroundService(serviceIntent)
//            } else {
//                applicationContext.startService(serviceIntent)
//            }
            applicationContext.startService(serviceIntent)
            Log.e(TAG,"AlarmWorker doWork success")
            Result.success()
        }catch (e: Exception){
            Log.e(TAG,"AlarmWorker doWork failure")
            Result.failure()
        }
    }
}