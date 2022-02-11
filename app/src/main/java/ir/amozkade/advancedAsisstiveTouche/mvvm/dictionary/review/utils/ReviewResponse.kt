package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.review.utils

import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.translator.TranslateResult

sealed class ReviewResponse {
    object ResetView : ReviewResponse()
    data class MeansWithDictName(val means: List<TranslateResult>) : ReviewResponse()
    object Completed : ReviewResponse()
    data class Favorite(val favorite:Boolean):ReviewResponse()
}
