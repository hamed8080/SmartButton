package ir.amozkade.advancedAsisstiveTouche.mvvm.themeManager.models

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize

@JsonIgnoreProperties(ignoreUnknown = true)
@Parcelize
class Section<T : Parcelable> @JsonCreator constructor(@JsonProperty("title") val title: String,
                                                       @JsonProperty("faTitle") val faTitle: String,
                                                       @JsonProperty("list") val list: List<T>,
                                                       @JsonProperty("displayType") val displayType: DisplayType) : Parcelable {
}