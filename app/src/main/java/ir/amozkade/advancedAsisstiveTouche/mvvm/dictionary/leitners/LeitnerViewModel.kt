package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.leitners

import android.util.Log
import javax.inject.Inject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.amozkade.advancedAsisstiveTouche.helper.api.DataState
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.leitners.utils.LeitnerResponse
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.leitners.utils.LeitnerStateEvent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.flow.collect

@HiltViewModel
class LeitnerViewModel @Inject constructor(
        private val leitnerRepository: LeitnerRepository,
        val exceptionObserver: MutableLiveData<Throwable>
) : ViewModel() {

    private val _response: MutableLiveData<DataState<LeitnerResponse>> = MutableLiveData()
    val response: LiveData<DataState<LeitnerResponse>> = _response

    fun setState(event: LeitnerStateEvent) {
        val handler = CoroutineExceptionHandler { _, exception ->
            exceptionObserver.postValue(exception)
            Log.i("TAG" , "ex:${exception.message } innser meesage:${exception.cause?.message}")
        }
        viewModelScope.launch(handler) {
            supervisorScope {
                when (event) {
                    is LeitnerStateEvent.AllLeitner -> {
                        leitnerRepository.getAllLeitners().collect {
                            _response.postValue(it)
                        }
                    }
                    is LeitnerStateEvent.AddOrEditLeitner -> {
                        leitnerRepository.addOrUpdateLeitner(event.leitner).collect {
                            _response.postValue(it)
                        }
                    }
                    is LeitnerStateEvent.DeleteLeitner -> {
                        leitnerRepository.delete(event.leitner).collect {
                            _response.postValue(it)
                        }
                    }
                    is LeitnerStateEvent.SetBackToTopEnable -> {
                        leitnerRepository.setIsBackToTopEnable(event.leitner).collect {
                            _response.postValue(it)
                        }
                    }
                    is LeitnerStateEvent.SeShowDefinition ->{
                        leitnerRepository.setShowDefinition(event.leitner).collect{
                            _response.postValue(it)
                        }
                    }
                }
            }
        }
    }
}