package ir.amozkade.advancedAsisstiveTouche.helper.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_5_6: Migration = object : Migration(5,6) {

    override fun migrate(database: SupportSQLiteDatabase) {
        // Raw sql Query if needed like below
        database.execSQL("""DROP TABLE Logger""")
    }
}