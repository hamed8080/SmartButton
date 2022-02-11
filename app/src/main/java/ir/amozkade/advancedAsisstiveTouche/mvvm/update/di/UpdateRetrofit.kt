package ir.amozkade.advancedAsisstiveTouche.mvvm.update.di

import ir.amozkade.advancedAsisstiveTouche.mvvm.update.UpdateApp
import retrofit2.http.GET
import retrofit2.http.Query

interface UpdateRetrofit {

    @GET("UpdateApp/")
    suspend fun checkUpdate(@Query("osType") osType: String = "ANDROID"): UpdateApp
}