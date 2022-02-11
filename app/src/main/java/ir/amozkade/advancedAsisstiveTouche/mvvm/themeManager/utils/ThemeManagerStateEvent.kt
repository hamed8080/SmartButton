package ir.amozkade.advancedAsisstiveTouche.mvvm.themeManager.utils

import ir.amozkade.advancedAsisstiveTouche.mvvm.themeManager.models.Theme
import ir.amozkade.advancedAsisstiveTouche.mvvm.themeManager.models.ThemePack


sealed class ThemeManagerStateEvent {

    object GetAllThemes:ThemeManagerStateEvent()
    data class DownloadTheme(val theme: Theme):ThemeManagerStateEvent()
    data class DownloadThemePack(val themePack: ThemePack) : ThemeManagerStateEvent()
}