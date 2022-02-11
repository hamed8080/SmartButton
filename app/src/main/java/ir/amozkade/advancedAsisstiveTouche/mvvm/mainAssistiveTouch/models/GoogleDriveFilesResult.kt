package ir.amozkade.advancedAsisstiveTouche.mvvm.mainAssistiveTouch.models

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GoogleDriveFilesResult @JsonCreator constructor(
        @JsonProperty("files") val files: List<DriveFile>,
        @JsonProperty("incompleteSearch") val incompleteSearch: Boolean,
        @JsonProperty("kind") val kind: String
) : Parcelable