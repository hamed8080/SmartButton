package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.downloadDictionary

import javax.inject.Inject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.amozkade.advancedAsisstiveTouche.helper.api.DataState
import ir.amozkade.advancedAsisstiveTouche.helper.downloader.DownloadResponse
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.downloadDictionary.utils.DownloadDictionaryResponse
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.downloadDictionary.utils.DownloadDictionaryStateEvent
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.manageDictionaries.ManageDictionaryRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.collect

@HiltViewModel
class DownloadDictionaryViewModel @Inject constructor(
        private val manageDictionaryRepository: ManageDictionaryRepository,
        val exceptionObserver: MutableLiveData<Throwable>
) : ViewModel() {

    private val _response: MutableLiveData<DataState<DownloadDictionaryResponse>> = MutableLiveData()
    val response: LiveData<DataState<DownloadDictionaryResponse>> = _response

    private val _downloadResponse: MutableLiveData<DataState<DownloadResponse>> = MutableLiveData()
    val downloadResponse: LiveData<DataState<DownloadResponse>> = _downloadResponse

    fun setState(event: DownloadDictionaryStateEvent) {
        val handler = CoroutineExceptionHandler { _, exception ->
            exceptionObserver.postValue(exception)
        }

        viewModelScope.launch(IO + handler) {
            supervisorScope {
                when (event) {
                    is DownloadDictionaryStateEvent.Download -> {
                        manageDictionaryRepository.downloadDictionary(event.downloadDictionaryStatus).collect {
                            _downloadResponse.postValue(it)
                        }
                    }
                    DownloadDictionaryStateEvent.GetAllDictionaryList -> {
                        manageDictionaryRepository.getDictionaryListFromServer().collect {
                            _response.postValue(it)
                        }
                    }
                    is DownloadDictionaryStateEvent.UnCompress -> {
                        manageDictionaryRepository.unCompressDictionary(event.dictionary).collect {
                            _downloadResponse.postValue(it)
                        }
                    }
                }
            }
        }
    }
}