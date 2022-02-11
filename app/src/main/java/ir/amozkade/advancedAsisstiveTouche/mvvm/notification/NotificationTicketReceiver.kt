package ir.amozkade.advancedAsisstiveTouche.mvvm.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import ir.amozkade.advancedAsisstiveTouche.mvvm.tickets.conversation.TicketConversationActivity
import ir.amozkade.advancedAsisstiveTouche.mvvm.tickets.conversation.model.TicketMessage
import java.lang.ref.WeakReference

class NotificationTicketReceiver(private val activity:WeakReference<TicketConversationActivity>) : BroadcastReceiver() {
    companion object{
        const val NOTIFICATION_TICKET_ACTION = "NOTIFICATION_TICKET_ACTION"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val ticket = intent.getParcelableExtra<TicketMessage>("TicketMessageNotification") ?: return
        activity.get()?.onNewTicketMessageArrived(ticket)
    }

}