package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.leitners.utils

import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.leitners.models.Leitner

sealed class LeitnerResponse{
    data class AllLeitner(val leitners:List<Leitner>): LeitnerResponse()
    data class DeletedLeitner(val leitner: Leitner):LeitnerResponse()
    data class AddOrEditedLeitner(val leitners: List<Leitner>):LeitnerResponse()
}
