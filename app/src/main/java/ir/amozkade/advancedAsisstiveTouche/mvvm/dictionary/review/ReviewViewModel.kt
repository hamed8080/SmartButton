package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.review

import android.content.Context
import android.os.Build
import android.speech.tts.TextToSpeech
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
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.review.di.WordRetrofit
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.review.models.Synonym
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.review.utils.ReviewResponse
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.review.utils.ReviewStateEvent
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.translator.TranslateResult
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.buttons.translate.CustomTranslateLanguage
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.buttons.translate.TranslateViewModel
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.leitners.LeitnerRepository
import ir.amozkade.advancedAsisstiveTouche.mvvm.preferenceActivity.SettingRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import java.util.*

@HiltViewModel
class ReviewViewModel @Inject constructor(
    private val questionAnswerRepository: QuestionAnswerRepository,
    private val reviewRepository: ReviewRepository,
    @ApplicationContext private val context: Context,
    private val dictionaryDao: DictionaryDao,
    private val wordRetrofit: WordRetrofit,
    @AppDatabasePath private val databasePath: String,
    private val googleTranslatorViewModel: TranslateViewModel,
    private val leitnerRepository: LeitnerRepository,
    val exceptionObserver: MutableLiveData<Throwable>,
    private val settingRepository: SettingRepository,
    private val tts: TextToSpeech
) : ViewModel() {

    private var model: ReviewModel = ReviewModel()
    private lateinit var questionAnswers: ArrayList<QuestionAnswer>

    //    private val availableModels = MutableLiveData<List<String>>()
    val definitions = MutableLiveData<List<String>>()
    val synonyms = MutableLiveData<List<Synonym>>()
    private val _response: MutableLiveData<DataState<ReviewResponse>> = MutableLiveData()
    val response: LiveData<DataState<ReviewResponse>> = _response


    fun setState(event: ReviewStateEvent) {
        val handler = CoroutineExceptionHandler { _, exception ->
            exceptionObserver.postValue(exception)
        }
        viewModelScope.launch(handler) {
            supervisorScope {

                when (event) {
                    is ReviewStateEvent.Init -> {
                        init(event.leitnerId, event.level)
                    }

                    is ReviewStateEvent.Guessed -> {
                        guessed()
                    }

                    is ReviewStateEvent.Failed -> {
                        failedGuess()
                    }

                    is ReviewStateEvent.Speak -> {
                        speakText(model.question)
                    }

                    is ReviewStateEvent.DeleteQuestion -> {
                        deleteQuestionAnswer()
                    }

                    is ReviewStateEvent.Favorite -> {
                        toggleFavorite()
                    }
                }
            }
        }
    }

    private suspend fun deleteQuestionAnswer() {
        questionAnswers.firstOrNull()?.let {
            questionAnswerRepository.delete(it)
            questionAnswers.removeAt(0)
            setupNextQuestion()
        }
    }

    private suspend fun toggleFavorite(){
        questionAnswers.firstOrNull()?.let {
            model.favorite = !it.favorite
            questionAnswerRepository.toggleFavorite(it)
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

    fun getModel(): ReviewModel {
        return model
    }

    private fun failedGuess() {
        _response.value = DataState.Success(ReviewResponse.ResetView)
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
        CoroutineScope(IO).launch {
            reviewRepository.backToTopLevel(questionAnswer, model.leitner)
        }
    }

    private fun showFinishDialog() {
        _response.postValue(DataState.Success(ReviewResponse.Completed))
    }

    private suspend fun guessed() {
        _response.value = DataState.Success(ReviewResponse.ResetView)
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
            model.favorite = questionAnswer.favorite
            _response.postValue(DataState.Success(ReviewResponse.Favorite(model.favorite)))
            model.questionAnswer = questionAnswer
            CoroutineScope(IO).launch {
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
        callApiToGetSynonymsAnDefinition()
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

    private fun callApiToGetSynonymsAnDefinition() {
        CoroutineScope(IO).launch {
            fetchDefinitionsFromServer()
            fetchSynonymsFromServer()
        }
    }

    private suspend fun fetchDefinitionsFromServer() = withContext(IO) {
        model.definitionsLoading = true
        try {

            val wordDetailWithDefinitions = wordRetrofit.getWordDetails(model.question.trim())
            withContext(Main) {
                model.ipa = wordDetailWithDefinitions.firstOrNull()?.getIpaFromTag() ?: ""
                model.partOfSpeech = wordDetailWithDefinitions.firstOrNull()?.getPartOfSpeech(context)
                model.definitionsLoading = false
                definitions.postValue(wordDetailWithDefinitions.firstOrNull()?.definitions
                        ?: arrayListOf())
            }
        } catch (e: Exception) {
            model.definitionsLoading = false
        }
    }

    private suspend fun fetchSynonymsFromServer() = withContext(IO) {
        model.synonymsLoading = true
        try {
            val synonyms = wordRetrofit.getSynonyms(model.question.trim())
            withContext(Main) {
                this@ReviewViewModel.synonyms.postValue(synonyms)
                model.synonymsLoading = false
            }
        } catch (e: Exception) {
            withContext(Main) {
                model.synonymsLoading = false
            }
        }
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

    fun addQuestionAnswer(questionAnswer: QuestionAnswer) {
        questionAnswers.add(questionAnswer)
        model.totalItemsInLevel = questionAnswers.size
    }

//    private fun setIsDownloadingIfRequired(source: String, dest: String) {
//        model.downloading = (availableModels.value?.contains(source)
//                ?: false) == false || (availableModels.value?.contains(dest) ?: false) == false
//    }

}