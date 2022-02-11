package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.review

import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.leitners.models.Leitner
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.levels.models.Level
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.levels.questionAnswer.QuestionAnswer
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.levels.questionAnswer.QuestionAnswerDao
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.levels.repository.LevelDao
import javax.inject.Inject
import kotlin.collections.ArrayList

class ReviewRepository @Inject constructor(
        private val questionAnswerDao: QuestionAnswerDao,
        private val levelDao: LevelDao
) {


    private suspend fun getLevel(level: Int, leitnerId: Int): Level {
        return levelDao.getLevel(level, leitnerId)
    }

    suspend fun getLevelQuestionAnswers(level: Int, leitnerId: Int): ArrayList<QuestionAnswer> {
        val levelId = getLevel(level, leitnerId).id
        val questionAnswers = questionAnswerDao.getReviewableQuestionAnswerForLevelInLeitner(levelId, leitnerId)
        return ArrayList(questionAnswers.shuffled())
    }

    suspend fun backToTopLevel(questionAnswer: QuestionAnswer, leitner: Leitner) {
        if (leitner.isBackToTopEnable) {
            val firstLevelId = levelDao.getFirstLevelIdInLeitner(leitner.id)
            questionAnswer.levelId = firstLevelId
            questionAnswer.passedTime = null
            questionAnswerDao.update(questionAnswer)
        }
    }
}