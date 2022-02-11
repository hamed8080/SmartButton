package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.manageDictionaries.di

import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.manageDictionaries.Dictionary
import retrofit2.http.GET

interface DownloadDictionaryRetrofit{
    @GET("SmartButton/Dictionary")
    suspend fun getAllDictionaries(): List<Dictionary>
}
