package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.manageDictionaries

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.amozkade.advancedAsisstiveTouche.AppDatabase
import ir.amozkade.advancedAsisstiveTouche.AppDatabasePath
import ir.amozkade.advancedAsisstiveTouche.AppModule
import ir.amozkade.advancedAsisstiveTouche.JWT
import ir.amozkade.advancedAsisstiveTouche.helper.api.DataState
import ir.amozkade.advancedAsisstiveTouche.helper.downloader.DownloadResponse
import ir.amozkade.advancedAsisstiveTouche.helper.downloader.Downloader
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.downloadDictionary.DownloadDictionaryAdapter
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.downloadDictionary.utils.DownloadDictionaryResponse
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.levels.questionAnswer.QuestionAnswer
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.levels.questionAnswer.QuestionAnswerDao
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.levels.repository.LevelDao
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.manageDictionaries.di.DictionaryDao
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.manageDictionaries.di.DownloadDictionaryRetrofit
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.manageDictionaries.utils.DictionaryResponse
import ir.mobitrain.applicationcore.helper.CommonHelpers
import kotlinx.coroutines.flow.*
import java.io.File
import java.io.FileFilter
import javax.inject.Inject

class ManageDictionaryRepository @Inject constructor(
        private val dictionaryDao: DictionaryDao,
        private val levelDao: LevelDao,
        private val questionAnswerDao: QuestionAnswerDao,
        private val retrofit: DownloadDictionaryRetrofit,
        private val downloader: Downloader,
        @AppDatabasePath private val databasePath: String,
        @JWT private val jwt: String,
        @ApplicationContext private val context: Context
) {

    suspend fun getAll(): Flow<DataState<DictionaryResponse>> = flow {
        emit(DataState.Loading)
        val dictionaries = dictionaryDao.getAll()
        emit(DataState.Success(DictionaryResponse.AllDictionary(dictionaries)))
    }

    suspend fun deleteDictionary(dictionary: Dictionary): Flow<DataState<DictionaryResponse>> = flow {
        emit(DataState.Loading)
        val dbPath = "${databasePath}/"
        File(dbPath).listFiles(FileFilter { it.name.contains(dictionary.dbNameWithoutZipExtension) })?.forEach {
            it.delete()
        }
        dictionaryDao.delete(dictionary)
        emit(DataState.Success(DictionaryResponse.DeletedDictionary))
    }

    suspend fun startToInsertToQuestionAnswers(dictionary: Dictionary, leitnerId: Int): Flow<DataState<DictionaryResponse>> = flow {
        emit(DataState.BlockLoading)
        val levelId = levelDao.getFirstLevelIdInLeitner(leitnerId)
        val fileDb = AppDatabase.openDicFileInDataFolder(context, dictionary.dbNameWithoutZipExtension, databasePath)
        val allWordsInDictionary = fileDb.dictionaryWordsDao().getAll()
        fileDb.close()
        if (File("${databasePath}/${dictionary.dbNameWithoutZipExtension}").exists()) {
//                remove duplicate file when room open database file
            File("${databasePath}/${dictionary.dbNameWithoutZipExtension}").delete()
        }
        val questionAnswers = allWordsInDictionary.map {
            QuestionAnswer(it.word ?: "", null, null, leitnerId, levelId)
        }
        questionAnswerDao.insert(questionAnswers)
        emit(DataState.Success(DictionaryResponse.InsertedAllDictionaryItemsIntoLeitner))
    }

    suspend fun insert(dictionary: Dictionary) {
        dictionaryDao.insert(dictionary)
    }

    @Suppress("EXPERIMENTAL_API_USAGE")
    suspend fun unCompressDictionary(dictionary: Dictionary) = callbackFlow<DataState<DownloadResponse>> {
        val zipFile = File("${databasePath}/${dictionary.fileName}")
        val savePath = databasePath
        CommonHelpers.decompressFileAtPath(zipFile, savePath, object : CommonHelpers.ZipCompressionDelegate {
            override fun failedUnZipFile() {

            }

            override fun successUnZipFile() {
                offer(DataState.Success(DownloadResponse.Uncompressed(dictionary)))
            }
        })
        insert(dictionary)
    }

    suspend fun downloadDictionary(downloadDictionaryStatus: DownloadDictionaryAdapter.DownloadDictionaryStatus): Flow<DataState<DownloadResponse>> = flow {
        val dicUrl = AppModule.base_url.replace("api/", "") + "SmartButton/Dictionaries/${downloadDictionaryStatus.dictionary.fileName}"
        val saveFilePath = "${databasePath}/${downloadDictionaryStatus.dictionary.fileName}"
        downloader.download(dicUrl, saveFilePath, "Basic $jwt" , downloadDictionaryStatus.dictionary).collect { dataState ->
            emit(dataState)
        }
    }

    suspend fun getDictionaryListFromServer(): Flow<DataState<DownloadDictionaryResponse>> = flow {
        emit(DataState.Loading)
        val dictionaries = retrofit.getAllDictionaries()
        emit(DataState.Success(DownloadDictionaryResponse.DictionaryList(dictionaries)))
    }

}
