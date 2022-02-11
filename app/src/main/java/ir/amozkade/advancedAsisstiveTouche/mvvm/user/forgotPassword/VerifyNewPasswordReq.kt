package ir.amozkade.advancedAsisstiveTouche.mvvm.user.forgotPassword

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize

@JsonIgnoreProperties(ignoreUnknown = true)
@Parcelize
data class VerifyNewPasswordReq @JsonCreator constructor(@JsonProperty("email") var email: String,
                                                         @JsonProperty("newPassword") var newPassword: String,
                                                         @JsonProperty("code") var code: Int) : Parcelable
