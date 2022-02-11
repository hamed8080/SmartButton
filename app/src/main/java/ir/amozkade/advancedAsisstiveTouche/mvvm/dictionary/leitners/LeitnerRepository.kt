package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.leitners

import ir.amozkade.advancedAsisstiveTouche.helper.api.DataState
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.leitners.add.utils.AddOrEditLeitnerResponse
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.leitners.di.LeitnerDao
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.leitners.models.Leitner
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.leitners.utils.LeitnerResponse
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.levels.repository.LevelRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LeitnerRepository @Inject constructor(private val leitnerDao: LeitnerDao, private val levelRepository: LevelRepository) {

    suspend fun delete(leitner: Leitner): Flow<DataState<LeitnerResponse>> = flow {
        emit(DataState.Loading)
        leitnerDao.delete(leitner)
        levelRepository.deleteLevelsForLeitner(leitner.id)
        emit(DataState.Success(LeitnerResponse.DeletedLeitner(leitner)))
    }

    suspend fun getAllLeitners(): Flow<DataState<LeitnerResponse>> = flow {
        emit(DataState.Loading)
        val leitners = leitnerDao.getAll()
        emit(DataState.Success(LeitnerResponse.AllLeitner(leitners)))
    }

    fun addOrUpdateLeitner(leitner: Leitner): Flow<DataState<LeitnerResponse>> = flow {
        emit(DataState.Loading)
        if (leitner.id == 0) {
            val leitnerInsertedId = insert(leitner)
            levelRepository.insert(LevelRepository.getInitializeLevelsForLeitner(leitnerInsertedId.toInt()))
        } else {
            leitnerDao.update(leitner)
        }
        val leitners =  leitnerDao.getAll()
        emit(DataState.Success(LeitnerResponse.AddOrEditedLeitner(leitners)))
    }

    private suspend fun insert(leitner: Leitner): Long {
        return leitnerDao.insert(leitner)
    }

    suspend fun checkExist(name: String): Flow<DataState<AddOrEditLeitnerResponse>> = flow {
        emit(DataState.Loading)
        val exist = leitnerDao.checkDuplicate(name) != null
        emit(DataState.Success(AddOrEditLeitnerResponse.CheckedDuplicateResult(exist)))
    }

    fun setIsBackToTopEnable(leitner: Leitner) = flow<DataState<LeitnerResponse>> {
        emit(DataState.Loading)
        leitnerDao.setBackToTopEnable(leitner)
        val leitners = leitnerDao.getAll()
        emit(DataState.Success(LeitnerResponse.AllLeitner(leitners)))
    }

    fun setShowDefinition(leitner: Leitner) = flow<DataState<LeitnerResponse>> {
        emit(DataState.Loading)
        leitnerDao.setShowDefinition(leitner)
        val leitners = leitnerDao.getAll()
        emit(DataState.Success(LeitnerResponse.AllLeitner(leitners)))
    }

    suspend fun getLeitnerWithId(id:Int):Leitner{
        return  leitnerDao.getLeitnerById(id)
    }
}
