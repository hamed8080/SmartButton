package ir.amozkade.advancedAsisstiveTouche.mvvm.tickets.conversation

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.databinding.RowIncomingTicketMessageBinding
import ir.amozkade.advancedAsisstiveTouche.databinding.RowOutgoingTicketMessageBinding
import ir.amozkade.advancedAsisstiveTouche.mvvm.tickets.conversation.model.TicketMessage

class TicketConversationAdapter(private val messages: ArrayList<TicketMessage>, val context: Context) : RecyclerView.Adapter<TicketConversationAdapter.ViewHolder>() {

    enum class ViewTypes {
        INCOMING, OUTGOING
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        return if (viewType == ViewTypes.INCOMING.ordinal) {
            val incomingBinding = DataBindingUtil.inflate<RowIncomingTicketMessageBinding>(inflater, R.layout.row_incoming_ticket_message, parent, false)
            ViewHolder(incomingBinding)
        } else {
            val outgoingBinding = DataBindingUtil.inflate<RowOutgoingTicketMessageBinding>(inflater, R.layout.row_outgoing_ticket_message, parent, false)
            ViewHolder(outgoingBinding)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].fromServer) ViewTypes.INCOMING.ordinal
        else ViewTypes.OUTGOING.ordinal
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ticket = messages[holder.bindingAdapterPosition]
        if (ticket.fromServer) {
            holder.bindIncoming(ticket)
        } else {
            holder.bindOutgoing(ticket)
        }
    }

    fun addNewItemAtBottom(ticketMessage: TicketMessage) {
        messages.add(ticketMessage)
        notifyItemInserted(messages.count())
    }

    open class ViewHolder(val row: ViewDataBinding) : RecyclerView.ViewHolder(row.root) {

        fun bindIncoming(ticketMessage: TicketMessage) {
            (row as RowIncomingTicketMessageBinding).ticketMessage = ticketMessage
        }

        fun bindOutgoing(ticketMessage: TicketMessage) {
            (row as RowOutgoingTicketMessageBinding).ticketMessage = ticketMessage
        }
    }
}
