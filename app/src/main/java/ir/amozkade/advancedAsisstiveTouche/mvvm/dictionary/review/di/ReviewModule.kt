package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.review.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.create
import javax.inject.Qualifier


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefinitionApi

@InstallIn(ViewModelComponent::class)
@Module
object ReviewModule {


    @DefinitionApi
    @Provides
    @ViewModelScoped
    fun provideDefinitionApi(): Retrofit {
        return Retrofit.Builder()
                .baseUrl("https://api.datamuse.com/")
                .addConverterFactory(JacksonConverterFactory.create())
                .build()
    }

    @Provides
    @ViewModelScoped
    fun provideTicketRetrofit(@DefinitionApi retrofit:Retrofit): WordRetrofit {
        return retrofit.create()
    }

}