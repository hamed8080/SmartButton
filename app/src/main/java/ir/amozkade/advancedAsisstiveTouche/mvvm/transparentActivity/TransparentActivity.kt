package ir.amozkade.advancedAsisstiveTouche.mvvm.transparentActivity

import android.Manifest
import android.app.admin.DeviceAdminReceiver
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.AndroidEntryPoint
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.FloatingWindow
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.buttons.translate.TranslateButton
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.models.ButtonModelInPanel
import ir.amozkade.advancedAsisstiveTouche.mvvm.preferenceActivity.SettingRepository
import javax.inject.Inject

@AndroidEntryPoint
class TransparentActivity : AppCompatActivity() {

    @Inject
    lateinit var translateButton: TranslateButton

    @Inject
    lateinit var settingRepository: SettingRepository

    companion object {
        const val RequestCallPhoneAction = "REQUEST_CALL_PHONE_ACTION"
        const val RequestDeviceAdminACTION = "REQUEST_DEVICE_ADMIN_ACTION"
        const val RequestCameraAction = "REQUEST_CAMERA_ACTION"
        const val RequestDisturbAction = "REQUEST_DISTURB_ACTION"
        const val RequestWifiPanel = "REQUEST_WIFI_PANEL"
    }


    private val wifiPermissionContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}
    private val disturbContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}
    private val deviceAdminContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {activityResult->
        if (activityResult.resultCode != RESULT_OK) {
            FloatingWindow.floatingWindowService?.showWarn(getString(R.string.permission_denied_title), getString(R.string.permission_admin_subtitle), R.drawable.img_permission)
        }
    }

    private val cameraRequestCode = 100
    private val callPhoneRequestCode = 104

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.transparent_activity)
        if (intent.action == RequestDeviceAdminACTION) {
            requestDeviceAdmin()
        } else if (intent.action == RequestCameraAction) {
            requestCameraPermission()
        } else if (intent.action == RequestDisturbAction && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestDisturbPermission()
        } else if (intent.action == RequestCallPhoneAction) {
            requestCallPhonePermission()
        } else if (intent.action == RequestWifiPanel) {
            val intent = Intent(Settings.Panel.ACTION_WIFI)
            wifiPermissionContent.launch(intent)
        } else if (intent.action == Intent.ACTION_SEND && intent.getStringExtra(Intent.EXTRA_TEXT) != null) {
            val textToTranslate = intent.getStringExtra(Intent.EXTRA_TEXT)
            val buttonModelInPanel = ButtonModelInPanel()
            FloatingWindow.floatingWindowService?.onClickItemInPanel(translateButton, buttonModelInPanel)
            translateButton.translateFromOtherApp(textToTranslate ?: "")
            finish()
        }
        NotificationManagerCompat.from(this).cancel(null, 0)
    }

    private fun requestDeviceAdmin() {
        val componentName = ComponentName(this, DeviceAdminReceiver::class.java)
        val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName)
        deviceAdminContent.launch(intent)
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                cameraRequestCode
        )
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestDisturbPermission() {
        val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
        disturbContent.launch(intent)
    }

    private fun requestCallPhonePermission() {
        ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CALL_PHONE),
                callPhoneRequestCode
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == callPhoneRequestCode && grantResults.firstOrNull() != PackageManager.PERMISSION_GRANTED) {
            FloatingWindow.floatingWindowService?.showWarn(getString(R.string.call_permission_title), getString(R.string.call_permission_subtitle), R.drawable.img_permission)
        }
        if (requestCode == cameraRequestCode && grantResults.firstOrNull() != PackageManager.PERMISSION_GRANTED) {
            FloatingWindow.floatingWindowService?.showWarn(getString(R.string.camera_permission_title), getString(R.string.camera_permission_subtitle), R.drawable.img_permission)
        }
        finish()
    }
}