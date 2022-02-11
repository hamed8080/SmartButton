package ir.amozkade.advancedAsisstiveTouche.mvvm.user.profile.editProfile

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize

@JsonInclude(JsonInclude.Include.NON_NULL)
@Parcelize
class EditUserDetailReq @JsonCreator constructor(@JsonProperty("phone") var phone: String,
                                                 @JsonProperty("userId") var userId: String,
                                                 @JsonProperty("firstName") var firstName: String,
                                                 @JsonProperty("lastName") var lastName: String

) : Parcelable