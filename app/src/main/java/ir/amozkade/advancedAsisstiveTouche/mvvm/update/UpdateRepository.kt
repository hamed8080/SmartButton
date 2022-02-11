package ir.amozkade.advancedAsisstiveTouche.mvvm.update

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInstaller
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.amozkade.advancedAsisstiveTouche.AppDir
import ir.amozkade.advancedAsisstiveTouche.BuildConfig
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.helper.api.DataState
import ir.amozkade.advancedAsisstiveTouche.helper.downloader.DownloadResponse
import ir.amozkade.advancedAsisstiveTouche.helper.downloader.Downloader
import ir.amozkade.advancedAsisstiveTouche.mvvm.BaseActivityDelegate
import ir.amozkade.advancedAsisstiveTouche.mvvm.mainAssistiveTouch.MainAssistiveTouchActivity
import ir.amozkade.advancedAsisstiveTouche.mvvm.update.di.UpdateRetrofit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import java.io.File
import java.io.FileInputStream
import javax.inject.Inject

class UpdateRepository @Inject constructor(
        @ApplicationContext private val context: Context,
        private val updateRetrofit: UpdateRetrofit
) {

    companion object {
        const val PACKAGE_INSTALLED_ACTION = "PACKAGE_INSTALLED_ACTION"
    }

    @Inject
    lateinit var downloader: Downloader

    @Inject
    @AppDir
    lateinit var appDir: String

    private val installFile: File
        get() = File("$appDir/SmartButton.apk")


    suspend fun checkUpdate(): Flow<UpdateResponse> = flow {
        val update = updateRetrofit.checkUpdate()
        when {
            update.minVer > BuildConfig.VERSION_NAME -> {
                emit(UpdateResponse.ShowMandatoryDialog)
                startDownloadApk(update.url)
            }
            update.versionCode > BuildConfig.VERSION_NAME -> emit(UpdateResponse.ShowUpdateAlert(update))
        }
    }

    suspend fun startDownloadApk(apkUrl: String): Flow<UpdateResponse> = flow {
        downloader.download(apkUrl, "$appDir/SmartButton.apk").collect { dataState ->
            if (dataState is DataState.Success && dataState.data is DownloadResponse.Finished) {
                installApk()
                emit(UpdateResponse.FinishDownloading)
            } else if (dataState is DataState.Success && dataState.data is DownloadResponse.Downloading) {
                emit(UpdateResponse.Downloading(dataState.data.progress))
            }
        }
    }

    private fun installApk() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            installAndroidQ(context)
        } else {
            val apkUri = Uri.fromFile(installFile)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }

    fun requestPackageInstallIfNeeded(cto: AppCompatActivity): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !cto.packageManager.canRequestPackageInstalls()) {
            val unknownSourceIntent = Intent(android.provider.Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
            unknownSourceIntent.data = Uri.parse("package:${cto.packageName}")
            (cto as MainAssistiveTouchActivity).viewModel.installPackageContent.launch(unknownSourceIntent)
            return true
        }
        return false
    }

    fun startInstallApp(intent: Intent?, delegate: BaseActivityDelegate, appDir: String) {
        intent?.extras?.let {
            val context = delegate as MainAssistiveTouchActivity
            if (intent.action == PACKAGE_INSTALLED_ACTION && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                when (it.getInt(PackageInstaller.EXTRA_STATUS)) {
                    PackageInstaller.STATUS_PENDING_USER_ACTION -> {
                        (delegate).startActivity(it.get(Intent.EXTRA_INTENT) as Intent)
                    }
                    PackageInstaller.STATUS_SUCCESS -> Toast.makeText(context.applicationContext, "Install succeeded!", Toast.LENGTH_LONG).show()
                    PackageInstaller.STATUS_FAILURE -> delegate.showWarn(
                            context.getString(R.string.install_fail),
                            String.format(context.getString(R.string.manual_install),
                                    "$appDir/SmartButton.apk"))
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun installAndroidQ(context: Context) {
        var session: PackageInstaller.Session? = null
        try {
            val installer = context.packageManager.packageInstaller
            val sessionParams = PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL)
            val sessionId: Int = installer.createSession(sessionParams)
            session = installer.openSession(sessionId)
            val inputStream = FileInputStream(installFile)
            val outputStream = session.openWrite("package", 0, installFile.length())
            inputStream.copyTo(outputStream)
            session.fsync(outputStream)
            outputStream.close()
            inputStream.close()
            val intent = Intent(context, MainAssistiveTouchActivity::class.java)
            intent.action = PACKAGE_INSTALLED_ACTION
            val pendingIntent = PendingIntent.getActivity(
                    context,
                    0,
                    intent,
                    0
            )
            session.commit(pendingIntent.intentSender)
        } catch (e: Exception) {
            session?.abandon()
            e.printStackTrace()
        }
    }

}