package ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.buttons

import android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_BACK
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.Permissions
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.SmartButtonAccessibilityService
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.delegates.FloatingWindowDelegate
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.delegates.AssistiveButtonDelegate
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.models.ButtonModelInPanel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class BackButton @Inject constructor(@ApplicationContext private val context: Context) : AssistiveButtonDelegate {

    override  fun action(delegate: FloatingWindowDelegate, buttonModel: ButtonModelInPanel) {
        if (!Permissions.isAccessibilityServiceEnabled(context)){
            Permissions.showAccessibilityRequestPagePermission(context)
            return
        }
        GlobalScope.launch (Dispatchers.Main){
            delay(200)
            SmartButtonAccessibilityService.accessibilityInstance?.performGlobalAction(GLOBAL_ACTION_BACK)
        }
    }

}