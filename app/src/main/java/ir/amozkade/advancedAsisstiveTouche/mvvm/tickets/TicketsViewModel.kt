package ir.amozkade.advancedAsisstiveTouche.mvvm.tickets

import android.content.Context
import android.text.Editable
import javax.inject.Inject
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.helper.InAppException
import ir.amozkade.advancedAsisstiveTouche.helper.api.DataState
import ir.amozkade.advancedAsisstiveTouche.mvvm.tickets.utils.TicketResponse
import ir.amozkade.advancedAsisstiveTouche.mvvm.tickets.utils.TicketsStateEvent
import ir.mobitrain.applicationcore.helper.CTextWatcher
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.collect

@HiltViewModel
class TicketsViewModel @Inject constructor(
    private val ticketRepository: TicketRepository,
    @ApplicationContext private val context: Context,
    val exceptionObserver: MutableLiveData<Throwable>
) : ViewModel() {

    var title: String = ""

    private val _response: MutableLiveData<DataState<TicketResponse>> = MutableLiveData()
    val response: LiveData<DataState<TicketResponse>> = _response

    var titleTextWatcher: CTextWatcher = object : CTextWatcher() {
        override fun afterTextChanged(s: Editable) {
            super.afterTextChanged(s)
            title = s.toString().trim()
        }
    }

    fun setState(event: TicketsStateEvent) {
        val handler = CoroutineExceptionHandler { _, exception ->
            exceptionObserver.postValue(exception)
        }
        viewModelScope.launch(IO + handler) {
            supervisorScope {

                when (event) {
                    is TicketsStateEvent.GetAllTickets -> {
                        ticketRepository.getAllTickets().collect {
                            withContext(Main) {
                                _response.postValue(it)
                            }
                        }
                    }
                    is TicketsStateEvent.StartNewTicket -> {
                        if (title.isEmpty()) {
                            withContext(Main) {
                                _response.postValue(DataState.Error(InAppException(context.getString(R.string.ticket), context.getString(R.string.empty_ticket_message), imageId = null)))
                            }

                        } else {
                            ticketRepository.startNewTicket(title).collect {
                                withContext(Main) {
                                    _response.postValue(it)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}