package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.leitners.di

import androidx.room.*
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.leitners.models.Leitner

@Dao
interface LeitnerDao {

    @Query("SELECT  *  FROM Leitner")
    suspend fun getAll(): List<Leitner>

    @Query("SELECT  *  FROM Leitner WHERE Id = :leitnerId LIMIT 1")
    suspend fun getLeitnerById(leitnerId: Int): Leitner

    @Insert
    suspend fun insert(leitner: Leitner):Long

    @Insert
    suspend fun insert(leitners: List<Leitner>)

    @Update
    suspend fun update(leitner : Leitner)

    @Delete
    suspend fun delete(leitner: Leitner)

    @Query("SELECT * From Leitner WHERE name = :name LIMIT 1")
    suspend fun checkDuplicate(name:String):Leitner?

    @Update
    suspend fun setBackToTopEnable(leitner: Leitner)

    @Update
    suspend fun setShowDefinition(leitner: Leitner)
}