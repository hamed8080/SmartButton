@file:Suppress("DEPRECATION")

package ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.buttons

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Camera
import android.hardware.camera2.CameraManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.Permissions
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.delegates.AssistiveButtonDelegate
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.delegates.EnableDelegate
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.delegates.FloatingWindowDelegate
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.models.ButtonModelInPanel
import ir.amozkade.advancedAsisstiveTouche.mvvm.transparentActivity.TransparentActivity
import javax.inject.Inject


class TorchButton @Inject constructor(@ApplicationContext private val context: Context) : AssistiveButtonDelegate, EnableDelegate {

    override var isEnable: Boolean = false
    private var cameraId: String? = null
    private var camManager: CameraManager? = null

    companion object {
        @Suppress("DEPRECATION")
        var cam: Camera? = null
    }

    @Suppress("DEPRECATION")
    override fun action(delegate: FloatingWindowDelegate, buttonModel: ButtonModelInPanel) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            delegate.showWarn(context.getString(R.string.camera_permission_title), context.getString(R.string.camera_permission_subtitle), R.drawable.img_permission)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Permissions.requestPermissionNotifAndroidNAndAbove(context , context.getString(R.string.camera_permission_title) , context.getString(R.string.camera_permission_subtitle) , TransparentActivity.RequestCameraAction)
            } else {
                val intent = Intent(context, TransparentActivity::class.java)
                intent.action = TransparentActivity.RequestCameraAction
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            }
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            camManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager?
            cameraId = camManager?.cameraIdList?.get(0)
            if (!isEnable) {
                flashLightOn(context)
            } else {
                flashLightOff(context)
            }
            delegate.onEnableButtonChange(buttonModel , isEnable)
            return
        }
        if (context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {


            if (cam == null)
                cam = Camera.open()
            if (cam?.parameters?.flashMode != "torch") {
                flashLightOnPreM()
            } else {
                flashLightOffPreM()
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun flashLightOnPreM() {
        val p: Camera.Parameters = cam?.parameters ?: return
        p.flashMode = Camera.Parameters.FLASH_MODE_TORCH
        cam?.parameters = p
        cam?.startPreview()
    }

    private fun flashLightOffPreM() {
        cam?.stopPreview()
        cam?.release()
        cam = null
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun flashLightOn(cto: Context) {
        if (cto.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            camManager?.setTorchMode(cameraId!!, true)
            isEnable = true
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun flashLightOff(cto: Context) {
        if (cto.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            camManager?.setTorchMode(cameraId!!, false)
            isEnable = false
        }
    }

    override fun initIsEnable(context: Context) {

    }
}