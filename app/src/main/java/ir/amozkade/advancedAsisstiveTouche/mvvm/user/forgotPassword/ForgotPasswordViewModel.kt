package ir.amozkade.advancedAsisstiveTouche.mvvm.user.forgotPassword

import javax.inject.Inject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.amozkade.advancedAsisstiveTouche.helper.api.DataState
import ir.amozkade.advancedAsisstiveTouche.mvvm.user.UserRepository
import ir.amozkade.advancedAsisstiveTouche.mvvm.user.forgotPassword.utils.ForgotPasswordResponse
import ir.amozkade.advancedAsisstiveTouche.mvvm.user.forgotPassword.utils.ForgotPasswordStateEvent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
        private val userRepository: UserRepository,
        val exceptionObserver: MutableLiveData<Throwable>
) : ViewModel() {

    private val _response: MutableLiveData<DataState<ForgotPasswordResponse>> = MutableLiveData()
    val response: LiveData<DataState<ForgotPasswordResponse>> = _response

    fun setState(event: ForgotPasswordStateEvent) {
        val handler = CoroutineExceptionHandler { _, exception ->
            exceptionObserver.postValue(exception)
        }
        viewModelScope.launch(Dispatchers.IO + handler) {
            supervisorScope {

                when (event) {
                    is ForgotPasswordStateEvent.RequestReset -> {
                        userRepository.requestResetCode(event.email).collect {
                            _response.postValue(it)
                        }
                    }
                }
            }
        }
    }
}