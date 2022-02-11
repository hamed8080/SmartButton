package ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.buttons

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.FloatingWindow
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.Permissions
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.SmartButtonAccessibilityService
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.helperActivities.ScreenshotActivity
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.delegates.AssistiveButtonDelegate
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.delegates.FloatingWindowDelegate
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.models.ButtonModelInPanel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class ScreenshotButton  @Inject constructor(@ApplicationContext private val context: Context): AssistiveButtonDelegate {

    override fun action(delegate: FloatingWindowDelegate, buttonModel: ButtonModelInPanel) {
        FloatingWindow.floatingWindowService?.isFromSubWindowItem = true
        GlobalScope.launch(Dispatchers.Main) {
            hideButtonToTakeScreenshot(delegate)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                if (!Permissions.isAccessibilityServiceEnabled(context)){
                    Permissions.showAccessibilityRequestPagePermission(context)
                    return@launch
                }
                SmartButtonAccessibilityService.accessibilityInstance?.performGlobalAction(AccessibilityService.GLOBAL_ACTION_TAKE_SCREENSHOT)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                delay(400) // hide animation
                val imageCaptureIntent = Intent(context, ScreenshotActivity::class.java)
                imageCaptureIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                imageCaptureIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                context.startActivity(imageCaptureIntent)
            }
            delay(2000)
            FloatingWindow.floatingWindowService?.isFromSubWindowItem = false
            delegate.showButton()
            delegate.closePanel()
        }
    }

    private fun hideButtonToTakeScreenshot(delegate: FloatingWindowDelegate) {
        delegate.closeButton()
        delegate.closePanelForSubPanel()
    }

}