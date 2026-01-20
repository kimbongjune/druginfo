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
 *
 *  서버 DrugSearchResponse 형식에 맞춤
 */
@Parcelize
@JsonClass(generateAdapter = true)
@Entity(tableName = DRUG_TABLE_NAME)
data class Document(
    // 기본 정보
    @Json(name = "id")
    val id: Long?,

    //의약품 품목 기준코드(PK)
    @Json(name = "itemSeq")
    @PrimaryKey(autoGenerate = false)
    @NonNull
    val itemSeq: String,

    //의약품 명
    @Json(name = "itemName")
    val itemName: String?,

    //제약사 명
    @Json(name = "entpName")
    val entpName: String?,

    //의약품 효능
    @Json(name = "efficacy")
    val efcyQesitm: String?,

    //의약품 복용법
    @Json(name = "useMethod")
    val useMethodQesitm: String?,

    //의약품 이미지
    @Json(name = "imageUrl")
    val itemImage: String?,

    // 식별 정보
    //의약품 모양
    @Json(name = "drugShape")
    val drugShape: String?,

    //앞면 색상
    @Json(name = "colorFront")
    val colorFront: String?,

    //뒷면 색상
    @Json(name = "colorBack")
    val colorBack: String?,

    //제형
    @Json(name = "formCodeName")
    val formCodeName: String?,

    //의약품 분류명
    @Json(name = "className")
    val className: String?,

    //전문/일반
    @Json(name = "etcOtcName")
    val etcOtcName: String?,

    //앞면 식별문구
    @Json(name = "printFront")
    val printFront: String?,

    //뒷면 식별문구
    @Json(name = "printBack")
    val printBack: String?,

    // 안전 정보 (요약)
    //주의사항 경고
    @Json(name = "cautionWarning")
    val warningText: String?,

    //부작용
    @Json(name = "sideEffect")
    val allegyReaction: String?,

    //상호작용
    @Json(name = "interaction")
    val interactionCaution: String?,

    //보관방법
    @Json(name = "storageMethod")
    val storageMethod: String?,

    // 상세 안전 정보
    //임부 투여 주의사항
    @Json(name = "pregnantWarning")
    val pregnantWomenInjectWarning: String?,

    //수유부 투여 주의사항
    @Json(name = "lactationWarning")
    val lactionWomenInjectWarning: String?,

    //소아 투여 주의사항
    @Json(name = "childWarning")
    val childInjectWarning: String?,

    //노인 투여 주의사항
    @Json(name = "elderlyWarning")
    val oldmanInjectWarning: String?,

    //과복용시 증상 및 처치사항
    @Json(name = "overdoseWarning")
    val overdoseTreatment: String?,

    // 추가 안전 정보 (앱 기존 필드 호환)
    //투여금지
    @Json(name = "noInject")
    val noInject: String?,

    //투여신중
    @Json(name = "cautionInject")
    val cautionInject: String?,

    //일반적 주의사항
    @Json(name = "generalCaution")
    val generalCaution: String?,

    //기타 주의사항
    @Json(name = "extraCaution")
    val extraCaution: String?,

    //투여 전 의사 상담
    @Json(name = "beforeConsultDoctor")
    val beforeConsultDoctor: String?,

    //복용 중지 후 의사 상담
    @Json(name = "afterConsultDoctor")
    val afterConsultDoctor: String?,

    //투여시 주의사항
    @Json(name = "doseWarning")
    val doseCaution: String?,

    //유통기한
    @Json(name = "validTerm")
    val validTerm: String?,

    //보험코드
    @Json(name = "ediCode")
    val ediCode: String?,

    //복합적인 투여 주의사항(임부, 소아, 노년, 기타) - 서버에서 미제공시 null
    @Json(name = "multieInjectWarning")
    val multieInjectWarning: String? = null,

    //임부 및 수유부 투여 주의사항 - 서버에서 미제공시 null
    @Json(name = "pregnantWomenWithLactationWomanWarning")
    val pregnantWomenWithLactationWomanWarning: String? = null,

    // === 추가 정보: 약가/성분 ===
    
    //약가 정보
    @Json(name = "priceInfo")
    val priceInfo: PriceInfo? = null,

    //성분 정보 목록
    @Json(name = "ingredients")
    val ingredients: List<IngredientInfo>? = null

) : Parcelable, Serializable

/**
 * 약가 정보 DTO
 */
@Parcelize
@JsonClass(generateAdapter = true)
@kotlinx.serialization.Serializable
data class PriceInfo(
    @Json(name = "ediCode")
    val ediCode: String? = null,
    @Json(name = "drugAmount")
    val drugAmount: Double? = null,
    @Json(name = "maxPrice")
    val maxPrice: Double? = null,
    @Json(name = "unit")
    val unit: String? = null,
    @Json(name = "spec")
    val spec: String? = null,
    @Json(name = "payType")
    val payType: String? = null,
    @Json(name = "applyStartDate")
    val applyStartDate: String? = null,
    @Json(name = "applyEndDate")
    val applyEndDate: String? = null
) : Parcelable, Serializable

/**
 * 성분 정보 DTO
 */
@Parcelize
@JsonClass(generateAdapter = true)
@kotlinx.serialization.Serializable
data class IngredientInfo(
    @Json(name = "componentCode")
    val componentCode: String? = null,
    @Json(name = "componentKorName")
    val componentKorName: String? = null,
    @Json(name = "componentEngName")
    val componentEngName: String? = null,
    @Json(name = "formulaCode")
    val formulaCode: String? = null,
    @Json(name = "formulaName")
    val formulaName: String? = null,
    @Json(name = "admRoute")
    val admRoute: String? = null,
    @Json(name = "atcCode")
    val atcCode: String? = null,
    @Json(name = "atcName")
    val atcName: String? = null
) : Parcelable, Serializable