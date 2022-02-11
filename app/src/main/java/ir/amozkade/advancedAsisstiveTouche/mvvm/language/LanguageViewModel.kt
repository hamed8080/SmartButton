package ir.amozkade.advancedAsisstiveTouche.mvvm.language

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ir.mobitrain.applicationcore.helper.Preference.setLanguage

class LanguageViewModel(application: Application ) :AndroidViewModel(application){

    var languageLiveData:MutableLiveData<String> = MutableLiveData()

    fun languageTaped(identifier:String){
        setLanguage(identifier)
        languageLiveData.value = identifier
    }
}