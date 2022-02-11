package ir.amozkade.advancedAsisstiveTouche.mvvm.permissions

import javax.inject.Inject
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.amozkade.advancedAsisstiveTouche.helper.api.DataState
import ir.amozkade.advancedAsisstiveTouche.mvvm.permissions.utils.PermissionResponse
import ir.amozkade.advancedAsisstiveTouche.mvvm.permissions.utils.PermissionStateEvent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

@HiltViewModel
class PermissionViewModel @Inject constructor(private val repository: PermissionRepository, val exceptionObserver: MutableLiveData<Throwable>) : ViewModel() {

    private val model = PermissionModel()

    private val _response: MutableLiveData<DataState<PermissionResponse>> = MutableLiveData()
    val response: LiveData<DataState<PermissionResponse>> = _response

    fun setState(event: PermissionStateEvent) {
        val handler = CoroutineExceptionHandler { _, exception ->
            exceptionObserver.postValue(exception)
        }
        viewModelScope.launch(handler) {
            supervisorScope {
                when (event) {
                    is PermissionStateEvent.GetAllPermissions -> {
                        repository.getAllPermissions().collect {
                            _response.postValue(it)
                        }
                    }
                }
            }
        }
    }

    fun getModel(): PermissionModel {
        return model
    }
}