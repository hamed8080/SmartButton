package ir.amozkade.advancedAsisstiveTouche.mvvm.tickets.di

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ir.amozkade.advancedAsisstiveTouche.mvvm.tickets.model.Ticket
import ir.amozkade.advancedAsisstiveTouche.mvvm.tickets.conversation.model.TicketMessage

@Dao
interface TicketDao {

    @Query("SELECT * FROM Ticket")
    suspend fun getAllTickets(): List<Ticket>

    @Query("DELETE FROM Ticket")
    suspend fun deleteAllTickets()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTicket(ticket: Ticket)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTickets(tickets: List<Ticket>)

    //Ticket Conversation
    @Query("SELECT * FROM TicketMessage WHERE id= :ticketId")
    suspend fun getAllTicketMessages(ticketId:Int): List<TicketMessage>

    @Query("DELETE FROM TicketMessage WHERE id= :ticketId")
    suspend fun deleteAllTicketMessages(ticketId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTicketMessage(ticketMessage: TicketMessage)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTicketMessages(ticketMessages: List<TicketMessage>)
}