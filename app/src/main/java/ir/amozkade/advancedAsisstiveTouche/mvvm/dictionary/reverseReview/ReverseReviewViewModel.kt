package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.reverseReview

import android.content.Context
import android.os.Build
import android.speech.tts.TextToSpeech
import android.text.Editable
import androidx.appcompat.app.AppCompatActivity
import javax.inject.Inject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.amozkade.advancedAsisstiveTouche.AppDatabase
import ir.amozkade.advancedAsisstiveTouche.AppDatabasePath
import ir.amozkade.advancedAsisstiveTouche.helper.api.DataState
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.levels.questionAnswer.QuestionAnswer
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.levels.questionAnswer.QuestionAnswerRepository
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.manageDictionaries.di.DictionaryDao
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.review.utils.ReviewResponse
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.translator.TranslateResult
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.buttons.translate.CustomTranslateLanguage
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.buttons.translate.TranslateViewModel
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.leitners.LeitnerRepository
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.reverseReview.utils.ReverseReviewStateEvent
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.review.ReviewRepository
import ir.amozkade.advancedAsisstiveTouche.mvvm.preferenceActivity.SettingRepository
import ir.mobitrain.applicationcore.helper.CTextWatcher
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import java.util.*

@HiltViewModel
class ReverseReviewViewModel @Inject constructor(
    private val questionAnswerRepository: QuestionAnswerRepository,
    private val reviewRepository: ReviewRepository,
    @ApplicationContext private val context: Context,
    private val dictionaryDao: DictionaryDao,
    @AppDatabasePath private val databasePath: String,
    private val googleTranslatorViewModel: TranslateViewModel,
    private val leitnerRepository: LeitnerRepository,
    val exceptionObserver: MutableLiveData<Throwable>,
    private val settingRepository: SettingRepository,
    private val tts: TextToSpeech
) : ViewModel() {

    private var model: ReverseReviewModel = ReverseReviewModel()
    private lateinit var questionAnswers: ArrayList<QuestionAnswer>

    private val _response: MutableLiveData<DataState<ReviewResponse>> = MutableLiveData()
    val response: LiveData<DataState<ReviewResponse>> = _response


    var questionWriteTextWatcher: CTextWatcher = object : CTextWatcher() {
        override fun afterTextChanged(s: Editable) {
            super.afterTextChanged(s)
            model.typedReverse = s.toString()
        }
    }

    fun setState(event: ReverseReviewStateEvent) {
        val handler = CoroutineExceptionHandler { _, exception ->
            exceptionObserver.postValue(exception)
        }
        viewModelScope.launch(handler) {
            supervisorScope {

                when (event) {
                    is ReverseReviewStateEvent.Init -> {
                        init(event.leitnerId, event.level)
                    }

                    is ReverseReviewStateEvent.Guessed -> {
                        guessed()
                    }

                    is ReverseReviewStateEvent.Failed -> {
                        failedGuess()
                    }

                    is ReverseReviewStateEvent.Speak -> {
                        speakText(model.question)
                    }

                    is ReverseReviewStateEvent.DeleteQuestion -> {
                        deleteQuestionAnswer()
                    }
                    ReverseReviewStateEvent.CheckReverse -> {
                        checkReverse()
                    }
                }
            }
        }
    }

    private fun checkReverse() {
        if(model.typedReverse.equals(model.question, ignoreCase = true)){
            viewModelScope.launch {
                guessed()
            }
        }else{
            failedGuess()
        }
    }

    private suspend fun deleteQuestionAnswer() {
        questionAnswers.firstOrNull()?.let {
            questionAnswerRepository.delete(it)
            questionAnswers.removeAt(0)
            setupNextQuestion()
        }
    }

    suspend fun init(leitnerId: Int, level: Int) {
        model.leitner = leitnerRepository.getLeitnerWithId(leitnerId)
        model.level = level
        questionAnswers = reviewRepository.getLevelQuestionAnswers(model.level, model.leitner.id)
        withContext(Main) {
            setupNextQuestion()
            model.totalItemsInLevel = questionAnswers.size
        }
        tts.language = Locale.US
        initGoogleTranslatorObserver()
    }

    fun getModel(): ReverseReviewModel {
        return model
    }

    private fun failedGuess() {
        _response.postValue(DataState.Success(ReviewResponse.ResetView))
        if (questionAnswers.size <= 0) {
            showFinishDialog()
            return
        }
        model.failedItemInLevel += 1
        backToTopLevelIfEnabled(questionAnswers.first())
        questionAnswers.removeAt(0)
        setupNextQuestion()
    }

    private fun backToTopLevelIfEnabled(questionAnswer: QuestionAnswer) {
        GlobalScope.launch(IO) {
            reviewRepository.backToTopLevel(questionAnswer, model.leitner)
        }
    }

    private fun showFinishDialog() {
        _response.postValue(DataState.Success(ReviewResponse.Completed))
    }

    private suspend fun guessed() {
        _response.postValue(DataState.Success(ReviewResponse.ResetView))
        if (questionAnswers.size <= 0) {
            showFinishDialog()
            return
        }
        questionAnswers.firstOrNull()?.let {
            if(questionAnswerRepository.isLastLevel(it.question)){
                it.completed = true
                questionAnswerRepository.update(it)
            }else{
                val newLevelId = questionAnswerRepository.getNextLevelIdFor(it.question, model.leitner.id)
                it.levelId = newLevelId
                it.passedTime = Date()
                questionAnswerRepository.update(it)
            }
            questionAnswers.removeAt(0)
            model.passedItemsInLevel += 1
        }
        setupNextQuestion()
    }

    private fun speakText(text: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        } else {
            @Suppress("DEPRECATION")
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null)
        }
    }

    private fun setupNextQuestion() {
        if (questionAnswers.isNullOrEmpty()) {
            model.question = ""
            model.answer = ""
            showFinishDialog()
            return
        } else {
            val questionAnswer = questionAnswers.first()
            model.question = questionAnswer.question
            //if answer not null mean user filled manually
            model.manual = questionAnswer.answer != null
            model.questionAnswer = questionAnswer
            GlobalScope.launch(IO) {
                when {
                    questionAnswer.answer != null -> {
                        setUserAnswerIfExist(questionAnswer)
                    }
                    dictionaryDao.countOfAllDictionary() > 0 -> {
                        startTranslate(model.question)
                    }
                    else -> {
                        googleTranslatorViewModel.sourceText.postValue(model.question)
                    }
                }
            }
        }
        model.answerVisible = false
    }

    private fun initGoogleTranslatorObserver() {
        googleTranslatorViewModel.sourceLang.value = CustomTranslateLanguage(settingRepository.getCashedModel().sourceLang ?: "en")
        googleTranslatorViewModel.targetLang.value = CustomTranslateLanguage(settingRepository.getCashedModel().destLang ?: "fa")
        googleTranslatorViewModel.translatedText.observe(context as AppCompatActivity) {
            if (!it.result.isNullOrEmpty()) {
                _response.postValue(
                    DataState.Success(
                        ReviewResponse.MeansWithDictName(
                            listOf(
                                TranslateResult("Google", it.result)
                            )
                        )
                    )
                )
            }
        }
    }

    private fun setUserAnswerIfExist(question: QuestionAnswer) {
        model.answer = question.answer
    }

    private suspend fun startTranslate(text: String) = withContext(IO) {
        val meansWithDictName = dictionaryDao.getAll().map {
            val mean = translate(text, it.dbNameWithoutZipExtension)
            TranslateResult(it.name, mean)
        }
        _response.postValue(DataState.Success(ReviewResponse.MeansWithDictName(meansWithDictName)))
    }

    private fun translate(text: String, dbName: String): String? {
        val blob = AppDatabase.openDicFileInDataFolder(context, dbName, databasePath).dictionaryWordsDao().getMean(text)?.mean
        blob?.let {
            return String(blob, Charsets.UTF_8)
        }
        return null
    }

    fun updateQuestionAnswer(questionAnswer: QuestionAnswer) {
        if (questionAnswers.contains(getModel().questionAnswer)) {
            questionAnswers.indexOf(getModel().questionAnswer).let { index ->
                questionAnswers[index].question = questionAnswer.question
                questionAnswers[index].answer = questionAnswer.answer
            }
        }
       getModel().question = questionAnswer.question
        if (questionAnswer.answer != null) {
            getModel().answer = questionAnswer.answer
        }
    }
}