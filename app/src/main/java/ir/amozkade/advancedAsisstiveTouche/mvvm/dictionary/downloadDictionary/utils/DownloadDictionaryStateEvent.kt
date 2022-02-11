package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.downloadDictionary.utils

import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.downloadDictionary.DownloadDictionaryAdapter
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.manageDictionaries.Dictionary

sealed class DownloadDictionaryStateEvent{
    data class Download(val downloadDictionaryStatus: DownloadDictionaryAdapter.DownloadDictionaryStatus):DownloadDictionaryStateEvent()
    object GetAllDictionaryList:DownloadDictionaryStateEvent()
    data class UnCompress(val dictionary: Dictionary):DownloadDictionaryStateEvent()
}
