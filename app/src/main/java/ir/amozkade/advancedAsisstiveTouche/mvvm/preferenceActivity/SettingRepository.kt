package ir.amozkade.advancedAsisstiveTouche.mvvm.preferenceActivity

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.models.ButtonPosition
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.models.ButtonSize
import ir.amozkade.advancedAsisstiveTouche.mvvm.preferenceActivity.di.SettingDao
import ir.mobitrain.applicationcore.helper.Converters.Companion.convertIntToDP
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class SettingRepository @Inject constructor(private val settingDao: SettingDao, @ApplicationContext private val context: Context) {

    private lateinit var cashedModel: Setting


    init {
        GlobalScope.launch(IO) {
            cashedModel = getSetting()
        }
    }

    fun getCashedModel(): Setting {
        return cashedModel
    }

    private suspend fun getSetting(): Setting {
        val setting = settingDao.getAll().firstOrNull()
        return if (setting == null) {
            //insert
            settingDao.addInitSetting(Setting.getInitModel())
            settingDao.getAll().first()
        } else {
            setting
        }
    }

    suspend fun saveButtonPosition(position: ButtonPosition) {
        settingDao.saveButtonPosition(cashedModel.id, position.x, position.y)
        cashedModel = getSetting()
    }

    suspend fun saveUserButtonSize(size: ButtonSize) {
        settingDao.saveButtonSize(convertIntToDP(size.width , context).toInt(), convertIntToDP(size.height , context).toInt(), cashedModel.id)
        cashedModel = getSetting()
    }

    suspend fun savePanelButtonsColor(color: Int) {
        settingDao.savePanelButtonsColor(color, cashedModel.id)
        cashedModel = getSetting()
    }

    suspend fun saveSelectedPanelColor(color: Int) {
        settingDao.saveSelectedPanelColor(color, cashedModel.id)
        cashedModel = getSetting()
    }

    suspend fun saveButtonColorOverlay(color: Int) {
        settingDao.saveButtonColorOverlay(color, cashedModel.id)
        cashedModel = getSetting()
    }

    suspend fun saveSpeedTextSize(textSize: Int) {
        settingDao.saveSpeedTextSize(textSize, cashedModel.id)
        cashedModel = getSetting()
    }

    suspend fun saveEnableCircular(enable: Boolean) {
        settingDao.saveEnableCircular(enable, cashedModel.id)
        cashedModel = getSetting()
    }

    suspend fun saveEnableMarqueeAnimation(enable: Boolean) {
        settingDao.saveEnableMarqueeAnimation(enable, cashedModel.id)
        cashedModel = getSetting()
    }

    suspend fun saveLeftMenuEnable(enable: Boolean) {
        settingDao.saveLeftMenuEnable(enable, cashedModel.id)
        cashedModel = getSetting()
    }

    suspend fun savePagerEnable(enable: Boolean) {
        settingDao.savePagerEnable(enable, cashedModel.id)
        cashedModel = getSetting()
    }

    suspend fun saveAnimationEnabled(enable: Boolean) {
        settingDao.saveAnimationEnabled(enable, cashedModel.id)
        cashedModel = getSetting()
    }

    suspend fun saveSpeedEnable(enable: Boolean) {
        settingDao.saveSpeedEnable(enable, cashedModel.id)
        cashedModel = getSetting()
    }

    suspend fun saveAutomaticEdgeEnable(enable: Boolean) {
        settingDao.saveAutomaticEdgeEnable(enable, cashedModel.id)
        cashedModel = getSetting()
    }

    suspend fun saveAutomaticAlphaEnable(enable: Boolean) {
        settingDao.saveAutomaticAlphaEnable(enable, cashedModel.id)
        cashedModel = getSetting()
    }

    suspend fun saveButtonIconSize(size: Int) {
        settingDao.saveButtonIconSize(size, cashedModel.id)
        cashedModel = getSetting()
    }

    suspend fun savePanelWidth(width: Float) {
        settingDao.savePanelWidth(width, cashedModel.id)
        cashedModel = getSetting()
    }

    suspend fun saveTextSizeInPanel(textSize: Int) {
        settingDao.saveTextSizeInPanel(textSize, cashedModel.id)
        cashedModel = getSetting()
    }

    suspend fun saveNewFirebaseToken(firebaseToken: String) {
        settingDao.saveNewFirebaseToken(firebaseToken, cashedModel.id)
        cashedModel = getSetting()
    }

    suspend fun setDoNotAskAgain(doNotShow: Boolean) {
        settingDao.setDoNotAskAgain(if (doNotShow) 1 else 0, cashedModel.id)
        cashedModel = getSetting()
    }

    suspend fun setNewFirebaseTokenSync(synced: Boolean) {
        settingDao.setNewFirebaseTokenSync(if (synced) 1 else 0, cashedModel.id)
        cashedModel = getSetting()
    }

    suspend fun setDefaultTheme() {
        settingDao.setDefaultTheme(cashedModel.id)
        cashedModel = getSetting()
    }

    suspend fun setUserSelectedImageName(imageName: String?) {
        settingDao.setUserSelectedImageName(imageName, cashedModel.id)
        cashedModel = getSetting()
    }

    suspend fun setUserSelectedPanelImageName(imageName: String?) {
        settingDao.setUserSelectedPanelImageName(imageName, cashedModel.id)
        cashedModel = getSetting()
    }

    suspend fun setUserSelectedFontName(fontName: String?) {
        settingDao.setUserSelectedFontName(fontName, cashedModel.id)
        cashedModel = getSetting()
    }

    suspend fun setSourceLang(code: String) {
        settingDao.setSourceLang(code, cashedModel.id)
        cashedModel = getSetting()
    }

    suspend fun setDestLang(code: String) {
        settingDao.setDestLang(code, cashedModel.id)
        cashedModel = getSetting()
    }

    suspend fun setButtonColorOverlay(color: Int) {
        settingDao.setButtonColorOverlay(color, cashedModel.id)
        cashedModel = getSetting()
    }

    suspend fun setPanelColorOverlay(color: Int) {
        settingDao.setPanelColorOverlay(color, cashedModel.id)
        cashedModel = getSetting()
    }

    suspend fun setPanelButtonsColor(color: Int) {
        settingDao.setPanelButtonsColor(color, cashedModel.id)
        cashedModel = getSetting()
    }

    suspend fun setFirstOpen(firstOpen: Boolean) {
        settingDao.setFirstOpen(firstOpen, cashedModel.id)
        cashedModel = getSetting()
    }

    suspend fun setOpenAppCount(count: Int) {
        settingDao.setOpenAppCount(count, cashedModel.id)
        cashedModel = getSetting()
    }

    suspend fun setStartedByUser(startedByUser: Boolean) {
        settingDao.setStartedByUser(startedByUser, cashedModel.id)
        cashedModel = getSetting()
    }

    suspend fun showPopupPermission(showed: Boolean) {
        settingDao.showPopupPermission(showed, cashedModel.id)
        cashedModel = getSetting()
    }

    suspend fun setLanguage(language: String) {
        settingDao.setLanguage(language, cashedModel.id)
        cashedModel = getSetting()
    }

    suspend fun setSingleTapAction(button: String?) {
        settingDao.setSingleTapAction(button, cashedModel.id)
        cashedModel = getSetting()
    }

    suspend fun setDoubleTapAction(button: String?) {
        settingDao.setDoubleTapAction(button, cashedModel.id)
        cashedModel = getSetting()
    }

    suspend fun setHoldAction(button: String?) {
        settingDao.setHoldAction(button, cashedModel.id)
        cashedModel = getSetting()
    }

    suspend fun setButtons(buttons: String?) {
        settingDao.setButtons(buttons, cashedModel.id)
        cashedModel = getSetting()
    }

    suspend fun setGoogleApiRefreshTokenKey(refreshToken: String?) {
        settingDao.setGoogleApiRefreshTokenKey(refreshToken, cashedModel.id)
        cashedModel = getSetting()
    }

    suspend fun setGoogleAccessToken(accessToken: String?) {
        settingDao.setGoogleAccessTokenResponseKey(accessToken, cashedModel.id)
        cashedModel = getSetting()
    }

    suspend fun setFailedUploadToGoogleDrive(failedUpload: Boolean?) {
        settingDao.setFailedUploadToGoogleDrive(failedUpload, cashedModel.id)
        cashedModel = getSetting()
    }

    suspend fun setGoogleDriveUploadSessionURL(sessionURL: String?) {
        settingDao.setGoogleDriveUploadSessionURL(sessionURL, cashedModel.id)
        cashedModel = getSetting()
    }

}