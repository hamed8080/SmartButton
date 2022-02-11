package ir.amozkade.advancedAsisstiveTouche.helper.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_6_7: Migration = object : Migration(6, 7) {

    override fun migrate(database: SupportSQLiteDatabase) {
        // Raw sql Query if needed like below
        database.execSQL("""ALTER TABLE Category RENAME TO Leitner""")

        //LEVEL
        database.execSQL("""CREATE TABLE LEVEL_TEMP (
                                            id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL ,
                                            level INTEGER NOT NULL,
                                            time INTEGER NOT NULL,
                                            leitnerId INTEGER NOT NULL
                                            );""")
        database.execSQL("""INSERT INTO LEVEL_TEMP(id, level , time,leitnerId)
                                        SELECT id, level , time , categoryId FROM LEVEL; """)
        database.execSQL("""DROP TABLE LEVEL """)
        database.execSQL("""ALTER TABLE LEVEL_TEMP RENAME TO LEVEL """)

        //Question Answer
        database.execSQL("""CREATE TABLE QuestionAnswer_TEMP(
                                        question TEXT NOT NULL,
                                        answer TEXT ,
                                        passedTime INTEGER,
                                        leitnerId INTEGER NOT NULL,
                                        levelId INTEGER NOT NULL,
                                        PRIMARY KEY(question,leitnerId),
                                        FOREIGN KEY(leitnerId) REFERENCES Leitner(id) ON UPDATE CASCADE ON DELETE CASCADE
                                    );""")
        database.execSQL("""INSERT INTO QuestionAnswer_TEMP(question, answer,passedTime,leitnerId,levelId)
                                            SELECT question,answer,passedTime,categoryId,levelId
                                                FROM QuestionAnswer""")
        database.execSQL("""DROP TABLE QuestionAnswer """)
        database.execSQL("""ALTER TABLE QuestionAnswer_TEMP RENAME TO QuestionAnswer """)
        database.execSQL("""CREATE INDEX `index_QuestionAnswer_leitnerId` ON `QuestionAnswer` (`leitnerId`)""")
    }
}