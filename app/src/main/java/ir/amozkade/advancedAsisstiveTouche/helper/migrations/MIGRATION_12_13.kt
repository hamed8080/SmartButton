package ir.amozkade.advancedAsisstiveTouche.helper.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_12_13: Migration = object : Migration(12, 13) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""ALTER TABLE `QuestionAnswer` ADD COLUMN`completed`INTEGER  NOT NULL  DEFAULT 0""")
    }
}