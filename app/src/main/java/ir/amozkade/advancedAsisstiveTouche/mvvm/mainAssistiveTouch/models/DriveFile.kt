package ir.amozkade.advancedAsisstiveTouche.mvvm.mainAssistiveTouch.models

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DriveFile @JsonCreator constructor(
        @JsonProperty("id") val id: String,
        @JsonProperty("kind") val kind: String,
        @JsonProperty("mimeType") val mimeType: String,
        @JsonProperty("name") val name: String,
        @JsonProperty("size") val size: Long?
) : Parcelable