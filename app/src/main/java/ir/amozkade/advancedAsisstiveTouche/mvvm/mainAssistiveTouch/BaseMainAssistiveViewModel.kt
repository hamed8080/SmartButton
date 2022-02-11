package ir.amozkade.advancedAsisstiveTouche.mvvm.mainAssistiveTouch

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.admin.DeviceAdminReceiver
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.*
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.amozkade.advancedAsisstiveTouche.*
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.helper.Common
import ir.amozkade.advancedAsisstiveTouche.helper.api.DataState
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.FloatingWindow
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.Permissions
import ir.amozkade.advancedAsisstiveTouche.mvvm.editButtonPositions.adapters.GridEditButtonPositionsAdapter
import ir.amozkade.advancedAsisstiveTouche.mvvm.editButtonPositions.ItemSelectDelegate
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.models.ButtonInPreferenceModel
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.models.ButtonModelInPanel
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.models.DefaultButtons
import ir.amozkade.advancedAsisstiveTouche.mvvm.exception.ExceptionRepository
import ir.amozkade.advancedAsisstiveTouche.mvvm.mainAssistiveTouch.utils.MainResponse
import ir.amozkade.advancedAsisstiveTouche.mvvm.mainAssistiveTouch.utils.MainStateEvent
import ir.amozkade.advancedAsisstiveTouche.mvvm.mainAssistiveTouch.utils.UploadOrDownload
import ir.amozkade.advancedAsisstiveTouche.mvvm.preferenceActivity.Setting
import ir.amozkade.advancedAsisstiveTouche.mvvm.preferenceActivity.SettingRepository
import ir.amozkade.advancedAsisstiveTouche.mvvm.update.UpdateRepository
import ir.amozkade.advancedAsisstiveTouche.mvvm.update.UpdateResponse
import ir.amozkade.advancedAsisstiveTouche.mvvm.user.UserRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import java.io.File
import java.util.*
import javax.inject.Inject

