package ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.buttons

import android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_NOTIFICATIONS
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.Permissions
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.SmartButtonAccessibilityService
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.delegates.FloatingWindowDelegate
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.delegates.AssistiveButtonDelegate
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.models.ButtonModelInPanel
import javax.inject.Inject

class NotificationButton @Inject constructor(@ApplicationContext private val context: Context) : AssistiveButtonDelegate {

    override fun action(delegate: FloatingWindowDelegate, buttonModel: ButtonModelInPanel) {
        if (!Permissions.isAccessibilityServiceEnabled(context)) {
            Permissions.showAccessibilityRequestPagePermission(context)
            return
        }
        SmartButtonAccessibilityService.accessibilityInstance?.performGlobalAction(GLOBAL_ACTION_NOTIFICATIONS)
    }
}