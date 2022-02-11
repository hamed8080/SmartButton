package ir.amozkade.advancedAsisstiveTouche.mvvm.user.profile

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import ir.amozkade.advancedAsisstiveTouche.BR

class ProfileModel : BaseObservable() {

    @Bindable
    var userName: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.userName)
        }

    @Bindable
    var email: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.email)
        }

    @Bindable
    var phone: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.phone)
        }

    @Bindable
    var firstName: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.firstName)
        }

    @Bindable
    var lastName: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.lastName)
        }

    @Bindable
    var lastLoginDateText: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.lastLoginDateText)
        }
}