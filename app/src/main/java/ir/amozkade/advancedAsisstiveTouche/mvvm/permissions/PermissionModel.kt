package ir.amozkade.advancedAsisstiveTouche.mvvm.permissions

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import ir.amozkade.advancedAsisstiveTouche.BR

class PermissionModel : BaseObservable() {

    @Bindable
    var serviceStarted: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.serviceStarted)
        }
}
