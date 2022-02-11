package ir.amozkade.advancedAsisstiveTouche.mvvm.preferenceActivity.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ir.amozkade.advancedAsisstiveTouche.AppDatabase
import ir.amozkade.advancedAsisstiveTouche.mvvm.preferenceActivity.SettingRepository
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
class SettingModule {

    @Provides
    fun provideSettingDao(database: AppDatabase): SettingDao {
        return database.settingDao()
    }

    @Singleton
    @Provides
    fun provideSettingRepository(settingDao: SettingDao, @ApplicationContext context: Context): SettingRepository {
        return SettingRepository(settingDao, context)
    }

}