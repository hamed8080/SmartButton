package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.addOrEditQuestion

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import ir.amozkade.advancedAsisstiveTouche.BR
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.addOrEditQuestion.utils.EditedQuestionAnswer
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.levels.questionAnswer.QuestionAnswer

class AddNewQuestionModel : BaseObservable() {

    var questionAnswer: QuestionAnswer? = null
        set(value) {
            field = value
            isInEditMode = true
            answer = value?.answer ?: ""
            isManual = value?.answer != null
            question = value?.question ?: ""
        }

    var isInEditMode: Boolean = false

    @Bindable
    var question: String? = null
        set(value) {
            field = value ?: ""
            notifyPropertyChanged(BR.question)
        }

    @Bindable
    var answer: String? = null
        set(value) {
            field = value ?: ""
            notifyPropertyChanged(BR.answer)
        }

    @Bindable
    var isManual: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.isManual)
        }

    fun getNewQuestionAnswer(levelId: Int, leitnerId: Int): QuestionAnswer? {
        val question = question ?: return null
        val answer: String? = if (isManual) {
            this.answer
        } else {
            null
        }

        return QuestionAnswer(question, answer, null, leitnerId, levelId)
    }

    fun getEditedQuestionAnswer(): EditedQuestionAnswer? {
        val question = question ?: return null
        val answer: String? = if (isManual) {
            this.answer
        } else {
            null
        }
        return EditedQuestionAnswer(QuestionAnswer(question, answer, questionAnswer?.passedTime, questionAnswer?.leitnerId!!, questionAnswer?.levelId!!), questionAnswer!!)
    }
}