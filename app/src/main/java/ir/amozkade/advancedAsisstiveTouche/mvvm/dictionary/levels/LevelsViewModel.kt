package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.levels


import javax.inject.Inject
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.amozkade.advancedAsisstiveTouche.helper.api.DataState

import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.leitners.models.Leitner
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.levels.questionAnswer.QuestionAnswerRepository
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.levels.repository.LevelRepository
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.levels.utils.LevelResponse
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.levels.utils.LevelStateEvent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.flow.collect

@HiltViewModel
class LevelsViewModel @Inject constructor(private val levelRepository: LevelRepository,
                                                   private val questionAnswerRepository: QuestionAnswerRepository,
                                                   val exceptionObserver: MutableLiveData<Throwable>) : ViewModel() {

    private var model: LevelsModel = LevelsModel()
    private val _response: MutableLiveData<DataState<LevelResponse>> = MutableLiveData()
    val response: LiveData<DataState<LevelResponse>> = _response


    fun setState(event: LevelStateEvent) {
        val handler = CoroutineExceptionHandler { _, exception ->
            exceptionObserver.postValue(exception)
        }
        viewModelScope.launch(handler) {
            supervisorScope {

                when (event) {
                    is LevelStateEvent.GetAllLevelsInLeitner -> {
                        questionAnswerRepository.getCompletedCountInLevel(model.leitner?.id).collect {
                            if (it is DataState.Success && it.data is LevelResponse.CompletedCounts){
                                model.completedCount = it.data.completedCount
                            }
                        }

                        levelRepository.getAllLevelsInLeitner(event.leitner).collect {
                            _response.postValue(it)
                            (it as? DataState.Success)?.let { dataState ->
                                (dataState.data as?  LevelResponse.Levels)?.let { it->
                                    val uncompletedCount  = it.levels.map { level -> level.questionCountInLevel }.sum()
                                    model.unCompletedCount = uncompletedCount
                                }
                            }
                        }
                    }
                    is LevelStateEvent.UpdateTimeOfLevel->{
                        levelRepository.updateTimeOfLevel(event.level.levelId,event.day)
                        model.time = event.day.toString()
                    }
                }
            }
        }
    }

    fun setLeitner(leitner : Leitner) {
        model.leitner = leitner
    }

    fun getModel(): LevelsModel {
        return model
    }
}