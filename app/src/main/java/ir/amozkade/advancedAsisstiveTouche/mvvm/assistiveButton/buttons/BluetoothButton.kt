package ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.buttons

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.provider.Settings
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.delegates.AssistiveButtonDelegate
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.delegates.EnableDelegate
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.delegates.FloatingWindowDelegate
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.delegates.LongActionDelegate
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.models.ButtonModelInPanel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class BluetoothButton @Inject constructor(@ApplicationContext private val context: Context) : AssistiveButtonDelegate, EnableDelegate, LongActionDelegate {

    override var isEnable: Boolean = false

    private val bluetoothAdapter:BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    override fun action(delegate: FloatingWindowDelegate, buttonModel: ButtonModelInPanel) {
        if (bluetoothAdapter?.isEnabled == true) {
            bluetoothAdapter.disable()
        } else {
            bluetoothAdapter?.enable()
        }
        GlobalScope.launch(Dispatchers.Main){
            isEnable = !isEnable
            delay(100)
            delegate.onEnableButtonChange(buttonModel, isEnable)
        }
    }

    override fun actionLong(floatingWindow: FloatingWindowDelegate, buttonModel: ButtonModelInPanel) {
        val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    override fun initIsEnable(context: Context) {
        isEnable = bluetoothAdapter?.isEnabled ?: false
    }

}