package ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.buttons

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.delegates.EnableDelegate
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.delegates.FloatingWindowDelegate
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.delegates.AssistiveButtonDelegate
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.delegates.LongActionDelegate
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.models.ButtonModelInPanel
import ir.amozkade.advancedAsisstiveTouche.mvvm.transparentActivity.TransparentActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class WifiButton @Inject constructor(@ApplicationContext private val context: Context): AssistiveButtonDelegate, EnableDelegate , LongActionDelegate {

    override var isEnable: Boolean = false

    @Suppress("DEPRECATION")
    @SuppressLint("WifiManagerLeak")
    override fun action(delegate: FloatingWindowDelegate, buttonModel: ButtonModelInPanel) {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            val intent = Intent(context, TransparentActivity::class.java)
            intent.action = TransparentActivity.RequestWifiPanel
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
            delegate.closePanel()
        }else{
            GlobalScope.launch(Dispatchers.Main) {
                //delay to show popup grant permission on some devices like xiaomi
                delay(500)
                val wfm = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
                wfm.isWifiEnabled = wfm.isWifiEnabled != true
                isEnable = !isEnable
                delegate.onEnableButtonChange(buttonModel , isEnable)
            }
        }

    }

    @Suppress("DEPRECATION")
    @SuppressLint("WifiManagerLeak", "WifiManagerPotentialLeak")
    override fun initIsEnable(context: Context) {
        val wfm = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        isEnable = wfm.isWifiEnabled
    }

    override fun actionLong(floatingWindow: FloatingWindowDelegate, buttonModel: ButtonModelInPanel) {
        val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

}