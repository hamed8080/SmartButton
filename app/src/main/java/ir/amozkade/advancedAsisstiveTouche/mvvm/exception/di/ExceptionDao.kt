package ir.amozkade.advancedAsisstiveTouche.mvvm.exception.di

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ir.amozkade.advancedAsisstiveTouche.mvvm.exception.CustomException

@Dao
interface ExceptionDao{
    @Query("SELECT  *  FROM CustomException")
    suspend fun getAllExceptions(): List<CustomException>

    @Query("DELETE FROM CustomException")
    suspend fun deleteAll()

    @Insert
    suspend fun insert(logger: CustomException)
}