package ir.amozkade.advancedAsisstiveTouche.mvvm.tickets.di

import ir.amozkade.advancedAsisstiveTouche.mvvm.tickets.model.Ticket
import ir.amozkade.advancedAsisstiveTouche.mvvm.tickets.conversation.model.TicketMessage
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface TicketRetrofit {


    @POST("SmartButton/Ticket")
    suspend fun startNewTicket(@Body ticket: Ticket): Ticket

    @GET("SmartButton/Ticket")
    suspend fun getAllTickets(@Query("userId") userId: String): List<Ticket>

    @POST("SmartButton/Ticket/AddUserTicketMessage")
    suspend fun addUserTicketMessage(@Body ticket: TicketMessage): TicketMessage

    @GET("SmartButton/Ticket/AllMessages")
    suspend fun getAllTicketMessages(@Query("ticketId") ticketId :Int): List<TicketMessage>

}