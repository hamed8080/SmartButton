package ir.amozkade.advancedAsisstiveTouche.mvvm.user.register

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
import ir.amozkade.advancedAsisstiveTouche.mvvm.user.UserRepository
import ir.amozkade.advancedAsisstiveTouche.mvvm.user.register.utils.RegisterResponse
import ir.amozkade.advancedAsisstiveTouche.mvvm.user.register.utils.RegisterStateEvent
import ir.mobitrain.applicationcore.helper.CTextWatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

@HiltViewModel
class RegisterViewModel @Inject constructor(
        val exceptionObserver: MutableLiveData<Throwable>,
        @ApplicationContext private val context: Context,
        private val userRepository: UserRepository
) : ViewModel() {


    private val _response: MutableLiveData<DataState<RegisterResponse>> = MutableLiveData()
    val response: LiveData<DataState<RegisterResponse>> = _response
    var model: RegisterModel = RegisterModel()


    var emailTextWatcher: CTextWatcher = object : CTextWatcher() {
        override fun afterTextChanged(s: Editable) {
            super.afterTextChanged(s)
            model.email = s.toString()
        }
    }

    var passwordTextWatcher: CTextWatcher = object : CTextWatcher() {
        override fun afterTextChanged(s: Editable) {
            super.afterTextChanged(s)
            model.password = s.toString()
        }
    }

    var repeatPasswordTextWatcher: CTextWatcher = object : CTextWatcher() {
        override fun afterTextChanged(s: Editable) {
            super.afterTextChanged(s)
            model.repeatPassword = s.toString()
        }
    }


    fun setState(event: RegisterStateEvent) {
        val handler = CoroutineExceptionHandler { _, exception ->
            exceptionObserver.postValue(exception)
        }
        viewModelScope.launch(handler) {
            supervisorScope {

                when (event) {
                    is RegisterStateEvent.Register -> {
                        register()
                    }
                }
            }
        }
    }

    private suspend fun register() {
        model.valid()?.let {
            exceptionObserver.postValue(InAppException(context.getString(R.string.register), context.getString(it), null))
            return
        }
        val email = model.email ?: return
        val password = model.password ?: return
        userRepository.doRegister(email, password).collect {
            _response.postValue(it)
        }
    }

}