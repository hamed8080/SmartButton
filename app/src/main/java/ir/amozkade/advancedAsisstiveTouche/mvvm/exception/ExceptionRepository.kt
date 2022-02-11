package ir.amozkade.advancedAsisstiveTouche.mvvm.exception

import ir.amozkade.advancedAsisstiveTouche.helper.api.DataState
import ir.amozkade.advancedAsisstiveTouche.mvvm.exception.di.ExceptionDao
import ir.amozkade.advancedAsisstiveTouche.mvvm.exception.di.ExceptionRetrofit
import ir.amozkade.advancedAsisstiveTouche.mvvm.exception.utils.ExceptionResponse
import ir.amozkade.advancedAsisstiveTouche.mvvm.preferenceActivity.SettingRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

class ExceptionRepository @Inject constructor(private val exceptionDao: ExceptionDao ,private val settingRepository: SettingRepository, private val exceptionRetrofit: ExceptionRetrofit) {

    fun saveException(e: Exception) {
        CoroutineScope(Dispatchers.Default).launch {
            exceptionDao.insert(ExceptionHandler.getCustomExceptionModel(e,settingRepository))
        }
    }

    suspend fun logExceptions(): Flow<DataState<ExceptionResponse>> = flow {
        emit(DataState.Loading)
        val exceptions = exceptionDao.getAllExceptions()
        if(exceptions.count() == 0) {
            emit(DataState.Success(ExceptionResponse.Success))
            return@flow
        }
        exceptionRetrofit.logExceptions(exceptions)
        exceptionDao.deleteAll()
        emit(DataState.Success(ExceptionResponse.Success))
    }
}