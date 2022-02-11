package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.manageDictionaries.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import ir.amozkade.advancedAsisstiveTouche.AppDatabase
import retrofit2.Retrofit
import retrofit2.create

@InstallIn(ViewModelComponent::class)
@Module
class DictionaryModule {


    @Provides
    fun provideDictionaryRetrofit(retrofit: Retrofit): DownloadDictionaryRetrofit {
        return retrofit.create()
    }

    @Provides
    fun provideDictionaryDao(database: AppDatabase): DictionaryDao {
        return database.dictionaryDao()
    }
}