package ir.amozkade.advancedAsisstiveTouche.mvvm.exception.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.amozkade.advancedAsisstiveTouche.AppDatabase
import retrofit2.Retrofit
import retrofit2.create

@InstallIn(SingletonComponent::class)
@Module
class ExceptionModule {

    @Provides
    fun provideExceptionRetrofit(retrofit:Retrofit): ExceptionRetrofit {
        return retrofit.create()
    }

    @Provides
    fun provideExceptionDao(database: AppDatabase): ExceptionDao {
        return database.exceptionDao()
    }
}