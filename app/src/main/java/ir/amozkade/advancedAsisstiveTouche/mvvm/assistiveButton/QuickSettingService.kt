package ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton

import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import dagger.hilt.android.AndroidEntryPoint
import ir.amozkade.advancedAsisstiveTouche.BaseApp
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.mvvm.exception.ExceptionRepository
import ir.amozkade.advancedAsisstiveTouche.mvvm.preferenceActivity.SettingRepository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.N)
@AndroidEntryPoint
class QuickSettingService : TileService() {

    @Inject
    lateinit var exceptionRepository: ExceptionRepository

    @Inject
    lateinit var settingRepository: SettingRepository

    private var isStarted: Boolean = false

    override fun onTileAdded() {
        super.onTileAdded()
        isStarted = FloatingWindow.floatingWindowService != null
        setTileState()
    }

    private fun setTileState() {
        if (isStarted) {
            qsTile.state = Tile.STATE_ACTIVE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                qsTile.subtitle = getString(R.string.on)
            }
        } else {
            qsTile.state = Tile.STATE_INACTIVE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                qsTile.subtitle = getString(R.string.off)
            }
        }
        qsTile.updateTile()
        FloatingWindow.isServiceStarted.value = isStarted
    }

    override fun onStartListening() {
        super.onStartListening()
        isStarted = FloatingWindow.floatingWindowService != null
        setTileState()
    }

    override fun onClick() {
        super.onClick()
        if (isStarted) {
            stopFloatingWindow()
        } else {
            startFloatingWindow()
        }
        setTileState()
    }

    private fun startFloatingWindow() {
        try {
            if (FloatingWindow.floatingWindowService == null) {// floatingWindowService check if service already is running don't start service multiple time
                FloatingWindow.persistIntent?.removeExtra("FROM_NOTIFICATION")
                FloatingWindow.persistIntent?.putExtra("USER_TAP_ON_START", "OK")
                startService(FloatingWindow.persistIntent)
                GlobalScope.launch(IO){
                    settingRepository.setStartedByUser(true)
                }
                isStarted = true
            }
        } catch (e: java.lang.Exception) {
            exceptionRepository.saveException(e)
        }
    }

    private fun stopFloatingWindow() {
        try {
            stopService(FloatingWindow.persistIntent)
            FloatingWindow.floatingWindowService = null
            GlobalScope.launch(IO){
                settingRepository.setStartedByUser(false)
            }
            isStarted = false
        } catch (e: Exception) {
            exceptionRepository.saveException(e)
        }
    }


}