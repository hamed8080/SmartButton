package ir.amozkade.advancedAsisstiveTouche.mvvm.editButtonPositions

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import ir.amozkade.advancedAsisstiveTouche.BR

class EditPositionModel : BaseObservable() {

    @Bindable
    var listEmpty = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.listEmpty)
        }
}