package ir.amozkade.advancedAsisstiveTouche.mvvm.tickets.conversation

import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import dagger.hilt.android.AndroidEntryPoint
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.databinding.ActivityTicketConversationBinding
import ir.amozkade.advancedAsisstiveTouche.helper.api.DataState
import ir.amozkade.advancedAsisstiveTouche.mvvm.BaseActivity
import ir.amozkade.advancedAsisstiveTouche.mvvm.notification.NotificationTicketReceiver
import ir.amozkade.advancedAsisstiveTouche.mvvm.tickets.model.Ticket
import ir.amozkade.advancedAsisstiveTouche.mvvm.tickets.model.TicketStatus
import ir.amozkade.advancedAsisstiveTouche.mvvm.tickets.conversation.model.TicketMessage
import ir.amozkade.advancedAsisstiveTouche.mvvm.tickets.conversation.utils.TicketConversationResponse
import ir.amozkade.advancedAsisstiveTouche.mvvm.tickets.conversation.utils.TicketConversationStateEvent
import java.lang.Exception
import java.lang.ref.WeakReference

@AndroidEntryPoint
class TicketConversationActivity : BaseActivity() {

    private lateinit var mBinding: ActivityTicketConversationBinding
    private val viewModel: TicketConversationViewModel by viewModels()
    private val br = NotificationTicketReceiver(WeakReference(this))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_ticket_conversation)
        showWarningAlert = false
        setupUI()
        setupObservers()
    }

    private fun setupUI() {

        val filter = IntentFilter(NotificationTicketReceiver.NOTIFICATION_TICKET_ACTION)
        registerReceiver(br , filter)
        addLoadingsToContainer(mBinding.root as ConstraintLayout)
        intent.getParcelableExtra<Ticket>("ticket")?.let { ticket->
            mBinding.actionBar.setActionBarTitle(ticket.title)
            disableSendContainerIfSolved(ticket)
            viewModel.setState(TicketConversationStateEvent.GetAllTicketMessages(ticketId = ticket.id))
            mBinding.btnSend.setOnClickListener { viewModel.setState(TicketConversationStateEvent.SendMessage(ticketId = ticket.id)) }
        }
        mBinding.vm = viewModel
    }

    private fun setupObservers(){
        viewModel.exceptionObserver.observe(this) {
            manageDataState(DataState.Error(it as Exception))
        }
        viewModel.response.observe(this) { dataState ->
            if (dataState is DataState.Success) {
                if (dataState.data is TicketConversationResponse.TicketMessages) {
                    setupRecyclerView(dataState.data.tickets)
                } else if (dataState.data is TicketConversationResponse.AddedTicket) {
                    successAddMessage(dataState.data.ticket)
                }
            }
            manageDataState(dataState)
        }
    }

    private fun disableSendContainerIfSolved(ticket: Ticket) {
        if (ticket.ticketStatus == TicketStatus.SOLVED) {
            mBinding.sendContainer.alpha = .6f
            mBinding.tfMessage.editText?.isEnabled = false
            mBinding.tfMessage.editText?.isClickable = false
            mBinding.btnSend.isEnabled = false
        }
    }

    private fun setupRecyclerView(messages: List<TicketMessage>) {
        mBinding.rcv.adapter = TicketConversationAdapter(ArrayList(messages), this)
        (mBinding.rcv.adapter as TicketConversationAdapter).notifyDataSetChanged()
        mBinding.rcv.scrollToPosition(messages.count() - 1)
    }

    private fun successAddMessage(ticketMessage: TicketMessage) {
        mBinding.tfMessage.editText?.text = null
        (mBinding.rcv.adapter as TicketConversationAdapter).addNewItemAtBottom(ticketMessage)
        mBinding.rcv.smoothScrollToPosition((mBinding.rcv.adapter as TicketConversationAdapter).itemCount + 1)
    }

    fun onNewTicketMessageArrived(ticketMessage: TicketMessage) {
        mBinding.rcv.run {
            (adapter as TicketConversationAdapter).addNewItemAtBottom(ticketMessage)
            smoothScrollToPosition((mBinding.rcv.adapter as TicketConversationAdapter).itemCount + 1)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(br)
    }
}
