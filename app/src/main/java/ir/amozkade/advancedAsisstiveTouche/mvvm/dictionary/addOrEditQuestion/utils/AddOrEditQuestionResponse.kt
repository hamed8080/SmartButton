package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.addOrEditQuestion.utils

import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.levels.questionAnswer.QuestionAnswer

sealed class AddOrEditQuestionResponse{
    data class SuccessAdded(val questionAnswer:QuestionAnswer):AddOrEditQuestionResponse()
    data class SuccessEdited(val questionAnswer:QuestionAnswer):AddOrEditQuestionResponse()
}
