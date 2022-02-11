package ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.buttons

import android.content.Context
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.delegates.FloatingWindowDelegate
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.delegates.AssistiveButtonDelegate
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.delegates.EnableDelegate
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.models.ButtonModelInPanel
import javax.inject.Inject

class RotateButton @Inject constructor(@ApplicationContext private val context: Context): AssistiveButtonDelegate, EnableDelegate {

    override var isEnable: Boolean = false


    override fun action(delegate: FloatingWindowDelegate, buttonModel: ButtonModelInPanel) {
        try {
            val isActiveRotation = Settings.System.getInt(context.contentResolver, Settings.System.ACCELEROMETER_ROTATION)
            isEnable = if (isActiveRotation == 1) {
                Settings.System.putInt(context.contentResolver, Settings.System.ACCELEROMETER_ROTATION, 0)
                true
            } else {
                Settings.System.putInt(context.contentResolver, Settings.System.ACCELEROMETER_ROTATION, 1)
                false
            }
            delegate.onEnableButtonChange(buttonModel, isEnable)
        } catch (e: SettingNotFoundException) {
            e.printStackTrace()
        }
    }

    override fun initIsEnable(context: Context) {
        val isActiveRotation = Settings.System.getInt(context.contentResolver, Settings.System.ACCELEROMETER_ROTATION)
        isEnable = isActiveRotation == 0
    }
}