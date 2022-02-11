package ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ir.amozkade.advancedAsisstiveTouche.AppDatabase
import ir.amozkade.advancedAsisstiveTouche.AppDir
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.leitners.di.LeitnerDao
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.levels.questionAnswer.QuestionAnswerDao
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.levels.repository.LevelDao
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.buttons.clipborad.ClipboardButton
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.buttons.clipborad.ClipboardDao
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.buttons.translate.TranslateButton
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.buttons.translate.TranslateViewModel
import ir.amozkade.advancedAsisstiveTouche.mvvm.preferenceActivity.SettingRepository
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ButtonModule {

    @Provides
    @Singleton
    fun provideClipboardDao(database: AppDatabase): ClipboardDao {
        return database.clipboardDao()
    }

    @Provides
    @Singleton
    fun provideClipboardButton(@ApplicationContext context: Context, settingRepository: SettingRepository, clipboardDao: ClipboardDao, @AppDir appDir: String): ClipboardButton {
        return ClipboardButton(context, clipboardDao, settingRepository, appDir)
    }

    @Provides
    @Singleton
    fun provideTranslateViewModel(@ApplicationContext context: Context, levelDao: LevelDao, questionAnswerDao: QuestionAnswerDao): TranslateViewModel {
        return TranslateViewModel(context, levelDao, questionAnswerDao)
    }

    @Provides
    @Singleton
    fun provideTranslateButton(leitnerDao: LeitnerDao, @ApplicationContext context: Context, settingRepository: SettingRepository, translateViewModel: TranslateViewModel, @AppDir appDir: String): TranslateButton {
        return TranslateButton(leitnerDao, context, appDir, settingRepository, translateViewModel)
    }
}