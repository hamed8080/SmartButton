package ir.amozkade.advancedAsisstiveTouche.mvvm.permissions.models

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize

@JsonIgnoreProperties(ignoreUnknown = true)
@Parcelize
class Permission @JsonCreator constructor(
        @JsonProperty("id") val id: Int,
        @JsonProperty("text") val text: String?,
        @JsonProperty("textFA") val textFA: String?,
        @JsonProperty("title") val title: String?,
        @JsonProperty("titleFA") val titleFA: String?) : Parcelable {
}