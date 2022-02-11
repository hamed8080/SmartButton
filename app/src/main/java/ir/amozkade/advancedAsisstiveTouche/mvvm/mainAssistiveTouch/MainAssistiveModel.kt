package ir.amozkade.advancedAsisstiveTouche.mvvm.mainAssistiveTouch

import android.annotation.SuppressLint
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import ir.amozkade.advancedAsisstiveTouche.BR
import java.util.*

class MainAssistiveModel : BaseObservable() {

    @Bindable
    var serviceStarted: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.serviceStarted)
        }

    @Bindable
    var doubleTapSelectedActionTitle: String? = ""
        @SuppressLint("DefaultLocale")
        set(value) {
            field =
                value?.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
            notifyPropertyChanged(BR.doubleTapSelectedActionTitle)
        }

    @Bindable
    var singleTapSelectedActionTitle: String? = ""
        @SuppressLint("DefaultLocale")
        set(value) {
            field =
                value?.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
            notifyPropertyChanged(BR.singleTapSelectedActionTitle)
        }

    @Bindable
    var holdSelectedActionTitle: String? = ""
        @SuppressLint("DefaultLocale")
        set(value) {
            field =
                value?.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
            notifyPropertyChanged(BR.holdSelectedActionTitle)
        }


    @Bindable
    var googleDriveIsInUploadOrDownloading: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.googleDriveIsInUploadOrDownloading)
            googleDriveButtonsOpacity = if(value) .4f else 1f
        }

    @Bindable
    var googleDriveButtonsOpacity: Float = 1f
        set(value) {
            field = value
            notifyPropertyChanged(BR.googleDriveButtonsOpacity)
        }

    @Bindable
    var googleDriveUploadPercent: Int = 0
        set(value) {
            field = value
            notifyPropertyChanged(BR.googleDriveUploadPercent)
        }

    @Bindable
    var googleDriveUploadPercentText: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.googleDriveUploadPercentText)
        }

}