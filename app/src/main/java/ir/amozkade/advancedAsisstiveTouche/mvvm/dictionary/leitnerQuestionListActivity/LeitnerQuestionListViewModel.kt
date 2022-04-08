package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.leitnerQuestionListActivity


import android.os.Build
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
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
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.collect
import java.util.*

@HiltViewModel
class LeitnerQuestionListViewModel @Inject constructor(
        private val questionAnswerRepository: QuestionAnswerRepository,
        private val leitnerDao: LeitnerDao,
        val exceptionObserver: MutableLiveData<Throwable>,
        private val tts: TextToSpeech
) : ViewModel() {


    var questionAnswers = arrayListOf<QuestionAnswer>()
    var levels = arrayListOf<Level>()

    private val _response: MutableLiveData<DataState<LeitnerQuestionListResponse>> = MutableLiveData()
    val response: LiveData<DataState<LeitnerQuestionListResponse>> = _response

    var isPlaying = false
    private val reviewQuestions = arrayListOf<QuestionAnswer>()
    private var reviewJob:Job? = null
    var repeatCount = 1
    var currentWordRepeatCount = 1

    var searchTextWatcher: CTextWatcher = object : CTextWatcher() {
        override fun afterTextChanged(s: Editable) {
            super.afterTextChanged(s)
            setState(LeitnerQuestionListStateEvent.Search(s.toString()))
        }
    }

    init {
        tts.language = Locale.US
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
                            reviewQuestions.clear()
                            if (it is DataState.Success && it.data is LeitnerQuestionListResponse.AllQuestions) {
                                questionAnswers.addAll(it.data.questionAnswers)
                                reviewQuestions.addAll(it.data.questionAnswers)
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

                    is LeitnerQuestionListStateEvent.Edited ->{
                        questionAnswers.first { it.question == event.questionAnswer?.question }.let {
                            it.answer = event.questionAnswer?.answer
                            _response.postValue(DataState.Success(LeitnerQuestionListResponse.QuestionAnswerUpdated(it)))
                        }
                    }

                    is LeitnerQuestionListStateEvent.Add ->{
                        event.questionAnswer?.let {
                            questionAnswers.add(it)
                            _response.postValue(DataState.Success(LeitnerQuestionListResponse.Added(it)))
                        }
                    }

                    is LeitnerQuestionListStateEvent.PlayOrPause ->{
                        playOrPauseReview(event.play)
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
                        reviewQuestions.clear()
                        reviewQuestions.addAll(sorted)
                        _response.postValue(DataState.Success(LeitnerQuestionListResponse.AllQuestions(questionAnswers, levels)))
                    }
                }
            }
        }
    }

    private fun playOrPauseReview(play: Boolean) {
        isPlaying = play
        if (play){
            playOrResumeReview()
        }else{
            pauseReview()
        }
    }

    fun pauseReview() {
        reviewJob?.cancel()
        reviewJob = null
    }

    private fun playOrResumeReview() {
        reviewJob = CoroutineScope(Main).launch {
            delay(3500)
            reviewQuestions.firstOrNull()?.let {
                tts.setOnUtteranceProgressListener(object :UtteranceProgressListener(){
                    override fun onStart(utteranceId: String?) {
                    }

                    override fun onDone(utteranceId: String?) {
                        if (isPlaying){
                            playOrResumeReview()
                        }
                    }

                    override fun onError(utteranceId: String?) {
                    }
                })
                speakText(it.question)
                _response.postValue(DataState.Success(LeitnerQuestionListResponse.ReviewingQuestion(it,"${reviewQuestions.size}/${questionAnswers.size}" )))
                if (currentWordRepeatCount >= repeatCount ){
                    reviewQuestions.removeAt(0)
                    currentWordRepeatCount = 1
                }else{
                    currentWordRepeatCount += 1
                }
            }
        }
        reviewJob?.start()
    }

    private fun speakText(text: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, TextToSpeech.ACTION_TTS_QUEUE_PROCESSING_COMPLETED)
        } else {
            @Suppress("DEPRECATION")
            tts.speak(text, TextToSpeech.QUEUE_ADD, null)
        }
    }

    suspend fun getAllLeitners():List<Leitner>{
       return leitnerDao.getAll()
    }

    fun increaseRepeatCount() {
        if (repeatCount <= 6){
            repeatCount += 1
        }else{
            repeatCount = 1
        }
        _response.postValue(DataState.Success(LeitnerQuestionListResponse.RepeatCount(repeatCount)))
    }
}