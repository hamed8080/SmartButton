package ir.amozkade.advancedAsisstiveTouche.mvvm.user.login.models

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize

@JsonIgnoreProperties(ignoreUnknown = true)
@Parcelize
class GoogleAccessTokenResponse @JsonCreator constructor(
        @JsonProperty("access_token") var access_token: String,
        @JsonProperty("expires_in") var expires_in: Int,
        @JsonProperty("scope") var scope: String,
        @JsonProperty("token_type") var token_type: String,
        @JsonProperty("id_token") var id_token: String,
        @JsonProperty("refresh_token") var refresh_token: String?

) : Parcelable