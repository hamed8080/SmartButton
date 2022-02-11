package ir.amozkade.advancedAsisstiveTouche.helper.bindings

import android.widget.SeekBar

class SeekBarBinding {

    open class CustomSeekBarChangeListener:SeekBar.OnSeekBarChangeListener{
        override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {

        }

        override fun onStartTrackingTouch(p0: SeekBar?) {
        }

        override fun onStopTrackingTouch(p0: SeekBar?) {
        }

    }
}