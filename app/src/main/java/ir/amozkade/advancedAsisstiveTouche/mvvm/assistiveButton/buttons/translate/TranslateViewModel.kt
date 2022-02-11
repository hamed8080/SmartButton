package ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.buttons.translate

import ir.amozkade.advancedAsisstiveTouche.R
import android.content.Context
import android.util.LruCache
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.*
import androidx.lifecycle.Observer
import com.google.android.gms.tasks.OnCompleteListener
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.google.mlkit.common.model.RemoteModelManager
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.levels.questionAnswer.QuestionAnswer
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.levels.questionAnswer.QuestionAnswerDao
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.levels.repository.LevelDao
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject


class TranslateViewModel @Inject constructor(
        @ApplicationContext private val context: Context,
        private val levelDao: LevelDao,
        private val questionAnswerDao: QuestionAnswerDao
) {


    private var languageObserverEnabled: Boolean = false

    fun setLanguageObserverEnabled(isEnable :Boolean) {
        languageObserverEnabled = isEnable
    }

    companion object {
        private const val NUM_TRANSLATORS = 3
    }

    private val modelManager: RemoteModelManager = RemoteModelManager.getInstance()
    val sourceLang = MutableLiveData<CustomTranslateLanguage>()
    val targetLang = MutableLiveData<CustomTranslateLanguage>()
    val translatedText = MediatorLiveData<ResultOrError>()
    val sourceText = MutableLiveData<String>()
    val downloadModelText = MutableLiveData<String>()
    private val availableModels = MutableLiveData<List<String>>()

    init {
        val processTranslation =
                OnCompleteListener<String> { task ->
                    if (task.isSuccessful) {
                        translatedText.value = ResultOrError(task.result, null)
                    } else {
                        translatedText.value = ResultOrError(null, task.exception)
                    }
                    fetchDownloadedModels()
                }
        translatedText.addSource(sourceText) { translate().addOnCompleteListener(processTranslation) }
        val languageObserver =
                Observer<CustomTranslateLanguage> {
                    if (languageObserverEnabled)
                        translate().addOnCompleteListener(processTranslation)
                }
        translatedText.addSource(sourceLang, languageObserver)
        translatedText.addSource(targetLang, languageObserver)
        fetchDownloadedModels()
    }

    private fun getModel(languageCode: String): TranslateRemoteModel {
        return TranslateRemoteModel.Builder(languageCode).build()
    }

    internal fun downloadLanguage(language: CustomTranslateLanguage) {
        val model = getModel(TranslateLanguage.fromLanguageTag(language.code)!!)
        modelManager.download(model, DownloadConditions.Builder().build())
                .addOnCompleteListener { fetchDownloadedModels() }
    }

    private fun fetchDownloadedModels() {
        modelManager.getDownloadedModels(TranslateRemoteModel::class.java)
                .addOnSuccessListener { remoteModels ->
                    availableModels.value =
                            remoteModels.sortedBy { it.language }.map { it.language }
                }
    }

    private fun translate(): Task<String> {
        val text = sourceText.value
        val source = sourceLang.value
        val target = targetLang.value
        if (source == null || target == null || text == null || text.isEmpty()) {
            return Tasks.forResult("")
        }
        val sourceLangCode = TranslateLanguage.fromLanguageTag(source.code)!!
        val targetLangCode = TranslateLanguage.fromLanguageTag(target.code)!!
        val options = TranslatorOptions.Builder()
                .setSourceLanguage(sourceLangCode)
                .setTargetLanguage(targetLangCode)
                .build()
        setIsDownloadingIfRequired(sourceLangCode, targetLangCode)
        return translators[options]
                .downloadModelIfNeeded()
                .continueWithTask { task ->
                    if (task.isSuccessful) {
                        translators[options].translate(text)
                    } else {
                        Tasks.forException<String>(
                                task.exception ?: Exception(context.getString(R.string.warning))
                        )
                    }
                }
    }

    private fun setIsDownloadingIfRequired(source: String, dest: String) {
        if (availableModels.value?.contains(source) == false || availableModels.value?.contains(dest) == false) {
            downloadModelText.value = context.getString(R.string.downloading_translation)
        }
    }

    fun addToLeitner(text: String, leitnerId: Int) {
        GlobalScope.launch {
            val levelId = levelDao.getFirstLevelIdInLeitner(leitnerId)
            val questionAnswer = QuestionAnswer(text, null, null, leitnerId, levelId)
            questionAnswerDao.insert(questionAnswer)
        }
    }

    val availableLanguages: List<CustomTranslateLanguage> = TranslateLanguage.getAllLanguages()
            .map {
                CustomTranslateLanguage(it)
            }

    private val translators =
            object : LruCache<TranslatorOptions, Translator>(NUM_TRANSLATORS) {
                override fun create(options: TranslatorOptions): Translator {
                    return Translation.getClient(options)
                }

                override fun entryRemoved(
                        evicted: Boolean,
                        key: TranslatorOptions,
                        oldValue: Translator,
                        newValue: Translator?
                ) {
                    oldValue.close()
                }
            }

}