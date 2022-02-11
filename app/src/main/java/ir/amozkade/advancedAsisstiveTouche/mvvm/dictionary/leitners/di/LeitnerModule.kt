package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.leitners.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.amozkade.advancedAsisstiveTouche.AppDatabase
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class LeitnerModule {

    @Provides
    @Singleton
    fun provideCategoriesDao(database: AppDatabase): LeitnerDao {
        return database.leitnerDao()
    }
}