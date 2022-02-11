package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.leitners.utils

import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.leitners.models.Leitner

sealed class LeitnerStateEvent {
    object AllLeitner : LeitnerStateEvent()
    data class DeleteLeitner(val leitner: Leitner) : LeitnerStateEvent()
    data class AddOrEditLeitner(val leitner: Leitner) : LeitnerStateEvent()
    data class SetBackToTopEnable(val leitner: Leitner) : LeitnerStateEvent()
    data class SeShowDefinition(val leitner: Leitner) : LeitnerStateEvent()
}

