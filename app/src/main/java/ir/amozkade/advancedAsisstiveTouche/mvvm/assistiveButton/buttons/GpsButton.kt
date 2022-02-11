package ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.buttons

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.provider.Settings
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.delegates.EnableDelegate
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.delegates.FloatingWindowDelegate
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.delegates.AssistiveButtonDelegate
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.delegates.LongActionDelegate
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.models.ButtonModelInPanel
import javax.inject.Inject

class GpsButton @Inject constructor(@ApplicationContext private val context: Context): AssistiveButtonDelegate, EnableDelegate, LongActionDelegate {

    override var isEnable: Boolean = false

    override fun action(delegate: FloatingWindowDelegate, buttonModel: ButtonModelInPanel) {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    override fun actionLong(floatingWindow: FloatingWindowDelegate, buttonModel: ButtonModelInPanel) {

    }

    override fun initIsEnable(context: Context) {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        isEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

}