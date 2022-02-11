package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.manageDictionaries.di

import androidx.room.*
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.manageDictionaries.Dictionary

@Dao
interface DictionaryDao{
    @Query("SELECT  *  FROM Dictionary")
    suspend fun getAll(): List<Dictionary>

    @Insert(onConflict= OnConflictStrategy.REPLACE)
    suspend fun insert(dictionary: Dictionary)

    @Delete
    suspend fun delete(dictionary: Dictionary)

    @Query("SELECT COUNT(*) FROM Dictionary")
    suspend fun countOfAllDictionary():Int
}