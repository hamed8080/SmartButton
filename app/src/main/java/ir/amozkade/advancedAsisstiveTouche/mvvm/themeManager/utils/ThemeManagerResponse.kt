package ir.amozkade.advancedAsisstiveTouche.mvvm.themeManager.utils

import ir.amozkade.advancedAsisstiveTouche.mvvm.themeManager.models.Section
import ir.amozkade.advancedAsisstiveTouche.mvvm.themeManager.models.Theme
import ir.amozkade.advancedAsisstiveTouche.mvvm.themeManager.models.ThemePack

sealed class ThemeManagerResponse{
    data class Themes(val themePacks:List<ThemePack>,val themes:List<Section<Theme>>):ThemeManagerResponse()
    data class SucceededDownloadTheme(val theme: Theme):ThemeManagerResponse()
    data class SucceededDownloadThemePack(val themePack: ThemePack):ThemeManagerResponse()
}
