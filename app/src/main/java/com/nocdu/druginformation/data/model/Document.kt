package com.nocdu.druginformation.data.model


import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
@JsonClass(generateAdapter = true)
@Entity(tableName = "drugs")
data class Document(
    @Json(name = "after_consult_doctor")
    val afterConsultDoctor: String?,
    @Json(name = "allegy_reaction")
    val allegyReaction: String?,
    @Json(name = "before_consult_doctor")
    val beforeConsultDoctor: String?,
    @Json(name = "caution_inject")
    val cautionInject: String?,
    @Json(name = "child_inject_warning")
    val childInjectWarning: String?,
    @Json(name = "class_name")
    val className: String?,
    @Json(name = "dose_caution")
    val doseCaution: String?,
    @Json(name = "edi_code")
    val ediCode: String?,
    @Json(name = "efcy_qesitm")
    val efcyQesitm: String?,
    @Json(name = "entp_name")
    val entpName: String?,
    @Json(name = "extra_caution")
    val extraCaution: String?,
    @Json(name = "general_caution")
    val generalCaution: String?,
    @Json(name = "interaction_caution")
    val interactionCaution: String?,
    @Json(name = "item_image")
    val itemImage: String?,
    @Json(name = "item_name")
    val itemName: String?,
    @Json(name = "item_seq")
    @PrimaryKey(autoGenerate = false)
    @NonNull
    val itemSeq: String,
    @Json(name = "laction_women_inject_warning")
    val lactionWomenInjectWarning: String?,
    @Json(name = "multie_inject_warning")
    val multieInjectWarning: String?,
    @Json(name = "no_inject")
    val noInject: String?,
    @Json(name = "oldman_inject_warning")
    val oldmanInjectWarning: String?,
    @Json(name = "overdose_treatment")
    val overdoseTreatment: String?,
    @Json(name = "pregnant_women_inject_warning")
    val pregnantWomenInjectWarning: String?,
    @Json(name = "pregnant_women_with_lactation_woman_warning")
    val pregnantWomenWithLactationWomanWarning: String?,
    @Json(name = "storage_method")
    val storageMethod: String?,
    @Json(name = "use_method_qesitm")
    val useMethodQesitm: String?,
    @Json(name = "valid_term")
    val validTerm: String?,
    @Json(name = "warning_text")
    val warningText: String?
) : Parcelable, Serializable