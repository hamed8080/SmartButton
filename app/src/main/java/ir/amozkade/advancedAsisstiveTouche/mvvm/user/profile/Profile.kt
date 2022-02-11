package ir.amozkade.advancedAsisstiveTouche.mvvm.user.profile

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize
import java.util.*

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Parcelize
class Profile @JsonCreator constructor(
        @PrimaryKey(autoGenerate = true) @JsonProperty("id") val id: Int = 0,
        @JsonProperty("email") var email: String? = null,
        @JsonProperty("phone") var phone: String? = null,
        @JsonProperty("lastLogin") var lastLogin: Date? = null,
        @JsonProperty("img") var img: String? = null,
        @JsonProperty("firstName") var firstName: String? = null,
        @JsonProperty("lastName") var lastName: String? = null
) : Parcelable {

//    companion object{
//        fun profileInstance(): Profile?{
//            val jsonString = getValue(Preference.Profile)
//            return try {
//                ObjectMapper().readValue(jsonString , Profile::class.java)
//            }catch(e:Exception){
//                null
//            }
//        }
//    }
}
