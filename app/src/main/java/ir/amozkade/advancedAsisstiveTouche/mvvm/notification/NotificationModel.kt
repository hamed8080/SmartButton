package ir.amozkade.advancedAsisstiveTouche.mvvm.notification

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import ir.amozkade.advancedAsisstiveTouche.BR

class NotificationModel : BaseObservable() {

    @Bindable
    var listEmpty = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.listEmpty)
        }

}