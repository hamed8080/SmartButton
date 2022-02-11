package ir.amozkade.advancedAsisstiveTouche.mvvm.mainAssistiveTouch.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.net.ssl.SSLSession

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GoogleDriveOkHttpClientInstance

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GoogleDriveRetrofitInstance

@Module
@InstallIn(ViewModelComponent::class)
class GoogleDriveModule {


    companion object{
        const val baseUrl = "https://www.googleapis.com/drive/v3/"
    }


    @GoogleDriveOkHttpClientInstance
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        val timeout: Long = 20
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val builder = OkHttpClient.Builder()
        builder.addInterceptor(logging)
        builder.hostnameVerifier { _: String?, _: SSLSession? -> true }
        return builder
                .readTimeout(timeout, TimeUnit.SECONDS)
                .connectTimeout(timeout, TimeUnit.SECONDS)
                .writeTimeout(timeout, TimeUnit.SECONDS)
                .callTimeout(timeout, TimeUnit.SECONDS)
                .build()
    }

    @GoogleDriveRetrofitInstance
    @Provides
    fun provideRetrofit(@GoogleDriveOkHttpClientInstance okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(baseUrl)
                .addConverterFactory(JacksonConverterFactory.create())
                .build()
    }

    @Provides
    fun provideGoogleDriveRetrofit(@GoogleDriveRetrofitInstance retrofit: Retrofit):GoogleDriveRetrofit{
        return  retrofit.create()
    }

}