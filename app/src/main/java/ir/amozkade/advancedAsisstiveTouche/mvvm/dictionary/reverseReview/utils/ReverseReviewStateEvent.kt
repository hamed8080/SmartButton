package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.reverseReview.utils

sealed class ReverseReviewStateEvent{
    data class Init(val leitnerId:Int, val level:Int):ReverseReviewStateEvent()
    object Guessed:ReverseReviewStateEvent()
    object Failed:ReverseReviewStateEvent()
    object DeleteQuestion:ReverseReviewStateEvent()
    object Speak:ReverseReviewStateEvent()
    object CheckReverse : ReverseReviewStateEvent()
}
