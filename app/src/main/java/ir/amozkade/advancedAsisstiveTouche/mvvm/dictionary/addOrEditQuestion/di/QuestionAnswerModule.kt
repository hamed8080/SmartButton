package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.addOrEditQuestion.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.amozkade.advancedAsisstiveTouche.AppDatabase
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.levels.questionAnswer.QuestionAnswerDao
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class QuestionAnswerModule {

    @Provides
    @Singleton
    fun provideQuestionAnswerDao(database: AppDatabase): QuestionAnswerDao {
        return database.questionAnswerDao()
    }
}