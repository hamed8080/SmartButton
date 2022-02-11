package ir.amozkade.advancedAsisstiveTouche

import android.annotation.SuppressLint
import android.content.Context
import android.os.Environment
import android.speech.tts.TextToSpeech
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.amozkade.advancedAsisstiveTouche.helper.api.CustomSSLSocketFactory
import ir.amozkade.advancedAsisstiveTouche.helper.api.NullOnEmptyConverterFactory
import ir.amozkade.advancedAsisstiveTouche.helper.api.TokenAuthenticator
import ir.amozkade.advancedAsisstiveTouche.helper.api.TokenInterceptor
import ir.mobitrain.applicationcore.ApplicationCoreDatabase
import ir.mobitrain.applicationcore.logger.LoggerDao
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create
import java.io.File
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton
import javax.net.ssl.SSLSession
import javax.net.ssl.X509TrustManager
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope
import dagger.hilt.components.SingletonComponent

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class JWT


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BaseRetrofitWithNoApi


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AppDir

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AppDatabasePath

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GoogleDriveGoogleSignInOption


@InstallIn(SingletonComponent::class)
@Module
object AppModule {


    //FIXME: upgrade version Code and name before release
    ///Never use url without 'www' if use url without 'www' you getting 405 on Login method
    val base_url = if (BuildConfig.DEBUG) "http://192.168.1.16/mobitrain/api/" else "https://www.mobitrain.ir/Api/"


    @Provides
    fun provideRefreshTokenService(): TokenAuthenticator.RefreshTokenService {
        return Retrofit.Builder()
                .baseUrl(base_url)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(NullOnEmptyConverterFactory())
                .addConverterFactory(JacksonConverterFactory.create())
                .build()
                .create()
    }

    @Provides
    @Singleton
    @SuppressLint("TrustAllX509TrustManager")
    fun provideX509Trust(): X509TrustManager {
        return object : X509TrustManager {
            @Throws(CertificateException::class)
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
            }

            @Throws(CertificateException::class)
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
            }

            override fun getAcceptedIssuers(): Array<X509Certificate?> {
                return arrayOfNulls(0)
            }
        }
    }

    @Provides
    @JWT
    fun provideJWT(): String {
        return ir.mobitrain.applicationcore.api.JWT.instance.computedJWT ?: ""
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room
                .databaseBuilder(
                        context,
                        AppDatabase::class.java,
                        AppDatabase.DATABASE_NAME)
                .addMigrations(*AppDatabase.migrations)
                .build()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(x509Trust: X509TrustManager, tokenAuthenticator: TokenAuthenticator): OkHttpClient {
        val timeout: Long = 20
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val builder = OkHttpClient.Builder()
        builder.authenticator(tokenAuthenticator)
        builder.addInterceptor(TokenInterceptor())
        builder.addInterceptor(logging)
        builder.sslSocketFactory(CustomSSLSocketFactory(x509Trust), x509Trust)
        builder.hostnameVerifier { _: String?, _: SSLSession? -> true }
        return builder
                .readTimeout(timeout, TimeUnit.SECONDS)
                .connectTimeout(timeout, TimeUnit.SECONDS)
                .writeTimeout(timeout, TimeUnit.SECONDS)
                .callTimeout(timeout, TimeUnit.SECONDS)
                .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(base_url)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(NullOnEmptyConverterFactory())
                .addConverterFactory(JacksonConverterFactory.create())
                .build()
    }


    @Provides
    @Singleton
    @BaseRetrofitWithNoApi
    fun provideRetrofitWithNoApi(): Retrofit {
        return Retrofit.Builder()
                .baseUrl(base_url.replace("api/", ""))
                .addConverterFactory(JacksonConverterFactory.create())
                .build()
    }

    @Provides
    fun provideExceptionHandlerLiveData(): MutableLiveData<Throwable> {
        return MutableLiveData()
    }

    @Provides
    fun provideAnalytics(@ApplicationContext context: Context): FirebaseAnalytics {
        return FirebaseAnalytics.getInstance(context)
    }

    @Provides
    @Singleton
    @AppDir
    fun provideAppDir(@ApplicationContext context: Context): String {
        val dir = context.getExternalFilesDir(null)?.absolutePath ?: ""
        if (!File(dir).exists()) {
            File(dir).mkdirs()
        }
        return dir
    }

    @Provides
    @Singleton
    @AppDatabasePath
    fun provideAppDatabasePath(@ApplicationContext context: Context): String {
        return Environment.getDataDirectory().absolutePath + "/data/${context.packageName}/databases"
    }

    @Provides
    @Singleton
    fun provideLoggerDao(@ApplicationContext context: Context): LoggerDao {
        return ApplicationCoreDatabase.getFileDatabase(context).loggerDao()
    }

    @Provides
    @Singleton
    fun provideTTS(@ApplicationContext context: Context): TextToSpeech {
        val tts = TextToSpeech(context, null)
        tts.language = Locale.US
        return tts
    }


    @Provides
    @Singleton
    @GoogleDriveGoogleSignInOption
    fun provideGoogleSignInOptions(@ApplicationContext context: Context): GoogleSignInOptions {
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.server_client_id))
                .requestServerAuthCode(context.getString(R.string.server_client_id))
                .requestScopes(Scope(Scopes.DRIVE_FULL), Scope(Scopes.DRIVE_FULL))
                .requestEmail()
                .build()
    }

}