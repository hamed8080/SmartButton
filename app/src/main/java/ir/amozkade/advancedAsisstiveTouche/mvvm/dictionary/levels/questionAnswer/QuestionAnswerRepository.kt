package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.levels.questionAnswer

import androidx.lifecycle.MutableLiveData
import ir.amozkade.advancedAsisstiveTouche.helper.api.DataState
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.addOrEditQuestion.utils.AddOrEditQuestionResponse
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.addOrEditQuestion.utils.EditedQuestionAnswer
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.leitnerQuestionListActivity.utils.LeitnerQuestionListResponse
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.levels.repository.LevelDao
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.levels.utils.LevelResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class QuestionAnswerRepository @Inject constructor(private val questionAnswerDao: QuestionAnswerDao, private val levelDao: LevelDao) {
    var questionAnswers: MutableLiveData<List<QuestionAnswer>> = MutableLiveData()

    suspend fun insert(questionAnswer: QuestionAnswer): Flow<DataState<AddOrEditQuestionResponse>> = flow {
        emit(DataState.Loading)
        questionAnswerDao.insert(questionAnswer)
        emit(DataState.Success(AddOrEditQuestionResponse.SuccessAdded(questionAnswer)))
    }

    suspend fun edit(editedQuestionAnswer: EditedQuestionAnswer) = flow<DataState<AddOrEditQuestionResponse>> {
        emit(DataState.Loading)
        val entity = questionAnswerDao.getQuestionAnswer(editedQuestionAnswer.original.question, editedQuestionAnswer.original.leitnerId)
        questionAnswerDao.delete(entity)
        editedQuestionAnswer.edited.completed = entity.completed
        editedQuestionAnswer.edited.favorite = entity.favorite
        questionAnswerDao.insert(editedQuestionAnswer.edited)
        emit(DataState.Success(AddOrEditQuestionResponse.SuccessEdited(editedQuestionAnswer.edited)))
    }

    suspend fun update(questionAnswer: QuestionAnswer) {
        questionAnswerDao.update(questionAnswer)
    }

    suspend fun delete(questionAnswer: QuestionAnswer) {
        questionAnswerDao.delete(questionAnswer)
    }

    suspend fun toggleFavorite(questionAnswer: QuestionAnswer){
        questionAnswer.favorite = !questionAnswer.favorite
        questionAnswer.favoriteDate = if (questionAnswer.favorite) java.util.Date() else null
        questionAnswerDao.update(questionAnswer)
    }

    suspend fun backToList(questionAnswer: QuestionAnswer): QuestionAnswer {
        questionAnswer.completed = false
        questionAnswer.passedTime = null
        val firstLevelId = questionAnswerDao.getLevelIdForLevelInLeitner(1, questionAnswer.leitnerId)
        questionAnswer.levelId = firstLevelId
        questionAnswerDao.update(questionAnswer)
        return questionAnswer
    }

    suspend fun moveToLeitner(questionAnswer: QuestionAnswer, leitnerId: Int) {
        val firstLevelId = questionAnswerDao.getLevelIdForLevelInLeitner(1, leitnerId)
        val entity = questionAnswerDao.getQuestionAnswer(questionAnswer.question, questionAnswer.leitnerId)
        questionAnswerDao.delete(entity)
        questionAnswer.leitnerId = leitnerId
        questionAnswer.levelId = firstLevelId
        questionAnswer.completed = false
        questionAnswerDao.insert(questionAnswer)
    }

    suspend fun getNextLevelIdFor(question: String, leitnerId: Int): Int {
        val currentLevel = questionAnswerDao.currentLevel(question)
        val newLevel = currentLevel + 1
        //newLevelId returned
        return questionAnswerDao.getLevelIdForLevelInLeitner(newLevel, leitnerId)
    }

    suspend fun isLastLevel(question: String): Boolean {
        val currentLevel = questionAnswerDao.currentLevel(question)
        return currentLevel == 13
    }

    suspend fun getAllQuestionAnswerInLeitner(leitnerId: Int) = flow<DataState<LeitnerQuestionListResponse>> {
        emit(DataState.Loading)
        val questionAnswers = questionAnswerDao.getAllQuestionsInLeitner(leitnerId)
        val levels = levelDao.getAllSimpleLevels(leitnerId)
        emit(DataState.Success(LeitnerQuestionListResponse.AllQuestions(questionAnswers, levels)))
    }

    suspend fun getCompletedCountInLevel(leitnerId: Int?) = flow<DataState<LevelResponse>> {
        emit(DataState.Loading)
        leitnerId?.let {
            val count = questionAnswerDao.getCompletedCount(leitnerId)
            emit(DataState.Success(LevelResponse.CompletedCounts(count)))
        }
    }
}
