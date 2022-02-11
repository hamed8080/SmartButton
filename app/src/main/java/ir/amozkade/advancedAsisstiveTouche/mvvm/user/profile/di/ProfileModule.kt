package ir.amozkade.advancedAsisstiveTouche.mvvm.user.profile.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.amozkade.advancedAsisstiveTouche.AppDatabase

@Module
@InstallIn(SingletonComponent::class)
class ProfileModule {

    @Provides
    fun provideProfileDao(database: AppDatabase):ProfileDao{
        return database.profileDao()
    }
}