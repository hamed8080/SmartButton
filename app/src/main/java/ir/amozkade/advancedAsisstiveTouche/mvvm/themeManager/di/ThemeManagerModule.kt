package ir.amozkade.advancedAsisstiveTouche.mvvm.themeManager.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.amozkade.advancedAsisstiveTouche.AppDatabase
import retrofit2.Retrofit
import retrofit2.create

@InstallIn(SingletonComponent::class)
@Module
class ThemeManagerModule {

    @Provides
    fun provideThemeManagerRetrofit(retrofit:Retrofit):ThemeManagerRetrofit{
       return retrofit.create()
    }

    @Provides
    fun provideThemeManagerDao(database: AppDatabase): ThemeDao {
        return database.themeDao()
    }
}