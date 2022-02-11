package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.review.utils

sealed class ReviewStateEvent{
    data class Init(val leitnerId:Int, val level:Int):ReviewStateEvent()
    object Guessed:ReviewStateEvent()
    object Failed:ReviewStateEvent()
    object DeleteQuestion:ReviewStateEvent()
    object Speak:ReviewStateEvent()
    object Favorite:ReviewStateEvent()
}
