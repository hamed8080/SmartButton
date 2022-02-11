package ir.amozkade.advancedAsisstiveTouche.mvvm.permissions.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import retrofit2.Retrofit
import retrofit2.create

@InstallIn(ViewModelComponent::class)
@Module
class PermissionModule {

    @Provides
    @ViewModelScoped
    fun provideTicketRetrofit(retrofit:Retrofit):PermissionRetrofit{
       return retrofit.create()
    }
}