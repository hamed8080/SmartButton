package ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.di

import android.content.Context
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.databinding.ButtonSmartButtonBinding
import ir.amozkade.advancedAsisstiveTouche.databinding.PanelSmartButtonBinding
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.ButtonGesture

@InstallIn(ServiceComponent::class)
@Module
object FloatingWindowModule {

    @Provides
    fun provideButtonGesture(@ApplicationContext context: Context): ButtonGesture {
        return ButtonGesture(context)
    }

    @Provides
    fun provideContextThemeWrapper(@ApplicationContext context: Context): ContextThemeWrapper {
        return ContextThemeWrapper(context, R.style.AppTheme)
    }

    @Provides
    fun providePanelBinding(contextThemeWrapper: ContextThemeWrapper): PanelSmartButtonBinding {
        return DataBindingUtil.inflate(LayoutInflater.from(contextThemeWrapper), R.layout.panel_smart_button, null, true)
    }

    @Provides
    fun provideButtonBinding(contextThemeWrapper: ContextThemeWrapper): ButtonSmartButtonBinding {
        return DataBindingUtil.inflate(LayoutInflater.from(contextThemeWrapper), R.layout.button_smart_button, null, true)
    }

}

