package ir.amozkade.advancedAsisstiveTouche.mvvm.preferenceActivity.di

import androidx.room.*
import ir.amozkade.advancedAsisstiveTouche.mvvm.preferenceActivity.Setting

@Dao
interface SettingDao {

    @Query("UPDATE SETTING SET xPosition=:xPosition, yPosition=:yPosition WHERE id=:id")
    suspend fun saveButtonPosition(id: Int, xPosition: Int, yPosition: Int)

    @Query("UPDATE SETTING SET buttonWidth=:buttonWidth, buttonHeight=:buttonHeight WHERE id=:id")
    suspend fun saveButtonSize(buttonWidth: Int, buttonHeight: Int, id: Int)

    @Query("UPDATE SETTING SET panelButtonsColor=:color WHERE id=:id")
    suspend fun savePanelButtonsColor(color: Int, id: Int)

    @Query("UPDATE SETTING SET panelColorOverlay=:color WHERE id=:id")
    suspend fun saveSelectedPanelColor(color: Int, id: Int)

    @Query("UPDATE SETTING SET buttonColorOverlay=:color WHERE id=:id")
    suspend fun saveButtonColorOverlay(color: Int, id: Int)

    @Query("UPDATE SETTING SET speedTextSize=:textSize WHERE id=:id")
    suspend fun saveSpeedTextSize(textSize: Int, id: Int)

    @Query("UPDATE SETTING SET enableCircularButton=:enable WHERE id=:id")
    suspend fun saveEnableCircular(enable: Boolean, id: Int)

    @Query("UPDATE SETTING SET enableMarqueeAnimation=:enable WHERE id=:id")
    suspend fun saveEnableMarqueeAnimation(enable: Boolean, id: Int)

    @Query("UPDATE SETTING SET isLeftMenu=:enable WHERE id=:id")
    suspend fun saveLeftMenuEnable(enable: Boolean, id: Int)

    @Query("UPDATE SETTING SET pagerEnable=:enable WHERE id=:id")
    suspend fun savePagerEnable(enable: Boolean, id: Int)

    @Query("UPDATE SETTING SET animationEnabled=:enable WHERE id=:id")
    suspend fun saveAnimationEnabled(enable: Boolean, id: Int)

    @Query("UPDATE SETTING SET showSpeedEnabled=:enable WHERE id=:id")
    suspend fun saveSpeedEnable(enable: Boolean, id: Int)

    @Query("UPDATE SETTING SET isAutomaticEdgeEnabled=:enable WHERE id=:id")
    suspend fun saveAutomaticEdgeEnable(enable: Boolean, id: Int)

    @Query("UPDATE SETTING SET autoAlphaButtonEnable=:enable WHERE id=:id")
    suspend fun saveAutomaticAlphaEnable(enable: Boolean, id: Int)

    @Query("UPDATE SETTING SET panelButtonsIconSize=:size WHERE id=:id")
    suspend fun saveButtonIconSize(size: Int, id: Int)

    @Query("UPDATE SETTING SET panelWidthPercent=:width WHERE id=:id")
    suspend fun savePanelWidth(width: Float, id: Int)

    @Query("UPDATE SETTING SET panelButtonsTextSize=:textSize WHERE id=:id")
    suspend fun saveTextSizeInPanel(textSize: Int, id: Int)

    @Query("UPDATE SETTING SET firebaseToken=:firebaseToken,newFirebaseTokenSynced =1  WHERE id=:id")
    suspend fun saveNewFirebaseToken(firebaseToken: String, id: Int)

    @Query("UPDATE SETTING SET doNotAskRate=:doNotShow  WHERE id=:id")
    suspend fun setDoNotAskAgain(doNotShow: Int, id: Int)

    @Query("UPDATE SETTING SET newFirebaseTokenSynced=:synced  WHERE id=:id")
    suspend fun setNewFirebaseTokenSync(synced: Int, id: Int)

