package ir.amozkade.advancedAsisstiveTouche.mvvm.user.login.models

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true)
class Payload @JsonCreator constructor(@JsonProperty("nbf") var nbf: Int? = null,
                                       @JsonProperty("exp") var exp: Int? = null,
                                       @JsonProperty("iat") var iat: Int? = null,
                                       @JsonProperty("iss") var iss: String? = null,
                                       @JsonProperty("aud") var aud: String? = null,
                                       @JsonProperty("userId") var userId: String? = null) : Parcelable