package ir.amozkade.advancedAsisstiveTouche.mvvm.tickets.model

import android.content.Context
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import ir.amozkade.advancedAsisstiveTouche.R
import kotlinx.android.parcel.Parcelize
import java.util.*

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@Parcelize
class Ticket @JsonCreator constructor(@PrimaryKey @JsonProperty("id") val id: Int,
                                      @JsonProperty("title") val title: String,
                                      @JsonProperty("userId") val userId: String,
                                      @JsonProperty("token") val token: String?,
                                      @JsonProperty("api") val api: Int?,
                                      @JsonProperty("device") val device: String?,
                                      @JsonProperty("dataLog") val dataLog: String?,
                                      @JsonProperty("appVersion") val appVersion: String?,
                                      @JsonProperty("startDate") val startDate: Date?,
                                      @JsonProperty("ticketStatus") val ticketStatus: TicketStatus
) : Parcelable {
    fun ticketStatusString(context: Context): String {
        return when (ticketStatus) {
            TicketStatus.IN_PROGRESS -> context.getString(R.string.in_progress)
            TicketStatus.SOLVED -> context.getString(R.string.solved)
        }
    }
}