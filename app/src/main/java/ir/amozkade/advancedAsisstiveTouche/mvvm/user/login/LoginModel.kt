package ir.amozkade.advancedAsisstiveTouche.mvvm.user.login

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.BR

class LoginModel : BaseObservable(){


    @Bindable
    var email: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.email)
        }


    @Bindable
    var password: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.password)
        }

    fun validForLogin(): Int? {
        if (email.isNullOrEmpty()) {
            return R.string.enterEmail
        }
        if (password.isNullOrEmpty()) {
            return R.string.password_length_must_grater_than_six
        }
        return null
    }

}