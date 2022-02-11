package ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton

import android.net.TrafficStats
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.delegates.FloatingWindowDelegate
import java.util.*

class CurrentSpeedMeter private constructor() {

    companion object {
        private var instance: CurrentSpeedMeter? = null

        fun sharedInstance(): CurrentSpeedMeter {
            return if (instance == null) {
                instance = CurrentSpeedMeter()
                instance!!
            } else {
                instance!!
            }
        }
    }

    var first = true
    var beforeSpeed: Double = 0.0

    fun startShowingCurrentSpeedIfEnabled(delegate: FloatingWindowDelegate, showSpeedEnabledInPreference:Boolean) {
        if (showSpeedEnabledInPreference) {
            Timer().scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    calculateSpeed(delegate)
                }
            }, 0, 999)
        }
    }

    private fun calculateSpeed(delegate: FloatingWindowDelegate) {
        val overallTraffic = TrafficStats.getTotalRxBytes().toDouble()
        val currentDataRate: Double = overallTraffic - beforeSpeed
        beforeSpeed = overallTraffic
        // first is boolean for prevent overflow speed in txt
        if (!first && TrafficStats.getTotalRxBytes().toInt() != TrafficStats.UNSUPPORTED ) {
            delegate.speedChange(((currentDataRate / (1024 * 1024 / 1024)).toInt()).toString() + "kb")
        }
        first = false
    }
}