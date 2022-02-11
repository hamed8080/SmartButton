package ir.amozkade.advancedAsisstiveTouche.mvvm.mainAssistiveTouch

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import ir.amozkade.advancedAsisstiveTouche.*
import ir.amozkade.advancedAsisstiveTouche.databinding.ActivityMainBinding
import ir.amozkade.advancedAsisstiveTouche.helper.api.DataState
import ir.amozkade.advancedAsisstiveTouche.mvvm.BaseActivity
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.FloatingWindow
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.leitners.LeitnerActivity
import ir.amozkade.advancedAsisstiveTouche.mvvm.editButtonPositions.EditButtonPositionsActivity
import ir.amozkade.advancedAsisstiveTouche.mvvm.language.LanguageActivity
import ir.amozkade.advancedAsisstiveTouche.mvvm.mainAssistiveTouch.utils.MainResponse
import ir.amozkade.advancedAsisstiveTouche.mvvm.mainAssistiveTouch.utils.MainStateEvent
import ir.amozkade.advancedAsisstiveTouche.mvvm.notification.NotificationActivity
import ir.amozkade.advancedAsisstiveTouche.mvvm.permissions.PermissionActivity
import ir.amozkade.advancedAsisstiveTouche.mvvm.preferenceActivity.PreferenceActivity
import ir.amozkade.advancedAsisstiveTouche.mvvm.preferenceActivity.SettingRepository
import ir.amozkade.advancedAsisstiveTouche.mvvm.themeManager.ThemeManagerActivity
import ir.amozkade.advancedAsisstiveTouche.mvvm.tickets.TicketsActivity
import ir.amozkade.advancedAsisstiveTouche.mvvm.update.UpdateApp
import ir.amozkade.advancedAsisstiveTouche.mvvm.update.UpdateResponse
import ir.amozkade.advancedAsisstiveTouche.mvvm.user.login.LoginActivity
import ir.amozkade.advancedAsisstiveTouche.mvvm.user.profile.ProfileActivity
import ir.mobitrain.applicationcore.alertDialog.AlertDialogDelegate
import ir.mobitrain.applicationcore.alertDialog.CustomAlertDialog
import ir.mobitrain.applicationcore.api.JWT
import ir.mobitrain.applicationcore.helper.animations.CommonAnimation
import ir.mobitrain.applicationcore.logger.LoggerActivity
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
class MainAssistiveTouchActivity : BaseActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient

    @Inject
    @GoogleDriveGoogleSignInOption
    lateinit var gso: GoogleSignInOptions

    @Inject
    @AppDir
    lateinit var appDir: String

    @Inject
    lateinit var settingRepository: SettingRepository

    private val flagRestartActivityChangeLocale = "ACTIVITY_RESTART_FOR_LOCALE_CHANGE"
    private lateinit var mBinding: ActivityMainBinding
    val viewModel by viewModels<MainAssistiveViewModel>()

    companion object {
        var mainActivityInstance: WeakReference<MainAssistiveTouchActivity>? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setActivityContext(this)
        setupUI()
        setupObservers()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun setupUI() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mBinding.actionBar.btnNotifSetOnClickListener { openActivity(NotificationActivity::class.java) }
        mBinding.vm = viewModel
        if (BuildConfig.DEBUG) {
            mBinding.actionBar.mBinding.btnLogger.visibility = View.VISIBLE
            mBinding.actionBar.mBinding.btnLogger.setOnClickListener { goToLogger() }
        }
        mBinding.actionBar.mBinding.title.gravity = Gravity.CENTER
        mBinding.btnTheme.setOnClickListener { openActivity(ThemeManagerActivity::class.java) }
        mBinding.btnPreference.setOnClickListener { openActivity(PreferenceActivity::class.java) }
        mBinding.btnLanguage.setOnClickListener { openActivity(LanguageActivity::class.java) }
        mBinding.btnEditPosition.setOnClickListener { openActivity(EditButtonPositionsActivity::class.java) }
        mBinding.btnPermissions.setOnClickListener { openActivity(PermissionActivity::class.java) }
        mBinding.btnTicket.setOnClickListener { openActivity(TicketsActivity::class.java) }
        mBinding.btnTelegram.setOnClickListener { openTelegram() }
        mBinding.btnRemoveApp.setOnClickListener { removeAppDialog() }

        mBinding.btnBackUpToGoogleDrive.setOnClickListener {
            val account = GoogleSignIn.getLastSignedInAccount(this)
            // user signed in already
            if (account != null) {
                viewModel.setState(MainStateEvent.UploadToGoogleDrive)
            } else {
                requestGoogleDriveApiConsentScreen()
            }
        }

        mBinding.btnImportBackupFromGoogleDrive.setOnClickListener {
            val account = GoogleSignIn.getLastSignedInAccount(this)
            // user signed in already
            if (account != null) {
                viewModel.setState(MainStateEvent.DownloadBackupFromGoogleDrive)
            } else {
                requestGoogleDriveApiConsentScreen()
            }
        }

        mBinding.btnLeitner.setOnClickListener {
            openActivity(LeitnerActivity::class.java)
        }
        mBinding.actionBar.mBinding.btnLogin.setOnClickListener { openActivity(if (JWT.instance.computedJWT == null) LoginActivity::class.java else ProfileActivity::class.java) }
        if (intent.getBooleanExtra(flagRestartActivityChangeLocale, false)) {
            FloatingWindow.restartButtonService(this, settingRepository)
        }
//        from quick setting in notification center
        FloatingWindow.isServiceStarted.observe(this) {
            viewModel.getModel().serviceStarted = it
        }
        checkWritePermissionAndCheckForUpdate()
        mainActivityInstance = WeakReference(this)
        CommonAnimation.doAnimation(mBinding.mainContainer)
    }


    private fun setupObservers() {
        viewModel.exceptionObserver.observe(this) {
            manageDataState(DataState.Error(it as Exception))
        }
        viewModel.response.observe(this) { dataState ->
            if (dataState is DataState.Success) {
                when (dataState.data) {
                    is MainResponse.ShowReviewAlert -> {
                        showReviewAlert(dataState.data.btn)
                    }
                    is MainResponse.ExportSucceeded -> {
                        onSuccessExport(dataState.data.exportPath)
                    }
                    is MainResponse.Imported -> {
                        onSuccessImport()
                    }
                    is MainResponse.SignOutGoogle -> {
                        signOutGoogleClient()
                    }
                }
            }
            manageDataState(dataState)
        }

        viewModel.updateResponse.observe(this) { dataState ->
            if (dataState is UpdateResponse && dataState is UpdateResponse.ShowMandatoryDialog) {
                showMandatoryUpdate()
            } else if (dataState is UpdateResponse.Downloading) {

            } else if (dataState is UpdateResponse.ShowUpdateAlert) {
                changeLog(dataState.updateApp)
            }

        }
    }

    private fun goToLogger() {
        val intent = Intent(cto, LoggerActivity::class.java)
        startActivity(intent)
    }

    private fun changeLog(updateApp: UpdateApp) {
        var message = ""
        updateApp.changes.forEach { message += "- ${it.description}\n" }
        CustomAlertDialog.showDialog(context = this,
                title = getString(R.string.update_app),
                message = message,
                submitTitle = getString(R.string.update),
                submitTextColorId = ContextCompat.getColor(this, R.color.red),
                cancelTextColorId = ContextCompat.getColor(this, R.color.primary_dark_color),
                cancelable = true,
                imageId = R.drawable.force_update, delegate = object : AlertDialogDelegate {
            override fun alertCompletion(type: AlertDialogDelegate.AlertTapType, extraButton: MaterialButton?) {
                if (type == AlertDialogDelegate.AlertTapType.Submit) {
                    viewModel.setState(MainStateEvent.DownloadApkUpdate(updateApp.url))
                }
            }
        })
    }

    private fun showMandatoryUpdate() {
        mBinding.mainContainer.visibility = View.GONE
        mBinding.mandatoryUpdate.visibility = View.VISIBLE
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && permissions.isNotEmpty() && permissions[0] == Manifest.permission.WRITE_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                viewModel.setState(MainStateEvent.StartCheckingUpdateWorkManager)
            } else {
                CustomAlertDialog.showDialog(context = this, title = getString(R.string.permission_denied_title), message = getString(R.string.writeExternalPermission), imageId = R.drawable.img_write_permission)
            }
        }
    }

    private fun checkWritePermissionAndCheckForUpdate() {
        val writePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (writePermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        } else {
            viewModel.setState(MainStateEvent.StartCheckingUpdateWorkManager)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        CustomAlertDialog.dismissDialog()
        mainActivityInstance = null
    }

    @Suppress("DEPRECATION")
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent?.action == "LANGUAGE_CHANGED") {
            val restartIntent = Intent(this, MainAssistiveTouchActivity::class.java)
            restartIntent.putExtra(flagRestartActivityChangeLocale, true)
            finish()
            startActivity(restartIntent)
        }
    }

    private fun onSuccessExport(uri: Uri) {
        var path = uri.path?.split(":")?.lastOrNull() ?: return
        path = if (path == "") "/" else "/${path}/"
        showWarn(getString(R.string.export_database), String.format(getString(R.string.export_database_path), path))
    }

    private fun onSuccessImport() {
        showWarn(getString(R.string.import_database), String.format(getString(R.string.import_database_path)))
    }

    private fun removeAppDialog() {
        CustomAlertDialog.showDialog(this,
                title = getString(R.string.delete),
                message = getString(R.string.delete_app_subtitle),
                submitTitle = getString(R.string.delete),
                submitTextColorId = ContextCompat.getColor(this, R.color.red),
                cancelTextColorId = ContextCompat.getColor(this, R.color.primary_color),
                imageId = R.drawable.img_delete,
                delegate = object : AlertDialogDelegate {
                    override fun alertCompletion(type: AlertDialogDelegate.AlertTapType, extraButton: MaterialButton?) {
                        if (type == AlertDialogDelegate.AlertTapType.Submit) {
                            viewModel.setState(MainStateEvent.RemoveApp)
                        }
                    }
                }
        )
    }

    private fun showReviewAlert(btnDoNotAskAgain: MaterialButton) {
        CustomAlertDialog.showDialog(this,
                title = getString(R.string.review),
                message = getString(R.string.rate_the_app),
                submitTitle = getString(R.string.rate_now),
                submitTextColorId = ContextCompat.getColor(this, R.color.red),
                cancelTextColorId = ContextCompat.getColor(this, R.color.primary_color),
                imageId = R.drawable.img_rate,
                extraButtons = arrayListOf(btnDoNotAskAgain),
                delegate = object : AlertDialogDelegate {
                    override fun alertCompletion(type: AlertDialogDelegate.AlertTapType, extraButton: MaterialButton?) {
                        if (type == AlertDialogDelegate.AlertTapType.Submit) {
                            openMarketReviewActivity()
                            lifecycleScope.launch(IO) {
                                settingRepository.setDoNotAskAgain(true)
                            }
                        } else if (btnDoNotAskAgain == extraButton) {
                            lifecycleScope.launch(IO) {
                                settingRepository.setDoNotAskAgain(true)
                            }
                        }
                    }
                }
        )
    }

    private fun openTelegram() {
        try {
            viewModel.setState(MainStateEvent.FirebaseAnalytics(intent.component?.className ?: ""))
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("tg://resolve?domain=mobitrain"))
            startActivity(intent)
        } catch (e: Exception) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/mobitrain")))
            viewModel.setState(MainStateEvent.FirebaseAnalytics(intent.component?.className ?: ""))
        }
    }

    private fun openMarketReviewActivity() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("market://details?id=${packageName}")
        startActivity(intent)
    }

    private fun openActivity(cls: Class<*>) {
        val intent = Intent(this, cls)
        startActivity(intent)
        viewModel.setState(MainStateEvent.FirebaseAnalytics(intent.component?.className ?: ""))
    }

    private fun signOutGoogleClient() {
        googleSignInClient.signOut()
    }

    private fun requestGoogleDriveApiConsentScreen() {
        val signInIntent = googleSignInClient.signInIntent
        viewModel.requestGoogleDriveConsent.launch(signInIntent)
    }
}