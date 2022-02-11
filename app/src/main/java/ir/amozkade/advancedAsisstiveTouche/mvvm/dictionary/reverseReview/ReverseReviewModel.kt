package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.reverseReview

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import ir.amozkade.advancedAsisstiveTouche.BR
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.leitners.models.Leitner
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.levels.questionAnswer.QuestionAnswer

class ReverseReviewModel : BaseObservable() {


    @Bindable
    var answerVisible: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.answerVisible)
        }

    var typedReverse: String = ""

    var questionAnswer: QuestionAnswer? = null

    @Bindable
    var question: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.question)
        }

    @Bindable
    var answer: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.answer)
        }

    @Bindable
    var totalItemsInLevel: Int = 0
        set(value) {
            field = value
            notifyPropertyChanged(BR.totalItemsInLevel)
        }

    @Bindable
    var passedItemsInLevel: Int = 0
        set(value) {
            field = value
            notifyPropertyChanged(BR.passedItemsInLevel)
            numberOfReviewedItems = 0 // fake to update view
        }

    @Bindable
    var failedItemInLevel: Int = 0
        set(value) {
            field = value
            notifyPropertyChanged(BR.failedItemInLevel)
            numberOfReviewedItems = 0 // fake to update view
        }

    @Bindable
    var manual: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.manual)
        }

    @Bindable
    var numberOfReviewedItems: Int = 0
        set(@Suppress("UNUSED_PARAMETER") value) {
            field = passedItemsInLevel + failedItemInLevel
            notifyPropertyChanged(BR.numberOfReviewedItems)
        }

    lateinit var leitner: Leitner
    var level: Int = 0
}