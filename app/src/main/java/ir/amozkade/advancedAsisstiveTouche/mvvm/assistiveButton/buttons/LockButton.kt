package ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.buttons

import android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_POWER_DIALOG
import android.app.admin.DeviceAdminReceiver
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.delegates.FloatingWindowDelegate
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.Permissions
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.SmartButtonAccessibilityService
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.delegates.AssistiveButtonDelegate
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.delegates.LongActionDelegate
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.models.ButtonModelInPanel
import ir.amozkade.advancedAsisstiveTouche.mvvm.transparentActivity.TransparentActivity
import javax.inject.Inject

class LockButton @Inject constructor(@ApplicationContext private val context: Context) : AssistiveButtonDelegate , LongActionDelegate{


    override fun action(delegate: FloatingWindowDelegate, buttonModel: ButtonModelInPanel) {
        val componentName = ComponentName(context, DeviceAdminReceiver::class.java)
        val dmp = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        if (dmp.isAdminActive(componentName)) {
            dmp.lockNow()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Permissions.requestPermissionNotifAndroidNAndAbove(context , context.getString(R.string.tap_to_request_admin_title) , context.getString(R.string.permission_admin_subtitle)  , TransparentActivity.RequestDeviceAdminACTION)
            } else {
                val intent = Intent(context, TransparentActivity::class.java)
                intent.action = TransparentActivity.RequestDeviceAdminACTION
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            }
        }
    }

    override fun actionLong(floatingWindow: FloatingWindowDelegate, buttonModel: ButtonModelInPanel) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (!Permissions.isAccessibilityServiceEnabled(context)){
                Permissions.showAccessibilityRequestPagePermission(context)
                return
            }
            SmartButtonAccessibilityService.accessibilityInstance?.performGlobalAction(GLOBAL_ACTION_POWER_DIALOG)
        }else{
            floatingWindow.showWarn(context.getString(R.string.not_compatible) ,context.getString(R.string.power_menu_compatible_with_android_five) , R.drawable.img_permission)
        }
    }
}