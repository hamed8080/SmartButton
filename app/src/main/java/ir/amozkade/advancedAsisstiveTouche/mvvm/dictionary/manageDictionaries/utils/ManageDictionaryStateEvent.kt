package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.manageDictionaries.utils

import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.manageDictionaries.Dictionary

sealed class ManageDictionaryStateEvent {
    object LoadAllDictionary : ManageDictionaryStateEvent()
    data class DeleteDictionary(val dictionary: Dictionary) : ManageDictionaryStateEvent()
    data class AddAllDictionaryItemsToLeitner(val dictionary: Dictionary, val leitnerId: Int) :ManageDictionaryStateEvent()
}

