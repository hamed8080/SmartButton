package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.review.di

import androidx.room.Dao
import androidx.room.Query
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.review.models.Words

@Dao
interface DictionaryWordsDao {
    @Query("SELECT  *  FROM Words")
    fun getAll(): List<Words>

    @Query("SELECT  *  FROM Words WHERE word=:word COLLATE NOCASE LIMIT 1")
    fun getMean(word:String): Words?
}