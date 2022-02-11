package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.review.models

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true)
data class Synonym @JsonCreator constructor(@JsonProperty("word") var synonym: String?
) : Parcelable