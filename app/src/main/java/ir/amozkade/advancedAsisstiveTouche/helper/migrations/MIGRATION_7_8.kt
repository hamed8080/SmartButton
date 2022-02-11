package ir.amozkade.advancedAsisstiveTouche.helper.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_7_8: Migration = object : Migration(7, 8) {

    override fun migrate(database: SupportSQLiteDatabase) {
        // Raw sql Query if needed like below
        database.execSQL("""ALTER TABLE Leitner ADD COLUMN isBackToTopEnable INTEGER DEFAULT 1 NOT NULL""")
    }
}