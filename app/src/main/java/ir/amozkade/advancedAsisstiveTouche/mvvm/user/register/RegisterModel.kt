package ir.amozkade.advancedAsisstiveTouche.mvvm.user.register

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import ir.amozkade.advancedAsisstiveTouche.BR
import ir.amozkade.advancedAsisstiveTouche.R

class RegisterModel : BaseObservable() {

    @Bindable
    var email: String? = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.email)
        }

    @Bindable
    var password: String? = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.password)
        }

    @Bindable
    var repeatPassword: String? = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.repeatPassword)
        }


    fun valid(): Int? {
        if (email?.isEmpty() == true) {
            return R.string.enterEmail
        }
        if (password?.isEmpty() == true || password?.length!! < 6) {
            return R.string.password_length_must_grater_than_six
        }
        if (repeatPassword?.isEmpty() == true || repeatPassword != password) {
            return R.string.incorrect_repeat_pass
        }
        return null
    }
}