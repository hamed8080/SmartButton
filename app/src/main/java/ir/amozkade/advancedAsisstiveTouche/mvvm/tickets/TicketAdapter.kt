package ir.amozkade.advancedAsisstiveTouche.mvvm.tickets

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.databinding.*
import ir.amozkade.advancedAsisstiveTouche.mvvm.tickets.conversation.TicketConversationActivity
import ir.amozkade.advancedAsisstiveTouche.mvvm.tickets.model.Ticket

class TicketAdapter(private val tickets: List<Ticket>, val context: Context) : RecyclerView.Adapter<TicketAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = DataBindingUtil.inflate<RowTicketBinding>(inflater, R.layout.row_ticket, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return tickets.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ticket = tickets[holder.bindingAdapterPosition]
        holder.bind(ticket)
        holder.row.root.setOnClickListener {
            val intent = Intent(context , TicketConversationActivity::class.java)
            intent.putExtra("ticket" , ticket)
            context.startActivity(intent)
        }
    }

    open class ViewHolder(val row: RowTicketBinding) : RecyclerView.ViewHolder(row.root) {
        fun bind(ticket: Ticket) {
            row.ticket = ticket
        }
    }
}