@HiltViewModel
open class BaseMainAssistiveViewModel @Inject constructor(
    open val exceptionObserver: MutableLiveData<Throwable>,
    private val defaultButtons: DefaultButtons,
    private val commonHelper: Common,
    private val mainRepository: MainRepository,
    private val googleDriveRepository: GoogleDriveRepository,
    private val exceptionRepository: ExceptionRepository,
    private var userRepository: UserRepository,
    private val settingRepository: SettingRepository,
    private val common: Common,
    private val updateRepository: UpdateRepository

) : ViewModel(), ItemSelectDelegate {

    @SuppressLint("StaticFieldLeak")
    lateinit var context: AppCompatActivity
    private lateinit var dmp: DevicePolicyManager
    lateinit var installPackageContent: ActivityResultLauncher<Intent>
    lateinit var requestGoogleDriveConsent: ActivityResultLauncher<Intent>
    private lateinit var exportDatabaseContent: ActivityResultLauncher<Intent>
    private lateinit var importDatabaseContent: ActivityResultLauncher<Intent>

    open fun setActivityContext(context: AppCompatActivity) {
        this.context = context
        this.dmp = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        installPackageContent =
            context.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
                if (activityResult.resultCode == RESULT_OK) {
                    installPackage()
                }
            }
        requestGoogleDriveConsent =
            context.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
                if (activityResult.resultCode == RESULT_OK) {
                    googleSignInResult(activityResult.data)
                }
            }
        exportDatabaseContent =
            context.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
                if (activityResult.resultCode == RESULT_OK) {
                    DocumentFile.fromTreeUri(context, activityResult?.data?.data as Uri)
                        ?.let { documentFile ->
                            GlobalScope.launch {
                                export(documentFile)
                            }
                        }
                }
            }

        importDatabaseContent =
            context.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
                if (activityResult.resultCode == RESULT_OK) {
                    DocumentFile.fromTreeUri(context, activityResult.data?.data as Uri)
                        ?.let { documentFile ->
                            val documentFileDBFile = documentFile.findFile(AppDatabase.DATABASE_NAME)
                                ?: return@let
                            GlobalScope.launch(IO) {
                                import(documentFileDBFile)
                            }
                        }
                }
            }

        FloatingWindow.persistIntent = Intent(context, FloatingWindow::class.java)
        if (FloatingWindow.floatingWindowService != null) {
            model.serviceStarted = true
        }

        updateSelectedActionNameTitle(Setting.SingleTapActionKey)
        updateSelectedActionNameTitle(Setting.HoldActionKey)
        updateSelectedActionNameTitle(Setting.DoubleTapActionKey)
        showReviewAlertIfNeeded()
        GlobalScope.launch {
            userRepository.registerTokenToServer()
            common.backupExportDatabaseToPath()
        }
    }

    private val model: MainAssistiveModel = MainAssistiveModel()

    private val _response: MutableLiveData<DataState<MainResponse>> = MutableLiveData()
    val response: LiveData<DataState<MainResponse>> = _response

    private val _updateResponse: MutableLiveData<UpdateResponse> = MutableLiveData()
    val updateResponse: LiveData<UpdateResponse> = _updateResponse

    @Suppress("BlockingMethodInNonBlockingContext")
    fun setState(event: MainStateEvent) {
        val handler = CoroutineExceptionHandler { _, exception ->
            exceptionObserver.postValue(exception)
        }
        viewModelScope.launch(IO + handler) {
            supervisorScope {

                when (event) {
                    is MainStateEvent.RemoveApp -> removeApp()
                    is MainStateEvent.FirebaseAnalytics -> mainRepository.logActivity(event.activityName)
                    is MainStateEvent.ExchangeCode -> googleDriveRepository.getAccessTokenFromServer(
                        event.serverAuthCode
                    )
                    is MainStateEvent.UploadToGoogleDrive -> {
                        common.backupExportDatabaseToPath()
                        common.getFileFromExportedDatabase()?.let { dbFile ->
                            googleDriveRepository.uploadFile(dbFile).collect {
                                setUploadState(it)
                            }
                        }
                    }
                    is MainStateEvent.RequestGoogleApiRefreshToken -> {
                        googleDriveRepository.getAccessTokenWithRefreshToken()
                            .collect { isRefreshTokenExist ->
                                if (isRefreshTokenExist) {
                                    //updated google api access token
                                    if (event.uploadOrDownload == UploadOrDownload.Upload) {
                                        setState(MainStateEvent.UploadToGoogleDrive)
                                    } else {
                                        setState(MainStateEvent.DownloadBackupFromGoogleDrive)
                                    }
                                } else {
                                    //sign out to show consent dialog to user
                                    _response.postValue(DataState.Success(MainResponse.SignOutGoogle))
                                    model.googleDriveIsInUploadOrDownloading = false
                                }
                            }
                    }
                    is MainStateEvent.DownloadBackupFromGoogleDrive -> {
                        googleDriveRepository.downloadFileWithName(AppDatabase.DATABASE_NAME)
                            .collect {
                                setDownloadState(it)
                            }
                    }
                    is MainStateEvent.StartCheckingUpdateWorkManager -> startUpdateWorkManager()
                    is MainStateEvent.DownloadApkUpdate -> updateRepository.startDownloadApk(event.url)
                }
            }
        }
    }

    private suspend fun startUpdateWorkManager() {
        if (context.getString(R.string.market_package_name) == "NULL") {
            updateRepository.checkUpdate().collect {
                _updateResponse.postValue(it)
            }
        }
    }

    private fun setDownloadState(downloadState: GoogleDriveDownloadState) {
        when (downloadState) {
            is GoogleDriveDownloadState.Downloading -> {
                model.googleDriveUploadPercent = downloadState.progress
                model.googleDriveUploadPercentText = String.format(
                    context.getString(R.string.upload_google_drive_percent),
                    downloadState.progress.toString()
                )
                model.googleDriveIsInUploadOrDownloading = true
            }
            is GoogleDriveDownloadState.Failed -> {
                model.googleDriveIsInUploadOrDownloading = false
            }
            is GoogleDriveDownloadState.UnAuthorized -> {
                setState(MainStateEvent.RequestGoogleApiRefreshToken(UploadOrDownload.Download))
            }
            is GoogleDriveDownloadState.Finished -> {
                model.googleDriveIsInUploadOrDownloading = false
            }
        }
    }

    private fun setUploadState(uploadState: GoogleDriveUploadState) {
        when (uploadState) {
            is GoogleDriveUploadState.Uploading -> {
                model.googleDriveUploadPercent = uploadState.progress
                model.googleDriveUploadPercentText = String.format(
                    context.getString(R.string.upload_google_drive_percent),
                    uploadState.progress.toString()
                )
                model.googleDriveIsInUploadOrDownloading = true
            }
            is GoogleDriveUploadState.Failed -> {
                model.googleDriveIsInUploadOrDownloading = false
            }
            is GoogleDriveUploadState.UnAuthorized -> {
                setState(MainStateEvent.RequestGoogleApiRefreshToken(UploadOrDownload.Upload))
            }
            is GoogleDriveUploadState.SignOutFromGoogleSignInToGetNewToken -> {
                _response.postValue(DataState.Success(MainResponse.SignOutGoogle))
                model.googleDriveIsInUploadOrDownloading = false
            }
            is GoogleDriveUploadState.Finished -> {
                model.googleDriveIsInUploadOrDownloading = false
            }
        }
    }

    fun getModel(): MainAssistiveModel {
        return model
    }

    fun enableDisableTaped() {
        if (model.serviceStarted) {
            disableTaped()
        } else {
            enableTaped()
        }
    }

    private fun enableTaped() {
        initRequestPermissionsIfNeeded()
        try {
            if (!Permissions.openOverlayPermissionIfNotGranted(
                    context,
                    Permissions.overlayRequestPermissionCode
                )
            ) return
            if (FloatingWindow.floatingWindowService == null) {// floatingWindowService check if service already is running don't start service multiple time
                FloatingWindow.persistIntent?.removeExtra("FROM_NOTIFICATION")
                FloatingWindow.persistIntent?.putExtra("USER_TAP_ON_START", "OK")
                context.startService(FloatingWindow.persistIntent)
                viewModelScope.launch(IO) {
                    settingRepository.setStartedByUser(true)
                }
                model.serviceStarted = true
            }
        } catch (e: java.lang.Exception) {
            exceptionRepository.saveException(e)
        }
    }

    private fun disableTaped() {
        try {
            context.stopService(FloatingWindow.persistIntent)
            FloatingWindow.floatingWindowService = null
            model.serviceStarted = false
            viewModelScope.launch(IO) {
                settingRepository.setStartedByUser(false)
            }
        } catch (e: Exception) {
            exceptionRepository.saveException(e)
        }
    }

    private fun removeApp() {
        setState(MainStateEvent.FirebaseAnalytics("REMOVE_APP"))
        val mComponentName = ComponentName(context, DeviceAdminReceiver::class.java)
        if (dmp.isAdminActive(mComponentName)) {
            dmp.removeActiveAdmin(mComponentName)
        }
        val intent = Intent(Intent.ACTION_DELETE)
        intent.data = Uri.parse("package:" + context.packageName)
        context.startActivity(intent)
    }

    fun openGestureActionAlert(id: Int) {
        GridEditButtonPositionsAdapter.openChoseActionAlert(
            context as MainAssistiveTouchActivity,
            defaultButtons,
            this,
            id,
            settingRepository,
            false
        )
    }

    private fun installPackage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            updateRepository.installAndroidQ(context as MainAssistiveTouchActivity)
        }
    }

    private fun googleSignInResult(data: Intent?) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)
            setState(MainStateEvent.ExchangeCode(account.serverAuthCode))
        } catch (e: ApiException) {
            Log.w("TAG", "signInResult:failed code=" + e.statusCode)
        }
    }

    private suspend fun export(documentFile: DocumentFile) {
        commonHelper.exportDatabaseToPath(documentFile).collect {
            _response.postValue(it)
        }
    }

    private suspend fun import(documentFile: DocumentFile) {
        commonHelper.importDatabaseToPath(documentFile).collect {
            _response.postValue(it)
        }
    }

    @Suppress("DEPRECATION")
    fun openFileExportPath() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
            exportDatabaseContent.launch(intent)
        } else {
            val file =
                File(Environment.getExternalStorageDirectory().absolutePath + "/${AppDatabase.DATABASE_NAME}")
            GlobalScope.launch {
                export(DocumentFile.fromFile(file))
            }
        }
    }

    @Suppress("DEPRECATION")
    fun openFileImportPath() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
            importDatabaseContent.launch(intent)
        } else {
            val file =
                File(Environment.getExternalStorageDirectory().absolutePath + "/${AppDatabase.DATABASE_NAME}")
            GlobalScope.launch {
                import(DocumentFile.fromFile(file))
            }
        }
    }

    private fun showReviewAlertIfNeeded() {
        if (context.getString(R.string.market_package_name) == "NULL") return
        if (settingRepository.getCashedModel().openAppCount > 1 && !settingRepository.getCashedModel().doNotAskRate) {
            val btnDoNotAskAgain = MaterialButton(
                ContextThemeWrapper(
                    context,
                    R.style.Widget_MaterialComponents_Button_TextButton
                )
            )
            btnDoNotAskAgain.text = context.getString(R.string.do_not_ask_rate)
            btnDoNotAskAgain.background = null
            btnDoNotAskAgain.setTextColor(ContextCompat.getColor(context, R.color.black))
            val isFa = Locale.getDefault().language.contains("fa")
            btnDoNotAskAgain.typeface = ResourcesCompat.getFont(
                context,
                if (isFa) R.font.iransans_bold else R.font.sf_pro_rounded_bold
            )
            _response.postValue(DataState.Success(MainResponse.ShowReviewAlert(btnDoNotAskAgain)))
        }
    }

    private fun initRequestPermissionsIfNeeded() {
        Permissions.requestAllPermissions(context, Permissions.overlayRequestPermissionCode)
        if (Permissions.isMiuiWithApi29OrMore() && settingRepository.getCashedModel().showsPopupWindowsInBackgroundPermissionPage == null) {
            viewModelScope.launch(IO) {
                settingRepository.showPopupPermission(true)
            }
            Permissions.goToXiaomiPermissions(context)
        }
    }

    enum class ButtonTypeGesture {
        TAP, HOLD, DOUBLE
    }

    override fun onSelectButtonItem(button: ButtonModelInPanel, selectedPositionInMenu: Int) {
        val preferenceKey = convertPositionIdToPreferenceKey(selectedPositionInMenu)
        val buttonInPreference = ObjectMapper().writeValueAsString(
            ButtonInPreferenceModel.convertButtonModelInPanelToPreference(button)
        )
        viewModelScope.launch(IO) {
            when (preferenceKey) {
                Setting.SingleTapActionKey -> {
                    settingRepository.setSingleTapAction(buttonInPreference)
                }
                Setting.HoldActionKey -> {
                    settingRepository.setHoldAction(buttonInPreference)
                }
                Setting.DoubleTapActionKey -> {
                    settingRepository.setDoubleTapAction(buttonInPreference)
                }
            }
        }
        updateSelectedActionNameTitle(preferenceKey)
        FloatingWindow.restartButtonService(context, settingRepository)
    }

    override fun onDeleteButton(selectedPositionInMenu: Int) {
        FloatingWindow.restartButtonService(context, settingRepository)
        val preferenceKey = convertPositionIdToPreferenceKey(selectedPositionInMenu)
        updateSelectedActionNameTitle(preferenceKey)
    }

    private fun convertPositionIdToPreferenceKey(id: Int): String {
        var preferenceKey = ""
        when (id) {
            ButtonTypeGesture.TAP.ordinal -> {
                preferenceKey = Setting.SingleTapActionKey
            }
            ButtonTypeGesture.HOLD.ordinal -> {
                preferenceKey = Setting.HoldActionKey
            }
            ButtonTypeGesture.DOUBLE.ordinal -> {
                preferenceKey = Setting.DoubleTapActionKey
            }
        }
        return preferenceKey
    }

    private fun updateSelectedActionNameTitle(preferenceKey: String) {
        when (preferenceKey) {
            Setting.SingleTapActionKey -> {
                model.singleTapSelectedActionTitle =
                    getTitleOfButton(settingRepository.getCashedModel().singleTapAction)
            }
            Setting.HoldActionKey -> {
                model.holdSelectedActionTitle =
                    getTitleOfButton(settingRepository.getCashedModel().holdAction)
            }
            Setting.DoubleTapActionKey -> {
                model.doubleTapSelectedActionTitle =
                    getTitleOfButton(settingRepository.getCashedModel().doubleTapAction)
            }
        }
    }

    private fun getTitleOfButton(button: String?): String? {
        button?.let {
            val preferenceButton = ObjectMapper().readValue<ButtonInPreferenceModel>(button)
            val buttonModelInPanel =
                ButtonInPreferenceModel.convertPreferenceToButtonInPanel(preferenceButton, context)
            return buttonModelInPanel.getTitleBaseOnButtonType(context)
        }
        return null
    }

    fun getVersion(context: Context): String {
        return String.format(
            "${context.getString(R.string.version)}: %s c-%s",
            BuildConfig.VERSION_NAME,
            ir.mobitrain.applicationcore.BuildConfig.VERSION_NAME
        )
    }
}