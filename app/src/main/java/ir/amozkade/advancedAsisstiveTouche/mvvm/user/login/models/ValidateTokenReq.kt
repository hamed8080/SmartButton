package ir.amozkade.advancedAsisstiveTouche.mvvm.user.login.models

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize


@Parcelize
class ValidateTokenReq @JsonCreator constructor(@JsonProperty("tokenProvider") var tokenProvider: String,
                                                @JsonProperty("ios") val ios: Boolean = false,
                                                @JsonProperty("accessToken") var accessToken: String):Parcelable