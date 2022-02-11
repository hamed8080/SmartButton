package ir.amozkade.advancedAsisstiveTouche.helper.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_10_11: Migration = object : Migration(10, 11) {


    //    Change Default Value of button width and height size
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""CREATE TABLE `Setting_New` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                                            `openAppCount` INTEGER NOT NULL,
                                            `firstOpen` INTEGER NOT NULL,
                                            `firebaseToken` TEXT,
                                            `isAutomaticEdgeEnabled` INTEGER NOT NULL DEFAULT 1,
                                            `autoAlphaButtonEnable` INTEGER NOT NULL DEFAULT 1,
                                            `doNotAskRate` INTEGER NOT NULL DEFAULT 0,
                                            `version` TEXT NOT NULL,
                                            `animationEnabled` INTEGER NOT NULL DEFAULT 1,
                                            `isLeftMenu` INTEGER NOT NULL,
                                            `singleTapAction` TEXT,
                                            `holdAction` TEXT,
                                            `doubleTapAction` TEXT,
                                            `startedByUser` INTEGER NOT NULL,
                                            `sourceLang` TEXT,
                                            `destLang` TEXT,
                                            `showSpeedEnabled` INTEGER NOT NULL,
                                            `speedTextSize` INTEGER NOT NULL DEFAULT 12,
                                            `panelAlpha` REAL NOT NULL DEFAULT 1,
                                            `enableCircularButton` INTEGER NOT NULL DEFAULT 1,
                                            `enableMarqueeAnimation` INTEGER NOT NULL DEFAULT 1,
                                            `pagerEnable` INTEGER NOT NULL,
                                            `panelButtonsColor` INTEGER NOT NULL DEFAULT 255255255,
                                            `panelColorOverlay` INTEGER NOT NULL DEFAULT 0,
                                            `buttonColorOverlay` INTEGER NOT NULL DEFAULT 255255255,
                                            `panelWidthPercent` REAL NOT NULL DEFAULT 80,
                                            `newFirebaseTokenSynced` INTEGER NOT NULL,
                                            `panelButtonsTextSize` INTEGER NOT NULL DEFAULT 36,
                                            `panelButtonsIconSize` INTEGER NOT NULL DEFAULT 64,
                                            `userSelectedFontName` TEXT,
                                            `userSelectedPanelImageName` TEXT,
                                            `userSelectedImageName` TEXT NOT NULL DEFAULT 'default',
                                            `showsPopupWindowsInBackgroundPermissionPage` INTEGER DEFAULT 0,
                                            `buttons` TEXT,
                                            `buttonWidth` INTEGER NOT NULL DEFAULT 168,
                                            `buttonHeight` INTEGER NOT NULL DEFAULT 168,
                                            `xPosition` INTEGER NOT NULL, `yPosition` INTEGER NOT NULL,
                                            `lang` TEXT NOT NULL DEFAULT 'en',
                                            `smartButtonActivated` INTEGER NOT NULL DEFAULT 0,
                                            `isLeitnerEnabled` INTEGER NOT NULL DEFAULT 0,
                                            `successPayRequest` TEXT,
                                            `order` TEXT,
                                            `googleApiRefreshTokenKey` TEXT,
                                            `googleAccessTokenResponseKey` TEXT,
                                            `isFailedUploadToGoogleDrive` INTEGER DEFAULT 0,
                                            `googleDriveUploadSessionURL` TEXT
                                            )""")
        database.execSQL("INSERT INTO `Setting_New` SELECT * FROM `Setting`")
        database.execSQL("ALTER TABLE `Setting` RENAME TO `Setting_Old`")
        database.execSQL("ALTER TABLE `Setting_New` RENAME TO `Setting`")
        database.execSQL("DROP TABLE IF EXISTS `Setting_Old`")

    }
}