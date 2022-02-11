package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.levels.questionAnswer

import androidx.room.*

@Dao
interface QuestionAnswerDao {
    @Query("SELECT  *  FROM QuestionAnswer")
    suspend fun getAll(): List<QuestionAnswer>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(questionAnswer: QuestionAnswer)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(questions: List<QuestionAnswer>)

    @Update
    suspend fun update(questionAnswer : QuestionAnswer)

    @Delete
    suspend fun delete(questionAnswer : QuestionAnswer)

    @Query("""SELECT LEVEL.level FROM QuestionAnswer
                        INNER JOIN LEVEL ON QuestionAnswer.levelId = LEVEL.id
                            WHERE question=:question LIMIT 1
    """)
    suspend fun currentLevel(question: String): Int

    @Query("""SELECT LEVEL.id FROM Leitner 
        INNER JOIN LEVEL ON Leitner.id = LEVEL.leitnerId
        WHERE Leitner.id = :leitnerId AND LEVEL.level =:level LIMIT 1
    """)
    suspend fun getLevelIdForLevelInLeitner(level: Int, leitnerId: Int): Int

    //    check passed time is null for level 1 if null user can see question other levels must filled passedTime
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("""SELECT *,(select time from level where id =:levelId) as time  FROM  QuestionAnswer
        WHERE (levelId =:levelId AND leitnerId=:leitnerId and  (DATETIME('now') >   DATETIME(passedTime / 1000, 'unixepoch', '+' || time || ' days' ) OR passedTime IS NULL) AND completed = 0 )""")
    suspend fun getReviewableQuestionAnswerForLevelInLeitner(levelId: Int, leitnerId: Int): List<QuestionAnswer>

    @Query("SELECT * FROM QuestionAnswer WHERE leitnerId=:leitnerId order by LevelId ASC, Completed ASC")
    suspend fun getAllQuestionsInLeitner(leitnerId: Int):List<QuestionAnswer>

    @Query("SELECT * FROM QuestionAnswer WHERE question = :question AND leitnerId = :leitnerId LIMIT 1")
    suspend fun getQuestionAnswer(question:String , leitnerId: Int):QuestionAnswer

    @Query("SELECT Count(*) FROM QuestionAnswer WHERE completed = 1 AND leitnerId = :leitnerId")
    suspend fun getCompletedCount(leitnerId: Int):Int
}