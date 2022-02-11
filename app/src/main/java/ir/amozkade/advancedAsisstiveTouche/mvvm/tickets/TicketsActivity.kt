package ir.amozkade.advancedAsisstiveTouche.mvvm.tickets

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.databinding.ActivityTicketsBinding
import ir.amozkade.advancedAsisstiveTouche.helper.api.DataState
import ir.amozkade.advancedAsisstiveTouche.mvvm.BaseActivity
import ir.amozkade.advancedAsisstiveTouche.mvvm.tickets.conversation.TicketConversationActivity
import ir.amozkade.advancedAsisstiveTouche.mvvm.tickets.model.Ticket
import ir.amozkade.advancedAsisstiveTouche.mvvm.tickets.utils.TicketResponse
import ir.amozkade.advancedAsisstiveTouche.mvvm.tickets.utils.TicketsStateEvent
import ir.amozkade.advancedAsisstiveTouche.mvvm.user.login.LoginActivity
import ir.mobitrain.applicationcore.alertDialog.AlertDialogDelegate
import ir.mobitrain.applicationcore.alertDialog.CustomAlertDialog
import ir.mobitrain.applicationcore.api.JWT
import java.lang.Exception

@AndroidEntryPoint
class TicketsActivity : BaseActivity() {

    private lateinit var mBinding: ActivityTicketsBinding
    private val viewModel: TicketsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_tickets)
        setupUI()
        setupObservers()
        viewModel.setState(TicketsStateEvent.GetAllTickets)
    }

    private fun setupUI() {
        addLoadingsToContainer(mBinding.root as ConstraintLayout)
        mBinding.vm = viewModel
        mBinding.btnStartNewTicket.setOnClickListener {
            if (JWT.instance.computedJWT == null) {
                openLoginAlert()
                return@setOnClickListener
            }
            sendContainer(View.VISIBLE)
        }
        mBinding.btnSendStartTicket.setOnClickListener { viewModel.setState(TicketsStateEvent.StartNewTicket) }
    }

    private fun setupObservers() {
        viewModel.exceptionObserver.observe(this, Observer {
            manageDataState(DataState.Error(it as Exception))
        })
        viewModel.response.observe(this, Observer { dataState ->
            if(dataState is DataState.Success){
                if(dataState.data is TicketResponse.AllTickets){
                    setupRecyclerView(dataState.data.tickets)
                }else if (dataState.data is TicketResponse.TicketAdded){
                    openTicketMessageConversation(dataState.data.ticket)
                }
            }
            manageDataState(dataState)
        })
    }

    private fun setupRecyclerView(tickets: List<Ticket>) {
        sendContainer(View.GONE)
        hideEmptyView(tickets.count() > 0)
        mBinding.rcv.adapter = TicketAdapter(tickets, this)
        (mBinding.rcv.adapter as TicketAdapter).notifyDataSetChanged()
    }

    private fun openLoginAlert() {
        CustomAlertDialog.showDialog(this,
                submitTitle = getString(R.string.login),
                title = getString(R.string.login),
                message = getString(R.string.first_login_to_system),
                imageId = R.drawable.img_login,
                delegate = object : AlertDialogDelegate {
                    override fun alertCompletion(type: AlertDialogDelegate.AlertTapType, extraButton: MaterialButton?) {
                        if (type == AlertDialogDelegate.AlertTapType.Submit) {
                            startActivity(Intent(cto, LoginActivity::class.java))
                        }
                    }
                })
    }

    private fun sendContainer(visibility: Int) {
        mBinding.startNewTicketContainer.visibility = visibility
        val animation = AnimationUtils.loadAnimation(this, if (visibility == View.VISIBLE) R.anim.alpha_show else R.anim.alpha_hide)
        animation.duration = 200
        mBinding.startNewTicketContainer.animation = animation
        if (visibility == View.VISIBLE) {
            mBinding.btnStartNewTicket.visibility = View.GONE
            mBinding.actionBar.btnBackSetOnClickListener {
                sendContainer(View.GONE)
                mBinding.btnStartNewTicket.visibility = View.VISIBLE
            }
        } else {
            mBinding.btnStartNewTicket.visibility = View.VISIBLE
            mBinding.actionBar.btnBackSetOnClickListener {
                finish()
            }
        }
    }

    private fun openTicketMessageConversation(ticket: Ticket) {
        val intent = Intent(cto, TicketConversationActivity::class.java)
        intent.putExtra("ticket", ticket)
        startActivity(intent)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && mBinding.startNewTicketContainer.visibility == View.VISIBLE) {
            sendContainer(View.GONE)
            return false
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun hideEmptyView(hide:Boolean){
        mBinding.imgEmptyTicket.visibility = if(hide) View.GONE else View.VISIBLE
        mBinding.txtEmptyTicket.visibility = if(hide) View.GONE else View.VISIBLE
    }
}
