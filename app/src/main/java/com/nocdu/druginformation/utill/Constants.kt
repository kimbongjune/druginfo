package com.nocdu.druginformation.utill

import com.nocdu.druginformation.BuildConfig
import java.text.SimpleDateFormat
import java.util.*

object Constants {
    const val BASE_URL = "http://44.202.206.211/DrugInfo/"

    //const val BASE_URL = "http://192.168.0.178:8080/"
    const val SEARCH_DRUGS_TIME_DELAY = 100L
    const val PAGING_SIZE = 15
    const val PAGING_ADAPTER_MAX_SIZE = 3
    const val STARTING_PAGE_INDEX = 1

    const val DEFAULT_NOTIFICATION_CHANNEL_ID = "DrugInfo"
    const val DEFAULT_NOTIFICATION_CHANNEL_NAME = "DrugInfo"
    const val DEFAULT_NOTIFICATION_CHANNEL_DESC = "DrugInfo"

    const val ALARM_REQUEST_TO_BROADCAST = "ALARM_REQUEST_TO_BROADCAST"

    const val ACTION_START_SOUND_AND_VIBRATION = "ACTION_START_SOUND_AND_VIBRATION"
    const val ACTION_STOP_SOUND_AND_VIBRATION = "ACTION_STOP_SOUND_AND_VIBRATION"

    const val NUM_TABS = 4

    const val ALARM_TITLE = "의약품 알람"
    const val ALARM_BODY = "약"

    const val ALARM_REQUEST_CODE = "ALARM_REQUEST_CODE"

    const val ACQUIRE_WAKE_LOCK = "AlarmReceiver:WakeLock"

    const val DRUG_SEARCH_URI = "drugsearch/textsearch"
    const val FCM_SEND_URL = "fcm/send"
    const val AGREEMENTS_URL = "agreements"

    const val ALARM_DATABASE_NAME = "alarm_database"
    const val BOOKMARK_DATABASE_NAME = "favorite-drugs"

    const val ALARM_TABLE_NAME = "alarm"
    const val DRUG_TABLE_NAME = "drugs"
    const val DOSE_TIME_TABLE_NAME = "doses_time"
    const val FCM_TABLE_NAME = "token"

    const val DRUG_SEARCH_RESULT_ERROR = "에러가 발생했습니다."

    const val RETROFIT_NETWORK_CONNECTION_TIMEOUT = 1L
    const val RETROFIT_NETWORK_READ_TIMEOUT = 1L
    const val RETROFIT_NETWORK_WRITE_TIMEOUT = 1L

    const val COROUTINE_STAT_IN_STOP_TIME = 5000L

    //문자열로 전달받은 시간을 Pair 객체로 시간, 분으로 나누어 반환하는 메소드
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
}