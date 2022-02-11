package ir.amozkade.advancedAsisstiveTouche.mvvm.user.login.models

import android.os.Parcelable
import androidx.databinding.BaseObservable
import com.fasterxml.jackson.annotation.*
import kotlinx.android.parcel.Parcelize
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown =  true)
@Parcelize
class User @JsonCreator constructor(@JsonProperty("id") var id: String? = null,
                                    @JsonProperty("userName") var userName: String? = null,
                                    @JsonProperty("firstName") var firstName: String? = null,
                                    @JsonProperty("lastName") var lastName: String? = null,
                                    @JsonProperty("normalizedUserName") var normalizedUserName: String? = null,
                                    @JsonProperty("email") var email: String? = null,
                                    @JsonProperty("normalizedEmail") var normalizedEmail: String? = null,
                                    @JsonProperty("emailConfirmed") var emailConfirmed: String? = null,
                                    @JsonProperty("passwordHash") var passwordHash: String? = null,
                                    @JsonProperty("securityStamp") var securityStamp: String? = null,
                                    @JsonProperty("concurrencyStamp") var concurrencyStamp: String? = null,
                                    @JsonProperty("phoneNumber") var phoneNumber: String? = null,
                                    @JsonProperty("phoneNumberConfirmed") var phoneNumberConfirmed: String? = null,
                                    @JsonProperty("twoFactorEnabled") var twoFactorEnabled: String? = null,
                                    @JsonProperty("lockoutEnd") var lockoutEnd: String? = null,
                                    @JsonProperty("lockoutEnabled") var lockoutEnabled: String? = null,
                                    @JsonProperty("accessFailedCount") var accessFailedCount: String? = null,
                                    @JsonProperty("image") var image: String? = null,
                                    @JsonProperty("address") var address: String? = null,
                                    @JsonProperty("cityId") var cityId: Int? = null,
                                    @JsonProperty("lastLogin") var lastLogin: Date? = null) : BaseObservable(), Parcelable