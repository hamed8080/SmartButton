package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.levels.repository

import ir.amozkade.advancedAsisstiveTouche.helper.api.DataState
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.leitners.models.Leitner
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.levels.models.Level
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.levels.utils.LevelResponse
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LevelRepository @Inject constructor(private val levelDao: LevelDao) {

    suspend fun insert(levels: List<Level>) {
        levelDao.insert(levels)
    }

    suspend fun firstLevelIdForLeitner(leitnerId: Int): Int = withContext(IO) {
        levelDao.getFirstLevelIdInLeitner(leitnerId)
    }

    suspend fun updateTimeOfLevel(levelId: Int, day: Int) {
        val level = levelDao.getLevelWithLevelId(levelId)
        level.time = day
        levelDao.update(level)
    }

    suspend fun getAllLevelsInLeitner(leitner: Leitner): Flow<DataState<LevelResponse>> = flow {
        emit(DataState.Loading)
        val levels = levelDao.getAllLevels(leitner.id)
        emit(DataState.Success(LevelResponse.Levels(levels)))
    }

    suspend fun deleteLevelsForLeitner(leitnerId: Int) {
        levelDao.deleteLevelsForLeitner(leitnerId)
    }

    companion object {

        fun getInitializeLevelsForLeitner(leitnerId: Int): List<Level> {
            val levels = arrayListOf<Level>()
            //MAX 200 Date for default
            val times = listOf(1, 2, 4, 8, 16, 36, 48, 64, 96, 128, 148, 164, 200)
            for (i in 1..13) {
                levels.add(Level(0, i, times[i - 1], leitnerId))
            }
            return levels
        }
    }

}
