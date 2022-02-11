package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.levels.repository

import androidx.room.*
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.leitners.models.LeitnerLevels
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.levels.models.Level

@Dao
interface LevelDao{
    @Query("SELECT  *  FROM Level")
    suspend fun getAll(): List<Level>

    @Query("SELECT  *  FROM Level WHERE leitnerId=:leitnerId AND level=:level LIMIT 1")
    suspend fun getLevel( level:Int, leitnerId: Int ): Level

    @Insert(onConflict= OnConflictStrategy.IGNORE)
    suspend fun insert(level: Level)

    @Insert(onConflict= OnConflictStrategy.IGNORE)
    suspend fun insert(levels: List<Level>)

    @Update
    suspend fun update(level: Level)

    @Delete
    suspend fun delete(level: Level)

    @Transaction @Query("SELECT id , level FROM LEVEL WHERE id =:levelId AND leitnerId=:leitnerId")
    suspend fun getLevelQuestionAnswers(levelId:Int , leitnerId:Int): LevelQuestionAnswers?

    @Query("SELECT id FROM LEVEL WHERE leitnerId=:leitnerId AND level = 1 ORDER BY level Asc")
    suspend fun getFirstLevelIdInLeitner(leitnerId: Int):Int

    @Query("SELECT * FROM LEVEL WHERE id=:levelId Limit 1")
    suspend fun getLevelWithLevelId(levelId: Int): Level

    @Query("""SELECT
                        Leitner.id,
                        Leitner.name,
                        lv.Id AS levelId,
                        lv.level,
                        lv.time,
                        COUNT(qa.question) AS questionCountInLevel,
                        (
                            SELECT
                                SUM(DATETIME('now') > DATETIME(passedTime / 1000, 'unixepoch', '+' || lv.time || ' days'))
                            FROM
                                QuestionAnswer
                            WHERE
                                passedTime IS NOT NULL 
                                AND completed = 0
                                AND levelId = qa.levelId
                                GROUP BY levelId
                        ) AS questionReviewableCountInLevel
                    FROM
                        Leitner
                        INNER JOIN Level AS lv ON Leitner.id = lv.leitnerId
                        LEFT JOIN QuestionAnswer AS qa ON lv.id = qa.levelId
                    WHERE
                        Leitner.id =:leitnerId AND completed = 0 
                    GROUP BY
                        lv.level""")
    suspend fun getAllLevels(leitnerId: Int): List<LeitnerLevels>

    @Query("DELETE FROM LEVEL WHERE leitnerId=:leitnerId")
    suspend fun deleteLevelsForLeitner(leitnerId:Int)

    @Query("SELECT * FROM LEVEL WHERE leitnerId=:leitnerId")
    suspend fun getAllSimpleLevels(leitnerId: Int): List<Level>
}