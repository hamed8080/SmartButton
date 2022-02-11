package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.review

import ir.amozkade.advancedAsisstiveTouche.mvvm.BaseActivityDelegate
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.translator.TranslateResult

interface ReviewDelegate : BaseActivityDelegate {
    fun finishedReview()
    fun onMeanItemTaped(translateResult: TranslateResult)
    fun resetView()
}