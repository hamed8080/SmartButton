package ir.amozkade.advancedAsisstiveTouche.mvvm.tickets.conversation.utils


sealed class TicketConversationStateEvent{
    data class GetAllTicketMessages(val ticketId:Int):TicketConversationStateEvent()
    data class SendMessage(val ticketId: Int):TicketConversationStateEvent()
}