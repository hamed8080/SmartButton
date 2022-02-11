package ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.buttons

import android.os.Build
import androidx.annotation.RequiresApi
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.delegates.AssistiveButtonDelegate
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.delegates.FloatingWindowDelegate
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.models.ButtonModelInPanel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class RebootButton @Inject constructor() : AssistiveButtonDelegate {


    @Suppress("BlockingMethodInNonBlockingContext")
    @RequiresApi(Build.VERSION_CODES.N)
    override fun action(delegate: FloatingWindowDelegate, buttonModel: ButtonModelInPanel) {
        delegate.closePanel()
        GlobalScope.launch(Dispatchers.Main) {
            delay(1000)
            try {
                val process = Runtime.getRuntime().exec(arrayOf("su", "-c", "reboot"))
                process.waitFor()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}