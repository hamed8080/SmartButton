package ir.amozkade.advancedAsisstiveTouche.mvvm.tickets.conversation

import android.content.Context
import android.text.Editable
import javax.inject.Inject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.helper.InAppException
import ir.amozkade.advancedAsisstiveTouche.helper.api.DataState
import ir.amozkade.advancedAsisstiveTouche.mvvm.tickets.TicketRepository
import ir.amozkade.advancedAsisstiveTouche.mvvm.tickets.conversation.model.TicketMessage
import ir.amozkade.advancedAsisstiveTouche.mvvm.tickets.conversation.utils.TicketConversationResponse
import ir.amozkade.advancedAsisstiveTouche.mvvm.tickets.conversation.utils.TicketConversationStateEvent
import ir.mobitrain.applicationcore.helper.CTextWatcher
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect

@HiltViewModel
class TicketConversationViewModel @Inject constructor(private val ticketRepository: TicketRepository,
                                                      @ApplicationContext private val context: Context,
                                                      val exceptionObserver: MutableLiveData<Throwable>) : ViewModel() {

    private val _response: MutableLiveData<DataState<TicketConversationResponse>> = MutableLiveData()
    val response: LiveData<DataState<TicketConversationResponse>> = _response

    private var message: String = ""
    var messageTextWatcher: CTextWatcher = object : CTextWatcher() {
        override fun afterTextChanged(s: Editable) {
            super.afterTextChanged(s)
            message = s.toString().trim()
        }
    }

    fun setState(ticketConversationStateEvent: TicketConversationStateEvent) {
        val handler = CoroutineExceptionHandler { _, exception ->
            exceptionObserver.postValue(exception)
        }
        viewModelScope.launch(handler) {
            supervisorScope {

                when (ticketConversationStateEvent) {
                    is TicketConversationStateEvent.GetAllTicketMessages -> {
                        ticketRepository.getAllTicketMessages(ticketConversationStateEvent.ticketId).collect {
                            _response.postValue(it)
                        }
                    }
                    is TicketConversationStateEvent.SendMessage -> {
                        if (message.isEmpty()) {
                            _response.postValue(DataState.Error(InAppException(context.getString(R.string.ticket), context.getString(R.string.empty_ticket_message) , imageId = null)))
                        }else{
                            val ticketMessage = TicketMessage(0, ticketConversationStateEvent.ticketId, message, null, false)
                            ticketRepository.addNewMessage(ticketMessage).collect {
                                _response.postValue(it)
                            }
                        }
                    }
                }
            }
        }
    }
}