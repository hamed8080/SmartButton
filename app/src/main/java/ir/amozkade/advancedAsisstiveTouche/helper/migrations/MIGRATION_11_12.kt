package ir.amozkade.advancedAsisstiveTouche.helper.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_11_12: Migration = object : Migration(11, 12) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""ALTER TABLE `Leitner` ADD COLUMN`showDefinition`INTEGER  NOT NULL  DEFAULT 1""")
    }
}