package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.manageDictionaries.utils

import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.manageDictionaries.Dictionary

sealed class DictionaryResponse{
    data class AllDictionary(val dictionaries:List<Dictionary>):DictionaryResponse()
    object DeletedDictionary:DictionaryResponse()
    object InsertedAllDictionaryItemsIntoLeitner:DictionaryResponse()
}
