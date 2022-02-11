package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.addOrEditQuestion


import android.content.Context
import android.text.Editable
import javax.inject.Inject
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.amozkade.advancedAsisstiveTouche.helper.InAppException
import ir.amozkade.advancedAsisstiveTouche.helper.api.DataState

import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.addOrEditQuestion.utils.AddOrEditQuestionResponse
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.addOrEditQuestion.utils.AddOrEditQuestionStateEvent
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.levels.repository.LevelRepository
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.levels.questionAnswer.QuestionAnswerRepository
import ir.mobitrain.applicationcore.helper.CTextWatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import ir.amozkade.advancedAsisstiveTouche.R
import kotlinx.coroutines.flow.collect

@HiltViewModel
class AddOrEditQuestionViewModel @Inject constructor(
        private val levelRepository: LevelRepository,
        private val questionAnswerRepository: QuestionAnswerRepository,
        @ApplicationContext private val context: Context,
        val exceptionObserver: MutableLiveData<Throwable>
) : ViewModel() {

    var leitnerId: Int = 0
    private var model: AddNewQuestionModel = AddNewQuestionModel()

    val handler = CoroutineExceptionHandler { _, exception ->
        exceptionObserver.postValue(exception)
    }

    var questionTextWatcher: CTextWatcher = object : CTextWatcher() {
        override fun afterTextChanged(s: Editable) {
            super.afterTextChanged(s)
            model.question = s.toString()
        }
    }

    var answerTextWatcher: CTextWatcher = object : CTextWatcher() {
        override fun afterTextChanged(s: Editable) {
            super.afterTextChanged(s)
            model.answer = s.toString()
        }
    }

    private val _response: MutableLiveData<DataState<AddOrEditQuestionResponse>> = MutableLiveData()
    val response: LiveData<DataState<AddOrEditQuestionResponse>> = _response


    fun setState(event: AddOrEditQuestionStateEvent) {

        viewModelScope.launch(handler) {
            supervisorScope {
                when (event) {
                    is AddOrEditQuestionStateEvent.Save -> {
                        save()
                    }
                }
            }
        }
    }

    private suspend fun save() {
        if (model.question.isNullOrEmpty()) {
            exceptionObserver.postValue(InAppException(context.getString(R.string.question_cant_null), context.getString(R.string.enter_question), null))
        } else if (model.answer.isNullOrEmpty() && model.isManual) {
            exceptionObserver.postValue(InAppException(context.getString(R.string.answer_cant_null), context.getString(R.string.enter_answer), null))
        } else {

            if (model.isInEditMode) {
                model.getEditedQuestionAnswer()?.let { entity ->
                    questionAnswerRepository.edit(entity).collect {
                        _response.postValue(it)
                    }
                }
            } else {
                val levelId = levelRepository.firstLevelIdForLeitner(leitnerId)
                model.getNewQuestionAnswer(levelId, leitnerId)?.let { entity ->
                    questionAnswerRepository.insert(entity).collect {
                        _response.postValue(it)
                    }
                }
            }
        }
    }

    fun getModel(): AddNewQuestionModel {
        return model
    }
}