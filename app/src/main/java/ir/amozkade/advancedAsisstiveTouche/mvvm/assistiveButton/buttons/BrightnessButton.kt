package ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.buttons

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.view.LayoutInflater
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.databinding.BrightnessLayoutBinding
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.delegates.FloatingWindowDelegate
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.Permissions
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.delegates.AssistiveButtonDelegate
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.delegates.AutoDismissActionDelegate
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.models.ButtonModelInPanel
import ir.mobitrain.applicationcore.helper.Converters
import java.lang.Exception
import javax.inject.Inject


class BrightnessButton @Inject constructor(@ApplicationContext private val context: Context) : AssistiveButtonDelegate  , AutoDismissActionDelegate {

    override fun action(delegate: FloatingWindowDelegate, buttonModel: ButtonModelInPanel) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val mBinding = DataBindingUtil.inflate<BrightnessLayoutBinding>(inflater, R.layout.brightness_layout, null, false)
        val progress = mBinding.progress
        var currPercent = 0
        try {
            currPercent = Settings.System.getInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS)
        } catch (e: SettingNotFoundException) {
            e.printStackTrace()
        }
        progress.progress = currPercent
        progress.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                delegate.cancelDismissJob()
                setBrightness(progress, context)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                delegate.startDismissJob()
            }
        })
        val displayWidth = context.resources.displayMetrics.widthPixels
        val width = displayWidth - Converters.convertIntToSP(128, context).toInt()
        mBinding.subWindowView.layoutParams.width = width
        delegate.addViewToSubWindow(mBinding.root , this)
        mBinding.root.setOnClickListener {
            delegate.removeViewSubWindow(mBinding.root)
        }
    }

    fun setBrightness(newProgressValue: Int, cto: Context) {
        var progress = newProgressValue
        if (newProgressValue < 0) progress = 0 else if (newProgressValue > 255) progress = 255
        if (ContextCompat.checkSelfPermission(cto, Manifest.permission.WRITE_SETTINGS) == PackageManager.PERMISSION_GRANTED)
            Settings.System.putInt(cto.applicationContext.contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL)
        if (Permissions.isMiUi()) {
            progress = if (progress == 255) 8000 else progress
            try {
                Settings.System.putInt(cto.applicationContext.contentResolver, Settings.System.SCREEN_BRIGHTNESS, progress)
            } catch (e: Exception) {
                Settings.System.putInt(cto.applicationContext.contentResolver, Settings.System.SCREEN_BRIGHTNESS, 255)
            }
        } else {
            Settings.System.putInt(cto.applicationContext.contentResolver, Settings.System.SCREEN_BRIGHTNESS, progress)
        }
    }
}