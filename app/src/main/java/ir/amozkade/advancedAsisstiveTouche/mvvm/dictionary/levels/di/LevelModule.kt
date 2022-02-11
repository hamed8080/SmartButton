package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.levels.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.amozkade.advancedAsisstiveTouche.AppDatabase
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.levels.repository.LevelDao
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class LevelModule {

    @Provides
    @Singleton
    fun provideLevelDao(database: AppDatabase): LevelDao {
        return database.levelDao()
    }
}