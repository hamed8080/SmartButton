package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.leitners.add

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import ir.amozkade.advancedAsisstiveTouche.BR
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.leitners.models.Leitner

class AddOrEditLeitnerModel : BaseObservable() {

    @Bindable
    private lateinit var leitner: Leitner

    fun setLeitner(leitner : Leitner) {
        this.leitner = leitner
        name = leitner.name
        notifyPropertyChanged(BR.name)
    }

    fun getLeitner(): Leitner {
        return leitner
    }

    @Bindable
    var name: String = ""
        set(value) {
            field = value
            leitner.name = field
            notifyPropertyChanged(BR.name)
        }

    @Bindable
    var sourceLang: String = "en"
        set(value) {
            field = value
            notifyPropertyChanged(BR.sourceLang)
        }

    @Bindable
    var destLang: String = "fa"
        set(value) {
            field = value
            notifyPropertyChanged(BR.destLang)
        }
}