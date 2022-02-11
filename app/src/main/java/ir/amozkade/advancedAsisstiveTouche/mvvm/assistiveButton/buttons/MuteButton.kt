package ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.buttons

import android.app.NotificationManager
import android.content.Context
import android.media.AudioManager
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.Permissions
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.delegates.AssistiveButtonDelegate
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.delegates.EnableDelegate
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.delegates.FloatingWindowDelegate
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.models.ButtonModelInPanel
import ir.amozkade.advancedAsisstiveTouche.mvvm.transparentActivity.TransparentActivity
import javax.inject.Inject

class MuteButton  @Inject constructor(@ApplicationContext private val context: Context) : AssistiveButtonDelegate, EnableDelegate {

    override var isEnable: Boolean = false

    override fun action(delegate: FloatingWindowDelegate, buttonModel: ButtonModelInPanel) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && !notificationManager.isNotificationPolicyAccessGranted) {
            Permissions.requestPermissionNotifAndroidNAndAbove(context,
                    context.getString(R.string.tap_to_request_enable_disturb_mode_title) ,
                    context.getString(R.string.tap_to_request_enable_disturb_mode_subtitle)  ,
                    TransparentActivity.RequestDisturbAction)
            return
        }
        val audio = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audio.ringerMode = if(audio.ringerMode == AudioManager.RINGER_MODE_NORMAL) AudioManager.RINGER_MODE_SILENT else AudioManager.RINGER_MODE_NORMAL
        delegate.onEnableButtonChange(buttonModel , audio.ringerMode == AudioManager.RINGER_MODE_SILENT)
    }

    override fun initIsEnable(context: Context) {
        val audio = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        isEnable = audio.ringerMode == AudioManager.RINGER_MODE_SILENT
    }

}