package ir.amozkade.advancedAsisstiveTouche.mvvm.notification

import javax.inject.Inject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.amozkade.advancedAsisstiveTouche.AppDatabase
import ir.amozkade.advancedAsisstiveTouche.helper.api.DataState
import ir.amozkade.advancedAsisstiveTouche.mvvm.notification.utils.NotificationResponse
import ir.amozkade.advancedAsisstiveTouche.mvvm.notification.utils.NotificationStateEvent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.supervisorScope

@HiltViewModel
class NotificationViewModel @Inject constructor(private val repository: NotificationRepository, val exceptionObserver: MutableLiveData<Throwable>) : ViewModel() {

    private var model = NotificationModel()

    private val _response: MutableLiveData<DataState<NotificationResponse>> = MutableLiveData()
    val response: LiveData<DataState<NotificationResponse>> = _response

    fun getModel(): NotificationModel {
        return model
    }

    fun setState(event: NotificationStateEvent) {
        val handler = CoroutineExceptionHandler { _, exception ->
            exceptionObserver.postValue(exception)
        }
        viewModelScope.launch(handler) {
            supervisorScope {

                when (event) {
                    is NotificationStateEvent.GetAll -> {
                        repository.getAllNotifications().collect {
                            _response.postValue(it)
                        }
                    }
                    is NotificationStateEvent.ViewedAll -> {
                        repository.viewedAll().collect {
                            _response.postValue(it)
                        }
                    }
                }
            }
        }
    }
}
