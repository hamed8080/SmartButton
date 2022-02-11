package ir.amozkade.advancedAsisstiveTouche.mvvm.user.profile.resetPassword

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize

@JsonInclude(JsonInclude.Include.NON_NULL)
@Parcelize
data class PasswordResetReq @JsonCreator constructor(@JsonProperty("userId") var userId: String,
                                                     @JsonProperty("currentPassword") var currentPassword: String,
                                                     @JsonProperty("newPassword") var newPassword: String) : Parcelable