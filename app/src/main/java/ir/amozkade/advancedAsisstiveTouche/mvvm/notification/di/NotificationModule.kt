package ir.amozkade.advancedAsisstiveTouche.mvvm.notification.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.amozkade.advancedAsisstiveTouche.AppDatabase
import retrofit2.Retrofit
import retrofit2.create

@InstallIn(SingletonComponent::class)
@Module
class NotificationModule {

    @Provides
    fun provideTicketRetrofit(retrofit:Retrofit):NotificationRetrofit{
       return retrofit.create()
    }

    @Provides
    fun provideNotificationDao(database: AppDatabase): NotificationDao {
        return database.notificationDao()
    }
}