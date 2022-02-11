package ir.amozkade.advancedAsisstiveTouche.mvvm.user.loginWithSms

import javax.inject.Inject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.amozkade.advancedAsisstiveTouche.helper.api.DataState
import ir.amozkade.advancedAsisstiveTouche.mvvm.user.UserRepository
import ir.amozkade.advancedAsisstiveTouche.mvvm.user.loginWithSms.utils.LoginWithSMSResponse
import ir.amozkade.advancedAsisstiveTouche.mvvm.user.loginWithSms.utils.LoginWithSMSStateEvent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

@HiltViewModel
class LoginWithSMSViewModel @Inject constructor(
        val exceptionObserver: MutableLiveData<Throwable>,
        private val userRepository: UserRepository
) : ViewModel() {


    private val _response: MutableLiveData<DataState<LoginWithSMSResponse>> = MutableLiveData()
    val response: LiveData<DataState<LoginWithSMSResponse>> = _response


    fun setState(event: LoginWithSMSStateEvent) {
        val handler = CoroutineExceptionHandler { _, exception ->
            exceptionObserver.postValue(exception)
        }
        viewModelScope.launch(handler) {
            supervisorScope {

                when (event) {
                    is LoginWithSMSStateEvent.RequestVerificationCode -> {
                        userRepository.requestVerificationCode(event.phoneNumber).collect {
                            _response.postValue(it)
                        }
                    }
                    is LoginWithSMSStateEvent.VerifyCode -> {
                        userRepository.verifyWithServer(event.phoneNumber, event.vrfCode).collect {
                            _response.postValue(it)
                        }
                    }
                }
            }
        }
    }
}