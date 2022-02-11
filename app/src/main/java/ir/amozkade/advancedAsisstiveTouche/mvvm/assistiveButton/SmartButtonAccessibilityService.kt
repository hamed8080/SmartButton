package ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.view.accessibility.AccessibilityEvent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SmartButtonAccessibilityService : AccessibilityService() {

    override fun onServiceConnected() {
        super.onServiceConnected()

        // Set the type of events that this service wants to listen to. Others
        // won't be passed to this service.
        serviceInfo.eventTypes = AccessibilityEvent.TYPES_ALL_MASK

        // Set the type of feedback your service will provide.
        serviceInfo.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK

        // Default services are invoked only if no package-specific ones are present
        // for the type of AccessibilityEvent generated. This service *is*
        // application-specific, so the flag isn't necessary. If this was a
        // general-purpose service, it would be worth considering setting the
        // DEFAULT flag.

        serviceInfo.flags = AccessibilityServiceInfo.FLAG_REQUEST_TOUCH_EXPLORATION_MODE
        serviceInfo.notificationTimeout = 100
        accessibilityInstance = this
    }

    override fun onInterrupt() {
    }

    override fun onAccessibilityEvent(p0: AccessibilityEvent?) {
        if (p0?.eventType == AccessibilityEvent.TYPE_VIEW_CONTEXT_CLICKED || p0?.eventType == AccessibilityEvent.TYPE_VIEW_CLICKED) {
            if(FloatingWindow.floatingWindowService?.isFromSubWindowItem == false && FloatingWindow.floatingWindowService?.isSubWindowOpen == false){
                FloatingWindow.floatingWindowService?.closePanel()
                FloatingWindow.floatingWindowService?.showButton()
                FloatingWindow.floatingWindowService?.subWindowView?.let{
                    FloatingWindow.floatingWindowService?.removeViewSubWindow(it)
                }
            }else{
                FloatingWindow.floatingWindowService?.isSubWindowOpen = false
                FloatingWindow.floatingWindowService?.isFromSubWindowItem = false
            }
        }
    }


    companion object{
        var accessibilityInstance: SmartButtonAccessibilityService? = null
    }
}