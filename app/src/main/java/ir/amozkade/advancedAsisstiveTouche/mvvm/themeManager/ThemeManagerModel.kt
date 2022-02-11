package ir.amozkade.advancedAsisstiveTouche.mvvm.themeManager

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import ir.amozkade.advancedAsisstiveTouche.BR

class ThemeManagerModel : BaseObservable() {

    @Bindable
    var answer: String? = null
        set(value) {
            field = value ?: ""
            notifyPropertyChanged(BR.answer)
        }
}
