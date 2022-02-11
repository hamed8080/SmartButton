package ir.amozkade.advancedAsisstiveTouche.mvvm.tickets.conversation.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize
import java.util.*

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@Parcelize
class TicketMessage @JsonCreator constructor(@PrimaryKey @JsonProperty("id") val id: Int,
                                             @JsonProperty("ticketId") val ticketId: Int,
                                             @JsonProperty("message") val message: String,
                                             @JsonProperty("sendDate") val sendDate: Date?,
                                             @JsonProperty("fromServer") val fromServer: Boolean

) : Parcelable