package ir.amozkade.advancedAsisstiveTouche.mvvm.preferenceActivity

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ir.amozkade.advancedAsisstiveTouche.BuildConfig
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.models.*
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.models.ButtonInPreferenceModel.Companion.convertPreferenceToButtonInPanel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Entity
class Setting constructor(
        @PrimaryKey(autoGenerate = true) var id: Int,
        var openAppCount: Int,
        var firstOpen: Boolean,
        var firebaseToken: String?,
        @ColumnInfo(defaultValue = "1") var isAutomaticEdgeEnabled: Boolean,
        @ColumnInfo(defaultValue = "1") var autoAlphaButtonEnable: Boolean,
        @ColumnInfo(defaultValue = "0") var doNotAskRate: Boolean,
        var version: String,
        @ColumnInfo(defaultValue = "1") var animationEnabled: Boolean,
        var isLeftMenu: Boolean,
        var singleTapAction: String?,
        var holdAction: String?,
        var doubleTapAction: String?,
        var startedByUser: Boolean,
        var sourceLang: String?,
        var destLang: String?,
        var showSpeedEnabled: Boolean = false,
        @ColumnInfo(defaultValue = "12") var speedTextSize: Int,
        @ColumnInfo(defaultValue = "1") var panelAlpha: Float,
        @ColumnInfo(defaultValue = "1") var enableCircularButton: Boolean,
        @ColumnInfo(defaultValue = "1") var enableMarqueeAnimation: Boolean,
        var pagerEnable: Boolean,
        @ColumnInfo(defaultValue = "${255255255}") var panelButtonsColor: Int,
        @ColumnInfo(defaultValue = "${0}") var panelColorOverlay: Int,
        @ColumnInfo(defaultValue = "${255255255}") var buttonColorOverlay: Int,
        @ColumnInfo(defaultValue = "80") var panelWidthPercent: Float,
        var newFirebaseTokenSynced: Boolean,
        @ColumnInfo(defaultValue = "36") var panelButtonsTextSize: Int,
        @ColumnInfo(defaultValue = "64") var panelButtonsIconSize: Int,
        var userSelectedFontName: String?,
        var userSelectedPanelImageName: String?,
        @ColumnInfo(defaultValue = "default") var userSelectedImageName: String,
        @ColumnInfo(defaultValue = "0") var showsPopupWindowsInBackgroundPermissionPage: Boolean?,
        var buttons: String?,
        @ColumnInfo(defaultValue = "168") var buttonWidth: Int,
        @ColumnInfo(defaultValue = "168") var buttonHeight: Int,
        var xPosition: Int,
        var yPosition: Int,
        @ColumnInfo(defaultValue = "en") var lang: String,
        val googleApiRefreshTokenKey: String?,
        val googleAccessTokenResponseKey: String?,
        @ColumnInfo(defaultValue = "0") val isFailedUploadToGoogleDrive: Boolean?,
        val googleDriveUploadSessionURL: String?
) {

    fun getButtonPosition(): ButtonPosition {
        return ButtonPosition(xPosition, yPosition)
    }

    fun getButtonSize(): ButtonSize {
        return ButtonSize(buttonWidth, buttonHeight)
    }

    fun getSingleTapAction(context: Context): ButtonModelInPanel? {
        singleTapAction?.let {
            val buttonModelInPreferenceModel = ObjectMapper().readValue<ButtonInPreferenceModel>(it)
            return convertPreferenceToButtonInPanel(buttonModelInPreferenceModel, context)
        }
        return null
    }

    fun getDoubleTapAction(context: Context): ButtonModelInPanel? {
        doubleTapAction?.let {
            val buttonModelInPreferenceModel = ObjectMapper().readValue<ButtonInPreferenceModel>(it)
            return convertPreferenceToButtonInPanel(buttonModelInPreferenceModel, context)
        }
        return null
    }

    fun getHoldAction(context: Context): ButtonModelInPanel? {
        holdAction?.let {
            val buttonModelInPreferenceModel = ObjectMapper().readValue<ButtonInPreferenceModel>(it)
            return convertPreferenceToButtonInPanel(buttonModelInPreferenceModel, context)
        }
        return null
    }

    companion object {
        const val SingleTapActionKey = "SingleTapActionKey"
        const val HoldActionKey = "HoldActionKey"
        const val DoubleTapActionKey = "DoubleTapActionKey"

        fun getInitModel(): Setting {
            return Setting(
                    autoAlphaButtonEnable = true,
                    panelAlpha = 1f,
                    isAutomaticEdgeEnabled = true,
                    showSpeedEnabled = false,
                    buttonColorOverlay = Color.WHITE,
                    panelColorOverlay = Color.GRAY,
                    speedTextSize = 12,
                    panelButtonsIconSize = 64,
                    panelButtonsTextSize = 36,
                    singleTapAction = null,
                    holdAction = null,
                    doubleTapAction = null,
                    isLeftMenu = false,
                    userSelectedPanelImageName = null,
                    animationEnabled = true,
                    pagerEnable = false,
                    userSelectedImageName = "default",
                    lang = "en",
                    panelButtonsColor = Color.WHITE,
                    enableMarqueeAnimation = true,
                    buttons = null,
                    userSelectedFontName = null,
                    startedByUser = false,
                    buttonHeight = 168,
                    buttonWidth = 168,
                    xPosition = 0,
                    yPosition = 0,
                    firebaseToken = null,
                    panelWidthPercent = 80f,
                    sourceLang = "en",
                    destLang = "fa",
                    enableCircularButton = true,
                    id = 0,
                    newFirebaseTokenSynced = false,
                    doNotAskRate = false,
                    openAppCount = 0,
                    firstOpen = true,
                    showsPopupWindowsInBackgroundPermissionPage = false,
                    version = BuildConfig.VERSION_NAME,
                    googleAccessTokenResponseKey = null,
                    googleApiRefreshTokenKey = null,
                    isFailedUploadToGoogleDrive = false,
                    googleDriveUploadSessionURL = null

            )
        }

        @Suppress("DEPRECATION")
        fun appLaunchInit(defaultButtons: DefaultButtons, settingRepository: SettingRepository) {

            try {
                GlobalScope.launch(IO) {
                    val isFaFirstTime = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Resources.getSystem().configuration.locales[0].language.contains("fa")
                    } else {
                        Resources.getSystem().configuration.locale.language.contains("fa")
                    }
                    if (isFaFirstTime) {
                        settingRepository.setLanguage("fa")
                    }

                    if (settingRepository.getCashedModel().firstOpen) {
                        settingRepository.setFirstOpen(true)
                        settingRepository.setOpenAppCount(0)
                    } else {
                        val count = settingRepository.getCashedModel().openAppCount
                        settingRepository.setOpenAppCount(count + 1)
                    }

                    if (settingRepository.getCashedModel().singleTapAction == null) {
                        val button = defaultButtons.getDefaultButtons().first { it.buttonTypeName == ButtonModelInPanel.ButtonTypesName.OPEN_WINDOW }
                        val buttonInPreference = ObjectMapper().writeValueAsString(ButtonInPreferenceModel.convertButtonModelInPanelToPreference(button))
                        settingRepository.setSingleTapAction(buttonInPreference)
                    }
                    if (settingRepository.getCashedModel().holdAction == null) {
                        val button = defaultButtons.getDefaultButtons().first { it.buttonTypeName == ButtonModelInPanel.ButtonTypesName.HIDE_TO_NOTIFICATION }
                        val buttonInPreference = ObjectMapper().writeValueAsString(ButtonInPreferenceModel.convertButtonModelInPanelToPreference(button))
                        settingRepository.setHoldAction(buttonInPreference)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

}