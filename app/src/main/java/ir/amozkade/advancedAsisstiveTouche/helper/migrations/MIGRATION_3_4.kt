package ir.amozkade.advancedAsisstiveTouche.helper.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_3_4: Migration = object : Migration(3, 4) {
    // From version 1 to version 2
    override fun migrate(database: SupportSQLiteDatabase) {
        // Raw sql Query if needed like below
        database.execSQL("""CREATE TABLE IF NOT EXISTS Notification (
                                              id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                                              title TEXT NOT NULL,
                                              message TEXT NOT NULL,
                                              dateSend INTEGER NOT NULL,
                                              userId TEXT,
                                              seen INTEGER
                                        );""")

        database.execSQL("""  CREATE TABLE IF NOT EXISTS Ticket (
                                      id INTEGER PRIMARY KEY NOT NULL,
                                      title TEXT NOT NULL,
                                      userId TEXT NOT NULL,
                                      token TEXT,
                                      api INTEGER,
                                      device TEXT,
                                      dataLog TEXT,
                                      appVersion TEXT,
                                      startDate INTEGER,
                                      ticketStatus INTEGER NOT NULL
                            );""")

        database.execSQL("""CREATE TABLE IF NOT EXISTS TicketMessage (
                                  id INTEGER PRIMARY KEY NOT NULL,
                                  ticketId INTEGER NOT NULL,
                                  message TEXT NOT NULL,
                                  sendDate INTEGER,
                                  fromServer INTEGER NOT NULL
                                );""")
    }
}