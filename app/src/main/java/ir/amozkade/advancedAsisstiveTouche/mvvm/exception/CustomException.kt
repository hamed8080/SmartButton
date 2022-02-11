package ir.amozkade.advancedAsisstiveTouche.mvvm.exception

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
@Entity
data class CustomException @JsonCreator constructor(@JsonProperty("id") @PrimaryKey(autoGenerate = true) var id: Int = 0,
                                                    @JsonProperty("message") var message: String? = null,
                                                    @JsonProperty("traces") var traces: String? = null,
                                                    @JsonFormat(locale = "en", shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                                                    @JsonProperty("date") var date: Date? = null,
                                                    @JsonProperty("device") var device: String? = null,
                                                    @JsonProperty("api") var api: Int? = null,
                                                    @JsonProperty("appVersion") var appVersion: String? = null,
                                                    @JsonProperty("token") var token: String? = null
) : Parcelable