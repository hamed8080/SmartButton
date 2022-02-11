package ir.amozkade.advancedAsisstiveTouche.mvvm.themeManager

import android.content.Context
import javax.inject.Inject
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.amozkade.advancedAsisstiveTouche.helper.api.DataState
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.FloatingWindow
import ir.amozkade.advancedAsisstiveTouche.mvvm.preferenceActivity.SettingRepository
import ir.amozkade.advancedAsisstiveTouche.mvvm.themeManager.utils.ThemeManagerResponse
import ir.amozkade.advancedAsisstiveTouche.mvvm.themeManager.utils.ThemeManagerStateEvent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.flow.collect

@HiltViewModel
class ThemeManagerViewModel @Inject constructor(
        private val repository: ThemeManagerRepository,
        private val settingRepository: SettingRepository,
        val exceptionObserver: MutableLiveData<Throwable>
) : ViewModel() {

    private val model: ThemeManagerModel = ThemeManagerModel()
    private val _response: MutableLiveData<DataState<ThemeManagerResponse>> = MutableLiveData()
    val response: LiveData<DataState<ThemeManagerResponse>> = _response

    fun getModel(): ThemeManagerModel {
        return model
    }

    fun setState(event: ThemeManagerStateEvent) {
        val handler = CoroutineExceptionHandler { _, exception ->
            exceptionObserver.postValue(exception)
        }
        viewModelScope.launch(IO + handler) {
            supervisorScope {

                when (event) {
                    is ThemeManagerStateEvent.GetAllThemes -> {
                        repository.getAllThemes().collect {
                            _response.postValue(it)
                        }
                    }
                    is ThemeManagerStateEvent.DownloadTheme -> {
                        repository.downloadTheme(event.theme).collect {
                            _response.postValue(it)
                        }
                    }
                    is ThemeManagerStateEvent.DownloadThemePack -> {
                        repository.downloadPack(event.themePack).collect{
                            _response.postValue(it)
                        }
                    }
                }
            }
        }
    }

    fun setDefaultTheme(context: Context) {
        viewModelScope.launch(IO){
            settingRepository.setDefaultTheme()
        }
        FloatingWindow.restartButtonService(context,settingRepository)
    }
}