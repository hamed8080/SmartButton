package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.levels

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import ir.amozkade.advancedAsisstiveTouche.BR
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.leitners.models.Leitner

class LevelsModel : BaseObservable() {

    @Bindable
    var time: String = "1"
        set(value) {
            field = value
            notifyPropertyChanged(BR.time)
        }

    @Bindable
    var leitner: Leitner? = null
        set(value) {
            field = value ?: Leitner(id = 0, name = "")
            name = leitner?.name ?: ""
        }

    @Bindable
    var name: String = ""
        set(value) {
            field = value
            leitner?.name = field
            notifyPropertyChanged(BR.name)
        }

    @Bindable
    var completedCount: Int = 0
        set(value) {
            field = value
            notifyPropertyChanged(BR.completedCount)
            totalQuestion = completedCount + unCompletedCount
        }

    @Bindable
    var unCompletedCount: Int = 0
        set(value) {
            field = value
            notifyPropertyChanged(BR.unCompletedCount)
            totalQuestion = completedCount + unCompletedCount
        }

    @Bindable
    var totalQuestion: Int = 0
        set(_) {
            field = completedCount + unCompletedCount
            notifyPropertyChanged(BR.totalQuestion)
        }


}