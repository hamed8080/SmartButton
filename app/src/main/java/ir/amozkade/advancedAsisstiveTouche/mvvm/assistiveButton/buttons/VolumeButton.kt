package ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.buttons

import android.content.Context
import android.media.AudioManager
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.delegates.FloatingWindowDelegate
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.delegates.AssistiveButtonDelegate
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.models.ButtonModelInPanel
import javax.inject.Inject

class VolumeButton  @Inject constructor(@ApplicationContext private val context: Context) : AssistiveButtonDelegate {

    override fun action(delegate: FloatingWindowDelegate, buttonModel: ButtonModelInPanel) {
        val audio = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audio.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_SAME, AudioManager.FLAG_SHOW_UI)
    }
}