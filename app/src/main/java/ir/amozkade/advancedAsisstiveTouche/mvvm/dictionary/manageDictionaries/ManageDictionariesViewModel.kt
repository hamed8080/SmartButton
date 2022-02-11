package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.manageDictionaries


import javax.inject.Inject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.amozkade.advancedAsisstiveTouche.helper.api.DataState
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.leitners.di.LeitnerDao
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.leitners.models.Leitner
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.manageDictionaries.utils.DictionaryResponse
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.manageDictionaries.utils.ManageDictionaryStateEvent
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.collect

@HiltViewModel
class ManageDictionariesViewModel @Inject constructor(
        private val manageDictionaryRepository: ManageDictionaryRepository,
        private val leitnerDao: LeitnerDao,
        val exceptionObserver: MutableLiveData<Throwable>
) : ViewModel() {

    private var model: ManageDictionariesModel = ManageDictionariesModel()
    private val _response: MutableLiveData<DataState<DictionaryResponse>> = MutableLiveData()
    val response: LiveData<DataState<DictionaryResponse>> = _response

    fun setState(event: ManageDictionaryStateEvent) {
        val handler = CoroutineExceptionHandler { _, exception ->
            exceptionObserver.postValue(exception)
        }

        viewModelScope.launch(IO + handler) {
            supervisorScope {
                when (event) {
                    is ManageDictionaryStateEvent.LoadAllDictionary -> {
                        manageDictionaryRepository.getAll().collect {
                            _response.postValue(it)
                        }
                    }
                    is ManageDictionaryStateEvent.DeleteDictionary -> {
                        manageDictionaryRepository.deleteDictionary(event.dictionary).collect {
                            setState(ManageDictionaryStateEvent.LoadAllDictionary)
                        }
                    }

                    is ManageDictionaryStateEvent.AddAllDictionaryItemsToLeitner -> {
                        manageDictionaryRepository.startToInsertToQuestionAnswers(event.dictionary, event.leitnerId).collect {
                            _response.postValue(it)
                        }
                    }
                }
            }
        }
    }

    fun getModel(): ManageDictionariesModel {
        return model
    }

    suspend fun getAllLeitners(): List<Leitner> {
        return leitnerDao.getAll()
    }
}