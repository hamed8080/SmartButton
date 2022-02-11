package ir.amozkade.advancedAsisstiveTouche.mvvm.user.profile.editProfile

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import ir.amozkade.advancedAsisstiveTouche.BR
import ir.amozkade.advancedAsisstiveTouche.R

class EditProfileModel : BaseObservable() {

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

    fun validForEditProfile(): Int? {
        if (firstName.isNullOrEmpty()) {
            return R.string.enter_first_name
        }
        if (lastName.isNullOrEmpty()) {
            return R.string.enter_family_name
        }
        if (phone.isNullOrEmpty()) {
            return R.string.enter_phone_number
        }
        return null
    }

}