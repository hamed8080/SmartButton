package ir.amozkade.advancedAsisstiveTouche.mvvm.notification.di

import ir.amozkade.advancedAsisstiveTouche.mvvm.notification.Notification
import retrofit2.http.GET
import retrofit2.http.Query

interface NotificationRetrofit {
    @GET("FCM/UserNotifications")
    suspend fun getAllNotifications(@Query("userId") userId: String?): List<Notification>

    @GET("FCM/SeenAll")
    suspend fun setSeenAll(@Query("userId") userId: String?): Unit
}