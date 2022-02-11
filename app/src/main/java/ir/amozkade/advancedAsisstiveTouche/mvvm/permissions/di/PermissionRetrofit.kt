package ir.amozkade.advancedAsisstiveTouche.mvvm.permissions.di

import ir.amozkade.advancedAsisstiveTouche.mvvm.permissions.models.Permission
import retrofit2.http.GET

interface PermissionRetrofit {
    @GET("SmartButton/Permissions")
    suspend fun getAll(): List<Permission>
}