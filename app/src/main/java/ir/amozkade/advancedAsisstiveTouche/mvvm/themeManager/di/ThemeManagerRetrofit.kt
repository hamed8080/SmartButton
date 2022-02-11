package ir.amozkade.advancedAsisstiveTouche.mvvm.themeManager.di

import ir.amozkade.advancedAsisstiveTouche.mvvm.themeManager.models.Section
import ir.amozkade.advancedAsisstiveTouche.mvvm.themeManager.models.Theme
import ir.amozkade.advancedAsisstiveTouche.mvvm.themeManager.models.ThemePack
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

interface ThemeManagerRetrofit {

    @GET("SmartButton/Theme")
    suspend fun getAll(): List<Section<Theme>>


    @GET("SmartButton/Theme/ThemePacks")
    suspend fun getAllThemePacks():List<ThemePack>

    @GET
    @Streaming
    suspend fun download(@Url fileUrl: String?): Response<ResponseBody>
}