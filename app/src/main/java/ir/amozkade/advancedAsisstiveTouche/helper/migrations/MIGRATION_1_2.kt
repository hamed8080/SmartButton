package ir.amozkade.advancedAsisstiveTouche.helper.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2: Migration = object : Migration(1, 2) {
    // From version 1 to version 2
    override fun migrate(database: SupportSQLiteDatabase) {
        // Raw sql Query if needed like below
        database.execSQL("""CREATE TABLE IF NOT EXISTS CustomException (
                       id integer PRIMARY KEY AUTOINCREMENT NOT NULL,
                       message text,
                       traces text,
                       api integer,
                       device text,
                       appVersion text,
                       date integer)""")

        database.execSQL("""CREATE TABLE IF NOT EXISTS Clipboard (
                       id integer PRIMARY KEY AUTOINCREMENT NOT NULL,
                       text text,
                       orderPlace integer)""")
    }
}