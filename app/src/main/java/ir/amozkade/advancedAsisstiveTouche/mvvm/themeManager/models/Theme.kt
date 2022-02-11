package ir.amozkade.advancedAsisstiveTouche.mvvm.themeManager.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize

@JsonIgnoreProperties(ignoreUnknown = true)
@Parcelize
@Entity
data class Theme @JsonCreator constructor(@PrimaryKey @JsonProperty("id") val id:Int,
                                          @JsonProperty("title") val title: String,
                                          @JsonProperty("url") val url: String,
                                          @JsonProperty("isFree") val isFree:Boolean,
                                          @JsonProperty("thumbnailImageAddress") val thumbnailImageAddress: String,
                                          @JsonProperty("themeType") val themeType: String,
                                          @JsonProperty("downloadCount") val downloadCount:Int) : Parcelable