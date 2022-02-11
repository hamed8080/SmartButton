package ir.amozkade.advancedAsisstiveTouche.mvvm.tickets.model

import androidx.room.TypeConverter
import com.fasterxml.jackson.annotation.JsonProperty

enum class TicketStatus(val value: Int) {
    @JsonProperty("0")
    IN_PROGRESS(0),
    @JsonProperty("1")
    SOLVED(1)
}

class TicketStatusConverter{

    @TypeConverter
    fun fromIntToTicketStatus(int: Int): TicketStatus {
        return when(int){
            0-> TicketStatus.IN_PROGRESS
            1-> TicketStatus.SOLVED
            else -> TicketStatus.IN_PROGRESS
        }
    }

    @TypeConverter
    fun fromTicketStatusToInt(ticketStatus: TicketStatus):Int{
        return  ticketStatus.ordinal
    }
}