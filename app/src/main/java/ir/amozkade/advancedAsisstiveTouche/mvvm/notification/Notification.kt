package ir.amozkade.advancedAsisstiveTouche.mvvm.notification

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
data class Notification @JsonCreator constructor(@PrimaryKey(autoGenerate = true) @JsonProperty("id") val id: Int,
                                                 @JsonProperty("title") val title: String,
                                                 @JsonProperty("message") val message: String,
                                                 @JsonProperty("dateSend") val dateSend: Date,
                                                 @JsonProperty("userId") val userId: String?,
                                                 @JsonProperty("seen") val seen: Boolean?
) : Parcelable {
    val isGeneral: Boolean
        get() {
            return userId == null
        }
}