    @Query("UPDATE SETTING SET userSelectedImageName=null , userSelectedPanelImageName=null , userSelectedFontName=null    WHERE id=:id")
    suspend fun setDefaultTheme(id: Int)

    @Query("UPDATE SETTING SET userSelectedImageName=:imageName WHERE id=:id")
    suspend fun setUserSelectedImageName(imageName: String?, id: Int)

    @Query("UPDATE SETTING SET userSelectedPanelImageName=:imageName WHERE id=:id")
    suspend fun setUserSelectedPanelImageName(imageName: String?, id: Int)

    @Query("UPDATE SETTING SET userSelectedFontName=:fontName WHERE id=:id")
    suspend fun setUserSelectedFontName(fontName: String?, id: Int)

    @Query("UPDATE SETTING SET sourceLang=:code WHERE id=:id")
    suspend fun setSourceLang(code: String, id: Int)

    @Query("UPDATE SETTING SET destLang=:code WHERE id=:id")
    suspend fun setDestLang(code: String, id: Int)

    @Query("UPDATE SETTING SET buttonColorOverlay=:color WHERE id=:id")
    suspend fun setButtonColorOverlay(color: Int, id: Int)

    @Query("UPDATE SETTING SET panelColorOverlay=:color WHERE id=:id")
    suspend fun setPanelColorOverlay(color: Int, id: Int)

    @Query("UPDATE SETTING SET panelButtonsColor=:color WHERE id=:id")
    suspend fun setPanelButtonsColor(color: Int, id: Int)

    @Query("UPDATE SETTING SET firstOpen=:firstOpen WHERE id=:id")
    suspend fun setFirstOpen(firstOpen: Boolean, id: Int)

    @Query("UPDATE SETTING SET openAppCount=:count WHERE id=:id")
    suspend fun setOpenAppCount(count: Int, id: Int)

    @Query("UPDATE SETTING SET startedByUser=:startedByUser WHERE id=:id")
    suspend fun setStartedByUser(startedByUser: Boolean, id: Int)

    @Query("UPDATE SETTING SET showsPopupWindowsInBackgroundPermissionPage=:showed WHERE id=:id")
    suspend fun showPopupPermission(showed: Boolean, id: Int)

    @Query("UPDATE SETTING SET lang=:language WHERE id=:id")
    suspend fun setLanguage(language: String, id: Int)

    @Query("UPDATE SETTING SET singleTapAction=:button WHERE id=:id")
    suspend fun setSingleTapAction(button: String?, id: Int)

    @Query("UPDATE SETTING SET holdAction=:button WHERE id=:id")
    suspend fun setHoldAction(button: String?, id: Int)

    @Query("UPDATE SETTING SET doubleTapAction=:button WHERE id=:id")
    suspend fun setDoubleTapAction(button: String?, id: Int)

    @Query("UPDATE SETTING SET buttons=:buttons WHERE id=:id")
    suspend fun setButtons(buttons: String?, id: Int)

    @Query("UPDATE SETTING SET googleApiRefreshTokenKey=:refreshToken WHERE id=:id")
    suspend fun setGoogleApiRefreshTokenKey(refreshToken: String?, id: Int)

    @Query("UPDATE SETTING SET googleAccessTokenResponseKey=:accessToken WHERE id=:id")
    suspend fun setGoogleAccessTokenResponseKey(accessToken: String?, id: Int)

    @Query("UPDATE SETTING SET isFailedUploadToGoogleDrive=:failedUpload WHERE id=:id")
    suspend fun setFailedUploadToGoogleDrive(failedUpload: Boolean?, id: Int)

    @Query("UPDATE SETTING SET googleDriveUploadSessionURL=:sessionURL WHERE id=:id")
    suspend fun setGoogleDriveUploadSessionURL(sessionURL: String?, id: Int)

    @Insert
    suspend fun addInitSetting(setting: Setting)

    @Delete
    suspend fun deleteSetting(setting: Setting)

    @Query("SELECT * FROM SETTING")
    suspend fun getAll(): List<Setting>

}