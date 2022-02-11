package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.leitners.add


import android.content.Context
import android.text.Editable
import javax.inject.Inject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.helper.InAppException
import ir.amozkade.advancedAsisstiveTouche.helper.api.DataState
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.leitners.LeitnerRepository
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.leitners.add.utils.AddOrEditLeitnerResponse
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.leitners.add.utils.LeitnerAddOrEditStateEvent

import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.leitners.models.Leitner
import ir.mobitrain.applicationcore.helper.CTextWatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.flow.collect

@HiltViewModel
class AddOrEditLeitnerViewModel @Inject constructor(
        val exceptionObserver: MutableLiveData<Throwable>,
        @ApplicationContext private val context: Context,
        private val leitnerRepository: LeitnerRepository
) : ViewModel() {

    private var model: AddOrEditLeitnerModel = AddOrEditLeitnerModel()

    private val _response: MutableLiveData<DataState<AddOrEditLeitnerResponse>> = MutableLiveData()
    val response: LiveData<DataState<AddOrEditLeitnerResponse>> = _response

    var nameTextWatcher: CTextWatcher = object : CTextWatcher() {
        override fun afterTextChanged(s: Editable) {
            super.afterTextChanged(s)
            model.name = s.toString()
        }
    }

    fun setLeitner(leitner : Leitner?) {
        model.setLeitner(leitner ?: Leitner(0, ""))
    }

    fun getLeitner(): Leitner {
        return model.getLeitner()
    }

    fun setState(leitnerAddOrEditStateEvent: LeitnerAddOrEditStateEvent) {
        val handler = CoroutineExceptionHandler { _, exception ->
            exceptionObserver.postValue(exception)
        }
        viewModelScope.launch(handler) {
            supervisorScope {
                if (leitnerAddOrEditStateEvent is LeitnerAddOrEditStateEvent.CheckDuplicate) {
                    leitnerRepository.checkExist(model.name).collect {
                        if (it is DataState.Success && it.data is AddOrEditLeitnerResponse.CheckedDuplicateResult) {
                            if (!it.data.exist) {
                                _response.postValue(it)
                            } else {
                                exceptionObserver.postValue(InAppException(context.getString(R.string.add_or_edit_leitner), context.getString(R.string.leitner_exist), null))
                            }
                        }
                    }
                }
            }
        }
    }

    fun getModel(): AddOrEditLeitnerModel {
        return model
    }
}