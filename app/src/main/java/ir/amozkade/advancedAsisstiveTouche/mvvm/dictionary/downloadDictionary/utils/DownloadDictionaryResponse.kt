package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.downloadDictionary.utils

import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.manageDictionaries.Dictionary

sealed class DownloadDictionaryResponse{
    data class DictionaryList(val  dictionaries:List<Dictionary>):DownloadDictionaryResponse()
}
