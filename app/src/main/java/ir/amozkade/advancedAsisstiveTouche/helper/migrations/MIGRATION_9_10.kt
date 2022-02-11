package ir.amozkade.advancedAsisstiveTouche.helper.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_9_10: Migration = object : Migration(9, 10) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""ALTER TABLE `Setting` ADD COLUMN`googleApiRefreshTokenKey` TEXT""")
        database.execSQL("""ALTER TABLE `Setting` ADD COLUMN`googleAccessTokenResponseKey` TEXT""")
        database.execSQL("""ALTER TABLE `Setting` ADD COLUMN`isFailedUploadToGoogleDrive` INTEGER DEFAULT 0""")
        database.execSQL("""ALTER TABLE `Setting` ADD COLUMN`googleDriveUploadSessionURL` TEXT""")
    }
}