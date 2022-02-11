package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.levels.utils

import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.leitners.models.Leitner
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.leitners.models.LeitnerLevels

sealed class LevelStateEvent{
    data class GetAllLevelsInLeitner(val leitner: Leitner):LevelStateEvent()
    data class UpdateTimeOfLevel(val level: LeitnerLevels, val day: Int):LevelStateEvent()
}

