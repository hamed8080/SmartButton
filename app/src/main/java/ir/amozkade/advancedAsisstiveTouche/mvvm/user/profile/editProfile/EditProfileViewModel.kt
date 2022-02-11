package ir.amozkade.advancedAsisstiveTouche.mvvm.user.profile.editProfile

import android.content.Context
import android.graphics.Bitmap
import android.text.Editable
import android.util.Base64
import javax.inject.Inject
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.helper.InAppException
import ir.amozkade.advancedAsisstiveTouche.helper.api.DataState
import ir.amozkade.advancedAsisstiveTouche.mvvm.user.UserRepository
import ir.amozkade.advancedAsisstiveTouche.mvvm.user.login.models.Payload
import ir.amozkade.advancedAsisstiveTouche.mvvm.user.profile.editProfile.utils.EditProfileResponse
import ir.amozkade.advancedAsisstiveTouche.mvvm.user.profile.editProfile.utils.EditProfileStateEvent
import ir.mobitrain.applicationcore.api.JWT
import ir.mobitrain.applicationcore.helper.CTextWatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import java.io.ByteArrayOutputStream

@HiltViewModel
class EditProfileViewModel @Inject constructor(
        val exceptionObserver: MutableLiveData<Throwable>,
        @ApplicationContext private val context: Context,
        private val userRepository: UserRepository
) : ViewModel() {

    private val model: EditProfileModel = EditProfileModel()
    private val _response: MutableLiveData<DataState<EditProfileResponse>> = MutableLiveData()
    val response: LiveData<DataState<EditProfileResponse>> = _response

    var lastNameTextWatcher: CTextWatcher = object : CTextWatcher() {
        override fun afterTextChanged(s: Editable) {
            super.afterTextChanged(s)
            model.lastName = s.toString()
        }
    }

    var firstNameTextWatcher: CTextWatcher = object : CTextWatcher() {
        override fun afterTextChanged(s: Editable) {
            super.afterTextChanged(s)
            model.firstName = s.toString()
        }
    }

    var phoneTextWatcher: CTextWatcher = object : CTextWatcher() {
        override fun afterTextChanged(s: Editable) {
            super.afterTextChanged(s)
            model.phone = s.toString()
        }
    }

    fun setState(event: EditProfileStateEvent) {
        val handler = CoroutineExceptionHandler { _, exception ->
            exceptionObserver.postValue(exception)
        }
        viewModelScope.launch(Dispatchers.IO + handler) {
            supervisorScope {
                when (event) {
                    is EditProfileStateEvent.EditProfile -> {
                        editUserDetail()
                    }
                    is EditProfileStateEvent.UploadImage -> {
                        uploadImage(event.bitmap)
                    }
                }
            }
        }
    }

    private suspend fun editUserDetail() {
        model.validForEditProfile()?.let {
            exceptionObserver.postValue(InAppException(context.getString(R.string.edit_profile), context.getString(it), null))
            return
        }
        getEditDetailReq()?.let {editReq->
            userRepository.doEditProfile(editReq).collect {
                _response.postValue(it)
            }
        }
    }

    private suspend fun uploadImage(profileImage: Bitmap?) {
        profileImage?.let {
            val stream = ByteArrayOutputStream()
            profileImage.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val byteArray = stream.toByteArray()
            val base64Str = Base64.encodeToString(byteArray, Base64.DEFAULT)
            userRepository.uploadProfileImage(base64Str).collect {
                _response.postValue(it)
            }
        }
    }

    private fun getEditDetailReq(): EditUserDetailReq? {
        val mobile = model.phone
        val userId = JWT.instance.getPayload<Payload>()?.userId
        val firstName = model.firstName
        val lastName = model.lastName
        if(mobile!= null && userId != null && firstName != null && lastName != null ){
            return EditUserDetailReq(mobile, userId, firstName, lastName)
        }
        return null
    }

    fun getModel(): EditProfileModel {
        return model
    }
}