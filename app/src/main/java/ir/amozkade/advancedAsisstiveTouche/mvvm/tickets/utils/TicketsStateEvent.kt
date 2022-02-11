package ir.amozkade.advancedAsisstiveTouche.mvvm.tickets.utils


sealed class TicketsStateEvent {
    object GetAllTickets : TicketsStateEvent()
    object StartNewTicket : TicketsStateEvent()
}