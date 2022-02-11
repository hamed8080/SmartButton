package ir.amozkade.advancedAsisstiveTouche.mvvm.notification

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.databinding.*

class NotificationAdapter(private val notifications: ArrayList<Notification>, val context: Context) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = DataBindingUtil.inflate<RowNotificationBinding>(inflater, R.layout.row_notification, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return notifications.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notif = notifications[holder.bindingAdapterPosition]
        holder.bind(notif)
        holder.row.cv.setCardBackgroundColor(ContextCompat.getColor(context, if (notif.isGeneral) R.color.green else R.color.white_darker_3X))
        holder.row.txtMessage.setTextColor(ContextCompat.getColor(context, if (notif.isGeneral) R.color.white_darker_3X else R.color.black))
    }

    fun clear() {
        notifications.clear()
        notifyDataSetChanged()
    }

    open class ViewHolder(val row: RowNotificationBinding) : RecyclerView.ViewHolder(row.root) {
        fun bind(notification: Notification) {
            row.notification = notification
        }
    }
}
