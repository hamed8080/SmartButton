package ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.buttons

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.Permissions
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.SmartButtonAccessibilityService
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.delegates.FloatingWindowDelegate
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.delegates.AssistiveButtonDelegate
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.models.ButtonModelInPanel
import javax.inject.Inject

class PowerButton @Inject constructor(@ApplicationContext private val context: Context): AssistiveButtonDelegate {

    override fun action(delegate: FloatingWindowDelegate, buttonModel: ButtonModelInPanel) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (!Permissions.isAccessibilityServiceEnabled(context)){
                Permissions.showAccessibilityRequestPagePermission(context)
                return
            }
            SmartButtonAccessibilityService.accessibilityInstance?.performGlobalAction(AccessibilityService.GLOBAL_ACTION_POWER_DIALOG)
        }else{
            delegate.showWarn(context.getString(R.string.not_compatible) ,context.getString(R.string.power_menu_compatible_with_android_five) , R.drawable.img_not_compatible)
        }
    }
}