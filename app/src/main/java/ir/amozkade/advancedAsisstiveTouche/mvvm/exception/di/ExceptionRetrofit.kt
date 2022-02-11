package ir.amozkade.advancedAsisstiveTouche.mvvm.exception.di

import ir.amozkade.advancedAsisstiveTouche.mvvm.exception.CustomException
import retrofit2.http.Body
import retrofit2.http.POST

interface ExceptionRetrofit {

    @POST("SmartButton/Exception")
    suspend fun logExceptions(@Body exceptions: List<CustomException>): Unit
}