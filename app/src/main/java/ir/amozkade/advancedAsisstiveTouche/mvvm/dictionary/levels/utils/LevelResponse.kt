package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.levels.utils

import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.leitners.models.LeitnerLevels

sealed class LevelResponse{
    data class Levels(val levels:List<LeitnerLevels>): LevelResponse()
    data class CompletedCounts(val completedCount:Int): LevelResponse()
}
