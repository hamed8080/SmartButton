package ir.amozkade.advancedAsisstiveTouche

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ir.amozkade.advancedAsisstiveTouche.mvvm.preferenceActivity.Setting
import ir.amozkade.advancedAsisstiveTouche.helper.migrations.*
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.leitners.di.LeitnerDao
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.leitners.models.Leitner
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.levels.models.Level
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.levels.questionAnswer.QuestionAnswer
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.levels.questionAnswer.QuestionAnswerDao
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.levels.repository.LevelDao
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.manageDictionaries.Dictionary
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.manageDictionaries.di.DictionaryDao
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.review.di.DictionaryWordsDao
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.review.models.Words
import ir.amozkade.advancedAsisstiveTouche.mvvm.exception.CustomException
import ir.amozkade.advancedAsisstiveTouche.mvvm.exception.di.ExceptionDao
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.buttons.clipborad.Clipboard
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.buttons.clipborad.ClipboardDao
import ir.amozkade.advancedAsisstiveTouche.mvvm.notification.Notification
import ir.amozkade.advancedAsisstiveTouche.mvvm.notification.di.NotificationDao
import ir.amozkade.advancedAsisstiveTouche.mvvm.user.profile.Profile
import ir.amozkade.advancedAsisstiveTouche.mvvm.preferenceActivity.di.SettingDao
import ir.amozkade.advancedAsisstiveTouche.mvvm.themeManager.models.Theme
import ir.amozkade.advancedAsisstiveTouche.mvvm.themeManager.di.ThemeDao
import ir.amozkade.advancedAsisstiveTouche.mvvm.tickets.model.Ticket
import ir.amozkade.advancedAsisstiveTouche.mvvm.tickets.model.TicketStatusConverter
import ir.amozkade.advancedAsisstiveTouche.mvvm.tickets.conversation.model.TicketMessage
import ir.amozkade.advancedAsisstiveTouche.mvvm.tickets.di.TicketDao
import ir.amozkade.advancedAsisstiveTouche.mvvm.user.profile.di.ProfileDao
import ir.mobitrain.applicationcore.helper.Converters
import java.io.*

@Database(entities = [Theme::class,
    Leitner::class,
    Level::class,
    CustomException::class,
    Clipboard::class,
    QuestionAnswer::class,
    Notification::class,
    Ticket::class,
    TicketMessage::class,
    Dictionary::class,
    Setting::class,
    Profile::class
], version = 15, exportSchema = false)
@TypeConverters(Converters::class, TicketStatusConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun levelDao(): LevelDao
    abstract fun questionAnswerDao(): QuestionAnswerDao
    abstract fun leitnerDao(): LeitnerDao
    abstract fun exceptionDao(): ExceptionDao
    abstract fun themeDao(): ThemeDao
    abstract fun clipboardDao(): ClipboardDao
    abstract fun notificationDao(): NotificationDao
    abstract fun ticketDao(): TicketDao
    abstract fun dictionaryDao(): DictionaryDao
    abstract fun settingDao(): SettingDao
    abstract fun profileDao(): ProfileDao

    companion object {

        const val DATABASE_NAME = "SmartButton"
        val migrations = arrayOf(
                MIGRATION_1_2,
                MIGRATION_2_3,
                MIGRATION_3_4,
                MIGRATION_4_5,
                MIGRATION_5_6,
                MIGRATION_6_7,
                MIGRATION_7_8,
                MIGRATION_8_9,
                MIGRATION_9_10,
                MIGRATION_10_11,
                MIGRATION_11_12,
                MIGRATION_12_13,
                MIGRATION_13_14,
                MIGRATION_14_15,
        )

        fun openDicFileInDataFolder(context: Context, dbName: String, databasePath: String): WordsDatabase {
            return Room.databaseBuilder(context.applicationContext,
                    WordsDatabase::class.java,
                    dbName
            )
                    .createFromFile(File("${databasePath}/${dbName}.db"))
                    .build()
        }

        @Database(entities = [Words::class], version = 1, exportSchema = false)
        abstract class WordsDatabase : RoomDatabase() {
            abstract fun dictionaryWordsDao(): DictionaryWordsDao
        }
    }

}
