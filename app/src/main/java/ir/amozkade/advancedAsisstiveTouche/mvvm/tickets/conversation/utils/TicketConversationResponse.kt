package ir.amozkade.advancedAsisstiveTouche.mvvm.tickets.conversation.utils

import ir.amozkade.advancedAsisstiveTouche.mvvm.tickets.conversation.model.TicketMessage

sealed class TicketConversationResponse{
    data class TicketMessages(val tickets:List<TicketMessage>): TicketConversationResponse()
    data class AddedTicket(val ticket: TicketMessage): TicketConversationResponse()
}
