package ir.amozkade.advancedAsisstiveTouche.mvvm.editButtonPositions

import javax.inject.Inject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.amozkade.advancedAsisstiveTouche.helper.api.DataState
import ir.amozkade.advancedAsisstiveTouche.mvvm.editButtonPositions.utils.EditPositionResponse
import ir.amozkade.advancedAsisstiveTouche.mvvm.editButtonPositions.utils.EditPositionStateEvent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

@HiltViewModel
class EditButtonPositionViewModel @Inject constructor(
        private val repository: EditPositionRepository,
        val exceptionObserver: MutableLiveData<Throwable>
) : ViewModel() {

    private var model = EditPositionModel()
    private val _response: MutableLiveData<DataState<EditPositionResponse>> = MutableLiveData()
    val response: LiveData<DataState<EditPositionResponse>> = _response

    init {
        setState(EditPositionStateEvent.Init)
    }

    fun setState(event: EditPositionStateEvent) {
        val handler = CoroutineExceptionHandler { _, exception ->
            exceptionObserver.postValue(exception)
        }
        viewModelScope.launch(IO + handler) {
            supervisorScope {

                if (event is EditPositionStateEvent.Init) {
                    model.listEmpty = true
                    repository.init().collect {
                        _response.postValue(it)
                    }
                }

                if (event is EditPositionStateEvent.DeleteAll) {
                    model.listEmpty = true
                    _response.postValue(DataState.Success(EditPositionResponse.AllButtons(listOf())))
                }

                if (event is EditPositionStateEvent.SavePositions) {
                    repository.savePositions(event.buttons).collect {
                        _response.postValue(it)
                    }
                }
            }
        }
    }

    fun getModel(): EditPositionModel {
        return model
    }
}