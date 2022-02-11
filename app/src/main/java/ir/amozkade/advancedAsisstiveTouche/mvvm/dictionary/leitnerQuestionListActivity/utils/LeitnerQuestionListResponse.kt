package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.leitnerQuestionListActivity.utils

import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.levels.models.Level
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.levels.questionAnswer.QuestionAnswer

sealed class LeitnerQuestionListResponse{
    data class AllQuestions(val questionAnswers:List<QuestionAnswer> , val levels:List<Level>):LeitnerQuestionListResponse()
    data class QuestionAnswerUpdated(val questionAnswer:QuestionAnswer):LeitnerQuestionListResponse()
    data class Removed(val questionAnswer:QuestionAnswer):LeitnerQuestionListResponse()
}
