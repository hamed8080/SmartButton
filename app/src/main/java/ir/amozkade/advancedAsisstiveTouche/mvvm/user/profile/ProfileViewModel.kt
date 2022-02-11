package ir.amozkade.advancedAsisstiveTouche.mvvm.user.profile

import javax.inject.Inject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.amozkade.advancedAsisstiveTouche.mvvm.preferenceActivity.SettingRepository
import ir.amozkade.advancedAsisstiveTouche.mvvm.user.profile.utils.EditProfileStateEvent
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import saman.zamani.persiandate.PersianDate

@HiltViewModel
class ProfileViewModel @Inject constructor(
        val exceptionObserver: MutableLiveData<Throwable>,
        private val profileRepository: ProfileRepository,
        private val settingRepository: SettingRepository
) : ViewModel() {

    private var model: ProfileModel = ProfileModel()

    init {
        setState(EditProfileStateEvent.Init)
    }

    fun setState(event: EditProfileStateEvent) {
        val handler = CoroutineExceptionHandler { _, exception ->
            exceptionObserver.postValue(exception)
        }
        viewModelScope.launch(IO + handler) {
            supervisorScope {

                when (event) {
                    is EditProfileStateEvent.Init -> {
                        setupProfile()
                    }
                    is EditProfileStateEvent.ClearProfile -> {
                        profileRepository.clearProfile()
                    }
                    EditProfileStateEvent.ClearSyncFirebaseToken -> {
                        settingRepository.setNewFirebaseTokenSync(false)
                    }
                }
            }
        }
    }

    private fun setupProfile() {
        GlobalScope.launch(IO) {
            profileRepository.getProfile()?.let { payload ->
                withContext(Main) {
                    model.userName = payload.email ?: ""
                    model.email = payload.email ?: ""
                    model.phone = payload.phone ?: ""
                    model.firstName = payload.firstName ?: ""
                    model.lastName = payload.lastName ?: ""
                    val lastLogin = payload.lastLogin
                    if (lastLogin != null) {
                        val d = PersianDate(lastLogin)
                        model.lastLoginDateText = "${d.shYear}/${d.shMonth}/${d.shDay} ${d.hour}:${d.minute}"
                    }
                }
            }
        }
    }

    fun getModel(): ProfileModel {
        return model
    }
}