package ir.amozkade.advancedAsisstiveTouche.mvvm.update

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize

@Parcelize
class Change @JsonCreator constructor(@JsonProperty("id")val id:Int,
                                      @JsonProperty("description") val description:String
) :Parcelable