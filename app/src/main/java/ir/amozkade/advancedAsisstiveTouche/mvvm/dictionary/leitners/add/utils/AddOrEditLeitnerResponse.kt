package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.leitners.add.utils

sealed class AddOrEditLeitnerResponse{
    data class CheckedDuplicateResult(val exist:Boolean):AddOrEditLeitnerResponse()
}
