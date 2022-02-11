package ir.amozkade.advancedAsisstiveTouche.mvvm.user.login

import android.content.Context
import android.text.Editable
import android.util.Log
import javax.inject.Inject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.helper.InAppException
import ir.amozkade.advancedAsisstiveTouche.helper.api.DataState
import ir.amozkade.advancedAsisstiveTouche.mvvm.user.UserRepository
import ir.amozkade.advancedAsisstiveTouche.mvvm.user.login.utils.LoginResponse
import ir.amozkade.advancedAsisstiveTouche.mvvm.user.login.utils.LoginStateEvent
import ir.mobitrain.applicationcore.helper.CTextWatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

@HiltViewModel
class LoginViewModel @Inject constructor(
        val exceptionObserver: MutableLiveData<Throwable>,
        @ApplicationContext private val context: Context,
        private val userRepository: UserRepository
) : ViewModel() {
    private val model = LoginModel()

    private val _response: MutableLiveData<DataState<LoginResponse>> = MutableLiveData()
    val response: LiveData<DataState<LoginResponse>> = _response

    fun setState(event: LoginStateEvent) {
        val handler = CoroutineExceptionHandler { _, exception ->
            exceptionObserver.postValue(exception)
        }
        viewModelScope.launch(handler) {
            supervisorScope {

                when (event) {
                    is LoginStateEvent.DoLogin -> {
                        login()
                    }
                    is LoginStateEvent.GoogleLogin -> {
                        userRepository.getTokenFromGoogle(accessToken = event.token)
                    }
                }
            }
        }
    }

    private suspend fun login() {
        model.validForLogin()?.let {
            exceptionObserver.postValue(InAppException(context.getString(R.string.failed), context.getString(it), null))
            return
        }
        userRepository.doLogin(model.email!!, model.password!!).collect {
            _response.postValue(it)
        }
    }

    var emailTextWatcher: CTextWatcher = object : CTextWatcher() {
        override fun afterTextChanged(s: Editable) {
            super.afterTextChanged(s)
            model.email = s.toString()
        }
    }

    var userPassTextWatcher: CTextWatcher = object : CTextWatcher() {
        override fun afterTextChanged(s: Editable) {
            super.afterTextChanged(s)
            model.password = s.toString()
        }
    }

    fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            setState(LoginStateEvent.GoogleLogin(account.idToken))
        } catch (e: ApiException) {
            Log.w("TAG", "signInResult:failed code=" + e.statusCode)
        }
    }

}