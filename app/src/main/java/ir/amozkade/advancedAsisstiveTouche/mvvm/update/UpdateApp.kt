package ir.amozkade.advancedAsisstiveTouche.mvvm.update

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
class UpdateApp @JsonCreator constructor (@JsonProperty("id") val id:Int,
                                          @JsonProperty("versionCode") val versionCode:String,
                                          @JsonProperty("url") val url:String,
                                          @JsonProperty("os") val os:String,
                                          @JsonProperty("changes") val changes:List<Change>,
                                          @JsonProperty("minVer") val minVer:String,
                                          @JsonProperty("updateDate") val updateDate:Date
) : Parcelable