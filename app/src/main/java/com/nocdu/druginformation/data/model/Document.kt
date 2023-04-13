package com.nocdu.druginformation.data.model


import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nocdu.druginformation.utill.Constants.DRUG_TABLE_NAME
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import java.io.Serializable

/**
 *  Document 데이터 클래스
 *  데이터베이스에 저장할 약 정보 데이터를 정의한다.
 *  API에서 사용 할 데이터를 정의한다.
 *  데이터 사용을 위해 직렬화 한다.
 */
@Parcelize
@JsonClass(generateAdapter = true)
@Entity(tableName = DRUG_TABLE_NAME)
data class Document(
    //증상 발생시 의사에게 상담이 필요한 항목
    @Json(name = "after_consult_doctor")
    val afterConsultDoctor: String?,
    //알레르기 반응
    @Json(name = "allegy_reaction")
    val allegyReaction: String?,
    //투여 전 의사에게 상담이 필요한 항목
    @Json(name = "before_consult_doctor")
    val beforeConsultDoctor: String?,
    //투여시 신중해야 할 항목
    @Json(name = "caution_inject")
    val cautionInject: String?,
    //소아 투여시 주의사항
    @Json(name = "child_inject_warning")
    val childInjectWarning: String?,
    //의약품 분류명
    @Json(name = "class_name")
    val className: String?,
    //투여시 주의사항
    @Json(name = "dose_caution")
    val doseCaution: String?,
    //보험코드
    @Json(name = "edi_code")
    val ediCode: String?,
    //의약품 효능
    @Json(name = "efcy_qesitm")
    val efcyQesitm: String?,
    //제약사 명
    @Json(name = "entp_name")
    val entpName: String?,
    //기타 주의사항
    @Json(name = "extra_caution")
    val extraCaution: String?,
    //일반적인 주의사항
    @Json(name = "general_caution")
    val generalCaution: String?,
    //상호작용
    @Json(name = "interaction_caution")
    val interactionCaution: String?,
    //의약품 이미지
    @Json(name = "item_image")
    val itemImage: String?,
    //의약품 명
    @Json(name = "item_name")
    val itemName: String?,
    //의약품 품목 기준코드(PK)
    @Json(name = "item_seq")
    @PrimaryKey(autoGenerate = false)
    @NonNull
    val itemSeq: String,
    //수유부 투여 주의사항
    @Json(name = "laction_women_inject_warning")
    val lactionWomenInjectWarning: String?,
    //복합적인 투여 주의사항(임부, 소아, 노년, 기타)
    @Json(name = "multie_inject_warning")
    val multieInjectWarning: String?,
    //투여 금지 항목
    @Json(name = "no_inject")
    val noInject: String?,
    //노인 투여 주의사항
    @Json(name = "oldman_inject_warning")
    val oldmanInjectWarning: String?,
    //과복용시 증상 및 처치사항
    @Json(name = "overdose_treatment")
    val overdoseTreatment: String?,
    //임부 투여 주의사항
    @Json(name = "pregnant_women_inject_warning")
    val pregnantWomenInjectWarning: String?,
    //임부 및 수유부 투여 주의사항
    @Json(name = "pregnant_women_with_lactation_woman_warning")
    val pregnantWomenWithLactationWomanWarning: String?,
    //보관방법
    @Json(name = "storage_method")
    val storageMethod: String?,
    //의약품 복용법
    @Json(name = "use_method_qesitm")
    val useMethodQesitm: String?,
    //유통기한
    @Json(name = "valid_term")
    val validTerm: String?,
    //경고문구
    @Json(name = "warning_text")
    val warningText: String?
) : Parcelable, Serializable