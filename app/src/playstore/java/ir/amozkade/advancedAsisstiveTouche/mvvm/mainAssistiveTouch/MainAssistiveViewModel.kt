package ir.amozkade.advancedAsisstiveTouche.mvvm.mainAssistiveTouch

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import ir.amozkade.advancedAsisstiveTouche.helper.Common
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.models.DefaultButtons
import ir.amozkade.advancedAsisstiveTouche.mvvm.exception.ExceptionRepository
import ir.amozkade.advancedAsisstiveTouche.mvvm.preferenceActivity.SettingRepository
import ir.amozkade.advancedAsisstiveTouche.mvvm.update.UpdateRepository
import ir.amozkade.advancedAsisstiveTouche.mvvm.user.UserRepository

@HiltViewModel
open class MainAssistiveViewModel @Inject constructor(
    override val exceptionObserver: MutableLiveData<Throwable>,
    defaultButtons: DefaultButtons,
    commonHelper: Common,
    mainRepository: MainRepository,
    googleDriveRepository: GoogleDriveRepository,
    exceptionRepository: ExceptionRepository,
    userRepository: UserRepository,
    settingRepository: SettingRepository,
    common: Common,
    updateRepository: UpdateRepository

) : BaseMainAssistiveViewModel(
    exceptionObserver,
    defaultButtons,
    commonHelper,
    mainRepository,
    googleDriveRepository,
    exceptionRepository,
    userRepository,
    settingRepository,
    common,
    updateRepository

) {
    override fun setActivityContext(context: AppCompatActivity) {
        super.setActivityContext(context)
    }
}