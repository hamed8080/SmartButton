package ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.buttons

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioManager
import android.view.LayoutInflater
import android.widget.SeekBar
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.databinding.VolumePanelBinding
import ir.amozkade.advancedAsisstiveTouche.helper.bindings.SeekBarBinding
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.FloatingWindow
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.delegates.FloatingWindowDelegate
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.delegates.AssistiveButtonDelegate
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.delegates.AutoDismissActionDelegate
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.models.ButtonModelInPanel
import javax.inject.Inject
import kotlin.math.abs


class VolumeDownButton @Inject constructor(@ApplicationContext private val context: Context) : AssistiveButtonDelegate  , AutoDismissActionDelegate{

    override fun action(delegate: FloatingWindowDelegate, buttonModel: ButtonModelInPanel) {
        val audioManager = context.applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND)
        val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        val maxVolumeLevel = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        delegate.showVolumeView(currentVolume, maxVolumeLevel , this)
    }

    companion object {
        private var volumePanelBinding: VolumePanelBinding? = null


        @SuppressLint("ClickableViewAccessibility")
        fun showVolumeView(currentVolume: Int, maxVolumeLevel: Int, floatingWindowDelegate: FloatingWindowDelegate, buttonDelegate: AssistiveButtonDelegate) {
            if (volumePanelBinding == null) {
                volumePanelBinding = DataBindingUtil.inflate(LayoutInflater.from((floatingWindowDelegate as FloatingWindow)), R.layout.volume_panel, null, false)
            }
            val audioManager = (floatingWindowDelegate as FloatingWindow).applicationContext?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            volumePanelBinding?.progress = currentVolume
            volumePanelBinding?.seekBar?.max = maxVolumeLevel
            volumePanelBinding?.seekBar?.setOnSeekBarChangeListener(object : SeekBarBinding.CustomSeekBarChangeListener() {

                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    floatingWindowDelegate.cancelDismissJob()
                    if (p0?.progress == currentVolume) {
                        return
                    }
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                    val difference = abs((p0?.progress ?: 0) - currentVolume)
                    val isUp = (p0?.progress ?: 0) > currentVolume
                    for (i in 0..difference) {
                        audioManager.adjustVolume(if (isUp) AudioManager.ADJUST_RAISE else AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND)
                    }
                    floatingWindowDelegate.startDismissJob()
                }

            })
            val volumePanelBinding = volumePanelBinding ?: return
            if (!ViewCompat.isAttachedToWindow(volumePanelBinding.root)) {
                floatingWindowDelegate.addViewToSubWindow(volumePanelBinding.root, buttonDelegate)
            }
            volumePanelBinding.root.setOnClickListener {
                floatingWindowDelegate.removeViewSubWindow(volumePanelBinding.root)
            }
            floatingWindowDelegate.startDismissJob()
        }


    }
}