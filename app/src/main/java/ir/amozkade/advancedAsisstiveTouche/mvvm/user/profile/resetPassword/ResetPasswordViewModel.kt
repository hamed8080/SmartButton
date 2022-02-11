package ir.amozkade.advancedAsisstiveTouche.mvvm.user.profile.resetPassword

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
import ir.amozkade.advancedAsisstiveTouche.mvvm.user.login.models.Payload
import ir.amozkade.advancedAsisstiveTouche.mvvm.user.profile.resetPassword.utils.ResetPasswordResponse
import ir.amozkade.advancedAsisstiveTouche.mvvm.user.profile.resetPassword.utils.ResetPasswordStateEvent
import ir.mobitrain.applicationcore.api.JWT
import ir.mobitrain.applicationcore.helper.CTextWatcher
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
        private val userRepository: UserRepository,
        @ApplicationContext private val context: Context,
        val exceptionObserver: MutableLiveData<Throwable>
) : ViewModel() {

    private val model: ResetPasswordModel = ResetPasswordModel()
    private val _response: MutableLiveData<DataState<ResetPasswordResponse>> = MutableLiveData()
    val response: LiveData<DataState<ResetPasswordResponse>> = _response

    var currentPasswordTextWatcher: CTextWatcher = object : CTextWatcher() {
        override fun afterTextChanged(s: Editable) {
            super.afterTextChanged(s)
            model.currentPassword = s.toString()
        }
    }
    var newPasswordTextWatcher: CTextWatcher = object : CTextWatcher() {
        override fun afterTextChanged(s: Editable) {
            super.afterTextChanged(s)
            model.newPassword = s.toString()
        }
    }
    var newPasswordRepeatTextWatcher: CTextWatcher = object : CTextWatcher() {
        override fun afterTextChanged(s: Editable) {
            super.afterTextChanged(s)
            model.newPasswordRepeat = s.toString()
        }
    }

    fun setState(event: ResetPasswordStateEvent) {
        val handler = CoroutineExceptionHandler { _, exception ->
            exceptionObserver.postValue(exception)
        }
        viewModelScope.launch(Dispatchers.IO + handler) {
            supervisorScope {
                when (event) {
                    is ResetPasswordStateEvent.ResetPassword -> {
                        resetPassword()
                    }
                }
            }
        }
    }

    private suspend fun resetPassword() {
        model.valid()?.let {
            exceptionObserver.postValue(InAppException(context.getString(R.string.reset_password), context.getString(it), null))
            return
        }
        userRepository.resetPassword(getPasswordModel()).collect {
            _response.postValue(it)
        }
    }

    private fun getPasswordModel(): PasswordResetReq {
        return PasswordResetReq(userId = JWT.instance.getPayload<Payload>()?.userId!!, currentPassword = model.currentPassword!!, newPassword = model.newPassword!!)
    }

    fun getModel(): ResetPasswordModel {
        return model
    }

}