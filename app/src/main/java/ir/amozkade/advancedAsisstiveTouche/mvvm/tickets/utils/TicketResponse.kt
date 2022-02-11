package ir.amozkade.advancedAsisstiveTouche.mvvm.tickets.utils

import ir.amozkade.advancedAsisstiveTouche.mvvm.tickets.model.Ticket

sealed class TicketResponse{
    data class TicketAdded(val ticket: Ticket): TicketResponse()
    data class AllTickets(val tickets: List<Ticket>): TicketResponse()
}
