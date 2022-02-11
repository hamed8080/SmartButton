package ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.buttons.screenRecorder

import android.content.Context
import android.content.Intent
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.FloatingWindow
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.helperActivities.ScreenRecordingActivity
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.delegates.AssistiveButtonDelegate
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.delegates.FloatingWindowDelegate
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.models.ButtonModelInPanel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class ScreenRecordingButton @Inject constructor(@ApplicationContext private val context: Context): AssistiveButtonDelegate {

    override fun action(delegate: FloatingWindowDelegate, buttonModel: ButtonModelInPanel) {
        FloatingWindow.floatingWindowService?.isFromSubWindowItem = true
        GlobalScope.launch(Dispatchers.Main) {
            delegate.closeButton()
            delegate.closePanelForSubPanel()
            delay(400)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val imageCaptureIntent = Intent(context, ScreenRecordingActivity::class.java)
                imageCaptureIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                imageCaptureIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                context.startActivity(imageCaptureIntent)
            }
            FloatingWindow.floatingWindowService?.isFromSubWindowItem = false
        }
    }
}