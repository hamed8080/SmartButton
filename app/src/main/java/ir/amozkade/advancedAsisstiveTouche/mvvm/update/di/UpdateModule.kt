package ir.amozkade.advancedAsisstiveTouche.mvvm.update.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create

@Module
@InstallIn(SingletonComponent::class)
class UpdateModule {

    @Provides
    fun provideUpdateRetrofit(retrofit: Retrofit):UpdateRetrofit{
        return retrofit.create()
    }
}