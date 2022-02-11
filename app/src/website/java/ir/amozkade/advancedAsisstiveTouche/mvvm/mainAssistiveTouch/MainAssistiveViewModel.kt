package ir.amozkade.advancedAsisstiveTouche.mvvm.mainAssistiveTouch

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.amozkade.advancedAsisstiveTouche.helper.Common
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.models.DefaultButtons
import ir.amozkade.advancedAsisstiveTouche.mvvm.exception.ExceptionRepository
import ir.amozkade.advancedAsisstiveTouche.mvvm.mainAssistiveTouch.utils.MainStateEvent
import ir.amozkade.advancedAsisstiveTouche.mvvm.preferenceActivity.SettingRepository
import ir.amozkade.advancedAsisstiveTouche.mvvm.update.UpdateRepository
import ir.amozkade.advancedAsisstiveTouche.mvvm.user.UserRepository
import javax.inject.Inject

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
){

    override fun setActivityContext(context: AppCompatActivity) {
        super.setActivityContext(context)
    }

    fun openInstagram() {
        setState(MainStateEvent.FirebaseAnalytics("OPEN_TELEGRAM"))
        val uri = Uri.parse("http://instagram.com/_u/mobitrain.ir")
        val likeIng = Intent(Intent.ACTION_VIEW, uri)

        likeIng.setPackage("com.instagram.android")

        try {
            context.startActivity(likeIng)
        } catch (e: ActivityNotFoundException) {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/mobitrain.ir")))
        }
    }

    fun openWebsite() {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://mobitrain.ir/smartbutton")))
    }
}