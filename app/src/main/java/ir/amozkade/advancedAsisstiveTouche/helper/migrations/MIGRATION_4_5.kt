package ir.amozkade.advancedAsisstiveTouche.helper.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_4_5: Migration = object : Migration(4,5) {

    override fun migrate(database: SupportSQLiteDatabase) {
        // Raw sql Query if needed like below
        database.execSQL("DROP TABLE IF EXISTS QuestionAnswer")
        database.execSQL("DROP TABLE IF EXISTS Dictionary")
        database.execSQL("""CREATE TABLE IF NOT EXISTS Dictionary (
              id integer PRIMARY KEY NOT NULL,
              name text NOT NULL,
              fileName text NOT NULL,
              fileSize integer NOT NULL,
              entryCount integer NOT NULL
            );
        """)
        database.execSQL("""CREATE TABLE IF NOT EXISTS QuestionAnswer (
                       question TEXT  NOT NULL,
                       answer TEXT ,
                       passedTime INTEGER,
                       levelId INTEGER NOT NULL,
                       categoryId INTEGER NOT NULL,
                       FOREIGN KEY (categoryId) REFERENCES Category(id) ON DELETE CASCADE ON UPDATE CASCADE,
                       PRIMARY KEY (question, categoryId)
                        )""")
        database.execSQL("CREATE INDEX index_QuestionAnswer_categoryId ON QuestionAnswer (categoryId)")
    }
}