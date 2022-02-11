package ir.amozkade.advancedAsisstiveTouche.mvvm.user.login.models

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import ir.amozkade.advancedAsisstiveTouche.mvvm.user.profile.Profile
import kotlinx.android.parcel.Parcelize

@Parcelize
class ProfileWithJwt @JsonCreator constructor(
        @JsonProperty("jwt") var jwt: String,
        @JsonProperty("profile") var profile: Profile
) : Parcelable