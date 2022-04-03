package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.leitnerQuestionListActivity.utils

import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.levels.questionAnswer.QuestionAnswer

sealed class LeitnerQuestionListStateEvent {
    data class GetAllLeitnerQuestions(val leitnerId: Int) : LeitnerQuestionListStateEvent()
    data class Search(val query:String):LeitnerQuestionListStateEvent()
    data class Sort(val checkedId:Int):LeitnerQuestionListStateEvent()
    data class Delete(val questionAnswer: QuestionAnswer):LeitnerQuestionListStateEvent()
    data class BackToList(val questionAnswer: QuestionAnswer):LeitnerQuestionListStateEvent()
    data class MoveToLeitner(val questionAnswer: QuestionAnswer,val leitnerId: Int):LeitnerQuestionListStateEvent()
    data class Fav(val questionAnswer: QuestionAnswer):LeitnerQuestionListStateEvent()
    data class Edited(val questionAnswer: QuestionAnswer?):LeitnerQuestionListStateEvent()
    data class Add(val questionAnswer: QuestionAnswer?):LeitnerQuestionListStateEvent()
    object PlayOrPause:LeitnerQuestionListStateEvent()
}

sealed class SortType{
    object Date:SortType()
    object Alphabet:SortType()
    object Level:SortType()
}
