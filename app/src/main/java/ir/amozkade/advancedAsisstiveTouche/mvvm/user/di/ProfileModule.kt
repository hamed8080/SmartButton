package ir.amozkade.advancedAsisstiveTouche.mvvm.user.di

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ir.amozkade.advancedAsisstiveTouche.R
import retrofit2.Retrofit
import retrofit2.create

@InstallIn(ViewModelComponent::class)
@Module
class ProfileModule {

    @Provides
    fun provideProfileRetrofit(retrofit:Retrofit):ProfileRetrofit{
       return retrofit.create()
    }
}

@InstallIn(SingletonComponent::class)
@Module
class ProfileSingletonModule {

    @Provides
    fun provideGoogleSignInOptions(@ApplicationContext context: Context):GoogleSignInOptions{
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.server_client_id))
            .requestEmail()
            .build()
    }
}