package ir.amozkade.advancedAsisstiveTouche.helper.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_8_9: Migration = object : Migration(8, 9) {

    override fun migrate(database: SupportSQLiteDatabase) {

        database.execSQL("""CREATE TABLE `Setting` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
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
                                            `buttonWidth` INTEGER NOT NULL DEFAULT 64,
                                            `buttonHeight` INTEGER NOT NULL DEFAULT 64,
                                            `xPosition` INTEGER NOT NULL, `yPosition` INTEGER NOT NULL,
                                            `lang` TEXT NOT NULL DEFAULT 'en',
                                            `smartButtonActivated` INTEGER NOT NULL DEFAULT 0,
                                            `isLeitnerEnabled` INTEGER NOT NULL DEFAULT 0,
                                            `successPayRequest` TEXT,
                                            `order` TEXT
                                            )"""
        )

        database.execSQL("""CREATE TABLE `Profile` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                                                        `email` TEXT,
                                                        `phone` TEXT,
                                                        `lastLogin` INTEGER,
                                                        `img` TEXT,
                                                        `firstName` TEXT,
                                                        `lastName` TEXT
                                                        )"""
        )
    }
}