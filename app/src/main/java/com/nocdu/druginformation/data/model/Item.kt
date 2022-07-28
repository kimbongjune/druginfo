package com.nocdu.druginformation.data.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Item(
    @field:Json(name = "CHANGE_DATE")
    val cHANGEDATE: String?,
    @field:Json(name = "CHART")
    val cHART: String?,
    @field:Json(name = "CLASS_NAME")
    val cLASSNAME: String?,
    @field:Json(name = "CLASS_NO")
    val cLASSNO: String?,
    @field:Json(name = "COLOR_CLASS1")
    val cOLORCLASS1: String?,
    @field:Json(name = "COLOR_CLASS2")
    val cOLORCLASS2: Any?,
    @field:Json(name = "DRUG_SHAPE")
    val dRUGSHAPE: String?,
    @field:Json(name = "EDI_CODE")
    val eDICODE: Any?,
    @field:Json(name = "ENTP_NAME")
    val eNTPNAME: String?,
    @field:Json(name = "ENTP_SEQ")
    val eNTPSEQ: String?,
    @field:Json(name = "ETC_OTC_NAME")
    val eTCOTCNAME: String?,
    @field:Json(name = "FORM_CODE_NAME")
    val fORMCODENAME: String?,
    @field:Json(name = "IMG_REGIST_TS")
    val iMGREGISTTS: String?,
    @field:Json(name = "ITEM_ENG_NAME")
    val iTEMENGNAME: Any?,
    @field:Json(name = "ITEM_IMAGE")
    val iTEMIMAGE: String?,
    @field:Json(name = "ITEM_NAME")
    val iTEMNAME: String?,
    @field:Json(name = "ITEM_PERMIT_DATE")
    val iTEMPERMITDATE: String?,
    @field:Json(name = "ITEM_SEQ")
    val iTEMSEQ: String?,
    @field:Json(name = "LENG_LONG")
    val lENGLONG: String?,
    @field:Json(name = "LENG_SHORT")
    val lENGSHORT: String?,
    @field:Json(name = "LINE_BACK")
    val lINEBACK: Any?,
    @field:Json(name = "LINE_FRONT")
    val lINEFRONT: Any?,
    @field:Json(name = "MARK_CODE_BACK")
    val mARKCODEBACK: Any?,
    @field:Json(name = "MARK_CODE_BACK_ANAL")
    val mARKCODEBACKANAL: String?,
    @field:Json(name = "MARK_CODE_BACK_IMG")
    val mARKCODEBACKIMG: String?,
    @field:Json(name = "MARK_CODE_FRONT")
    val mARKCODEFRONT: Any?,
    @field:Json(name = "MARK_CODE_FRONT_ANAL")
    val mARKCODEFRONTANAL: String?,
    @field:Json(name = "MARK_CODE_FRONT_IMG")
    val mARKCODEFRONTIMG: String?,
    @field:Json(name = "PRINT_BACK")
    val pRINTBACK: Any?,
    @field:Json(name = "PRINT_FRONT")
    val pRINTFRONT: String?,
    @field:Json(name = "THICK")
    val tHICK: String?
)