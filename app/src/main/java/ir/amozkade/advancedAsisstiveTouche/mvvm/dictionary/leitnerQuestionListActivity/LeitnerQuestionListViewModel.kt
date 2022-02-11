package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.leitnerQuestionListActivity


import android.text.Editable
import javax.inject.Inject
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.amozkade.advancedAsisstiveTouche.helper.api.DataState
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.leitnerQuestionListActivity.utils.LeitnerQuestionListResponse
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.leitnerQuestionListActivity.utils.LeitnerQuestionListStateEvent
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.leitners.di.LeitnerDao
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.leitners.models.Leitner
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.levels.models.Level
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.levels.questionAnswer.QuestionAnswer
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.levels.questionAnswer.QuestionAnswerRepository
import ir.mobitrain.applicationcore.helper.CTextWatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.flow.collect
import java.util.*

@HiltViewModel
class LeitnerQuestionListViewModel @Inject constructor(
        private val questionAnswerRepository: QuestionAnswerRepository,
        private val leitnerDao: LeitnerDao,
        val exceptionObserver: MutableLiveData<Throwable>
) : ViewModel() {


    private var questionAnswers = arrayListOf<QuestionAnswer>()
    private var levels = arrayListOf<Level>()

    private val _response: MutableLiveData<DataState<LeitnerQuestionListResponse>> = MutableLiveData()
    val response: LiveData<DataState<LeitnerQuestionListResponse>> = _response

    var searchTextWatcher: CTextWatcher = object : CTextWatcher() {
        override fun afterTextChanged(s: Editable) {
            super.afterTextChanged(s)
            setState(LeitnerQuestionListStateEvent.Search(s.toString()))
        }
    }

    fun setState(event: LeitnerQuestionListStateEvent) {
        val handler = CoroutineExceptionHandler { _, exception ->
            exceptionObserver.postValue(exception)
        }
        viewModelScope.launch(handler) {
            supervisorScope {

                when (event) {
                    is LeitnerQuestionListStateEvent.GetAllLeitnerQuestions -> {
                        questionAnswerRepository.getAllQuestionAnswerInLeitner(event.leitnerId).collect {
                            questionAnswers.clear()
                            if (it is DataState.Success && it.data is LeitnerQuestionListResponse.AllQuestions) {
                                questionAnswers.addAll(it.data.questionAnswers)
                                levels.addAll(it.data.levels)
                            }
                            _response.postValue(it)
                        }
                    }

                    is LeitnerQuestionListStateEvent.Search -> {
                        if (event.query.isEmpty()) {
                            _response.postValue(DataState.Success(LeitnerQuestionListResponse.AllQuestions(questionAnswers, levels)))
                        } else {
                            val results = questionAnswers.filter { it.question.lowercase(Locale.ROOT)
                                .contains(event.query.lowercase(Locale.ROOT)) || it.answer?.lowercase(Locale.ROOT)
                                ?.contains(event.query.lowercase(Locale.ROOT)) == true }
                            _response.postValue(DataState.Success(LeitnerQuestionListResponse.AllQuestions(results, levels)))
                        }
                    }

                    is LeitnerQuestionListStateEvent.Delete -> {
                        questionAnswerRepository.delete(event.questionAnswer)
                        questionAnswers.remove(event.questionAnswer)
                    }

                    is LeitnerQuestionListStateEvent.BackToList -> {
                        val questionAnswer = questionAnswerRepository.backToList(event.questionAnswer)
                        _response.postValue(DataState.Success(LeitnerQuestionListResponse.QuestionAnswerUpdated(questionAnswer)))
                    }

                    is LeitnerQuestionListStateEvent.MoveToLeitner -> {
                        questionAnswerRepository.moveToLeitner(event.questionAnswer , event.leitnerId)
                        _response.postValue(DataState.Success(LeitnerQuestionListResponse.Removed(event.questionAnswer)))
                    }

                    is LeitnerQuestionListStateEvent.Fav -> {
                        questionAnswerRepository.toggleFavorite(event.questionAnswer)
                    }

                    is LeitnerQuestionListStateEvent.Sort -> {

                        val sorted: List<QuestionAnswer>
                        when (event.checkedId) {
                            ir.amozkade.advancedAsisstiveTouche.R.id.chpDate -> {
                                sorted = questionAnswers.toList().sortedByDescending { it.passedTime }
                            }

                            ir.amozkade.advancedAsisstiveTouche.R.id.chpLevel -> {
                                sorted = questionAnswers.toList().sortedBy { it.levelId }
                            }

                            ir.amozkade.advancedAsisstiveTouche.R.id.chpAlphabet -> {
                                sorted = questionAnswers.toList().sortedBy { it.question.trim() }
                            }

                            ir.amozkade.advancedAsisstiveTouche.R.id.chpFav -> {
                                sorted = questionAnswers.toList().sortedBy { it.favorite }.sortedByDescending { it.favoriteDate }
                            }

                            else -> {
                                sorted = questionAnswers.toList().sortedBy { it.levelId }
                            }
                        }

                        questionAnswers.clear()
                        questionAnswers.addAll(sorted)
                        _response.postValue(DataState.Success(LeitnerQuestionListResponse.AllQuestions(questionAnswers, levels)))
                    }
                }
            }
        }
    }

    suspend fun getAllLeitners():List<Leitner>{
       return leitnerDao.getAll()
    }
}