package ir.amozkade.advancedAsisstiveTouche.mvvm.tickets.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.amozkade.advancedAsisstiveTouche.AppDatabase
import retrofit2.Retrofit
import retrofit2.create

@InstallIn(SingletonComponent::class)
@Module
class TicketModule {

    @Provides
    fun provideTicketRetrofit(retrofit:Retrofit):TicketRetrofit{
       return retrofit.create()
    }

    @Provides
    fun provideTicketDao(database: AppDatabase): TicketDao {
        return database.ticketDao()
    }
}