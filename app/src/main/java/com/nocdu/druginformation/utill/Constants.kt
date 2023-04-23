package com.nocdu.druginformation.utill

import com.nocdu.druginformation.BuildConfig
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

/**
 *  상수 클래스
 *  앱에서 사용하는 상수와 공통 함수를 관리하는 클래스
 */
object Constants {
    //API 호출 시 사용하는 기본 URL
    const val BASE_URL = "http://44.202.206.211/DrugInfo/"
    //const val BASE_URL = "http://192.168.0.178:8080/"

    //페이징 어댑터 기본 페이징 사이즈
    const val PAGING_SIZE = 15
    //페이징 어댑터 최대 사이즈, 리사이클러뷰를 재사용 할 최대 사이즈
    const val PAGING_ADAPTER_MAX_SIZE = 3
    //의약품 API 조회시 사용하는 기본 페이지
    const val STARTING_PAGE_INDEX = 1

    //기본 Notification Chanel id
    const val DEFAULT_NOTIFICATION_CHANNEL_ID = "DrugInfo"
    //기본 Notification Chanel name
    const val DEFAULT_NOTIFICATION_CHANNEL_NAME = "DrugInfo"
    //기본 Notification Chanel description
    const val DEFAULT_NOTIFICATION_CHANNEL_DESC = "DrugInfo"

    //알람 등록시 브로드캐스트 리시버에 전달하기 위해 사용하는 액션
    const val ALARM_REQUEST_TO_BROADCAST = "ALARM_REQUEST_TO_BROADCAST"

    //알람이 발생시 서비스에 전달하기 위해 사용하는 액션, 소리와 진동을 발생시킨다.
    const val ACTION_START_SOUND_AND_VIBRATION = "ACTION_START_SOUND_AND_VIBRATION"
    //알람 해제시 서비스에 전달하기 위해 사용하는 액션, 소리와 진동을 해제한다.
    const val ACTION_STOP_SOUND_AND_VIBRATION = "ACTION_STOP_SOUND_AND_VIBRATION"

    //뷰페이저 페이지 개수
    const val NUM_TABS = 4

    //기본 알람 제목
    const val ALARM_TITLE = "의약품 알람"
    //기본 알람 내용
    const val ALARM_BODY = "약"

    //알람 요청, 삭제 시 사용하는 키
    const val ALARM_REQUEST_CODE = "ALARM_REQUEST_CODE"

    //기기가 꺼진 상태에서 알람이 발생했을 때 깨우기 위한 WakeLock 태그
    const val ACQUIRE_WAKE_LOCK = "AlarmReceiver:WakeLock"

    //의약품 검색 API URL
    const val DRUG_SEARCH_URI = "drugsearch/textsearch"
    //FCM 전송 API URL
    const val FCM_SEND_URL = "fcm/send"
    //개인정보 처리방침 URL
    const val AGREEMENTS_URL = "agreements"

    //알람 데이터베이스 이름
    const val ALARM_DATABASE_NAME = "alarm_database"
    //의약품 즐겨찾기 데이터베이스 이름
    const val BOOKMARK_DATABASE_NAME = "favorite-drugs"

    //알람 테이블 이름
    const val ALARM_TABLE_NAME = "alarm"
    //의약품 즐겨찾기 테이블 이름
    const val DRUG_TABLE_NAME = "drugs"
    //알람 등록시간 테이블 이름
    const val DOSE_TIME_TABLE_NAME = "doses_time"
    //FCM 테이블 이름
    const val FCM_TABLE_NAME = "token"

    //의약품 검색 결과가 없을 때 사용하는 메시지
    const val DRUG_SEARCH_RESULT_ERROR = "에러가 발생했습니다."

    //네트워크 연결 타임아웃
    const val RETROFIT_NETWORK_CONNECTION_TIMEOUT = 1L
    //네트워크 읽기 타임아웃
    const val RETROFIT_NETWORK_READ_TIMEOUT = 1L
    //네트워크 쓰기 타임아웃
    const val RETROFIT_NETWORK_WRITE_TIMEOUT = 1L
    //코틀린 Flow 구독 중단시 대기 시간
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

    //현재 시간을 요일 시간:분 으로 반환하는 함수
    fun getNowTime(plusMinute:Int):String{
        val now = LocalDateTime.now().plusMinutes((1+plusMinute).toLong())
        val formatter = DateTimeFormatter.ofPattern("a hh:mm")
        val formatted = now.format(formatter)
        return formatted
    }
}