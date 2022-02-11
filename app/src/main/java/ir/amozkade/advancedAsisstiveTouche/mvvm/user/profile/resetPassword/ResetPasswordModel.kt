package ir.amozkade.advancedAsisstiveTouche.mvvm.user.profile.resetPassword

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.BR

class ResetPasswordModel : BaseObservable() {
    @Bindable
    var currentPassword: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.currentPassword)
        }

    @Bindable
    var newPassword: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.newPassword)
        }


    @Bindable
    var newPasswordRepeat: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.newPasswordRepeat)
        }

    fun valid(): Int? {
        if (currentPassword.isNullOrEmpty()) {
            return R.string.enter_current_password
        }
        if (newPassword.isNullOrEmpty() || newPassword!!.length < 6) {
            return R.string.password_length_must_grater_than_six
        }
        if (newPasswordRepeat.isNullOrEmpty() || newPasswordRepeat != newPassword) {
            return R.string.incorrect_repeat_pass
        }
        return null
    }
}