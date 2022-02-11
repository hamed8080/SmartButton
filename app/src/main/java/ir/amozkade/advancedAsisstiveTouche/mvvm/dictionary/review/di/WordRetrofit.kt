package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.review.di

import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.review.models.Synonym
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.review.models.WordDetail
import retrofit2.http.GET
import retrofit2.http.Query

interface WordRetrofit {

    @GET("words?max=1&md=prsd&ipa=1")
    suspend fun getWordDetails(@Query("sp") sp: String): List<WordDetail>

    @GET("/words?max=4")
    suspend fun getSynonyms(@Query("rel_syn") rel_syn: String): List<Synonym>
}