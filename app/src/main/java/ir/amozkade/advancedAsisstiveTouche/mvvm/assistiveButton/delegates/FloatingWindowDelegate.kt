package ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.delegates

import android.view.View
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.models.ButtonModelInPanel


interface FloatingWindowDelegate {
    fun closePanel()
    fun openPanel()
    fun stopService()
    fun speedChange(speed: String)
    fun showButton()
    fun closeButton()
    fun showWarn(title: String, subtitle: String, imageId: Int?, imagePath: String? = null)
    fun showVolumeView(currentVolume: Int, maxVolumeLevel: Int, button: AssistiveButtonDelegate)
    fun onEnableButtonChange(button: ButtonModelInPanel, isEnable: Boolean)
    fun closePanelForSubPanel()
    fun addViewToSubWindow(view: View , button: AssistiveButtonDelegate)
    fun removeViewSubWindow(view: View)
    fun startDismissJob()
    fun cancelDismissJob()
    fun closePanelForScreenshot()
}