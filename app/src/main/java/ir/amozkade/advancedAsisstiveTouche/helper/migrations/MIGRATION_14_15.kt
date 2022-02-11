package ir.amozkade.advancedAsisstiveTouche.helper.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_14_15: Migration = object : Migration(14, 15) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""ALTER TABLE `QuestionAnswer` ADD COLUMN`favorite` INTEGER  NOT NULL DEFAULT 0""")
        database.execSQL("""ALTER TABLE `QuestionAnswer` ADD COLUMN`favoriteDate` INTEGER""")
    }
}