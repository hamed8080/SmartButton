package ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import dagger.hilt.android.AndroidEntryPoint
import ir.amozkade.advancedAsisstiveTouche.BaseApp
import ir.amozkade.advancedAsisstiveTouche.mvvm.exception.ExceptionRepository
import ir.amozkade.advancedAsisstiveTouche.mvvm.preferenceActivity.SettingRepository
import java.lang.Exception
import javax.inject.Inject

@AndroidEntryPoint
class SmartButtonBootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var exceptionRepository: ExceptionRepository

    @Inject
    lateinit var settingRepository: SettingRepository

    override fun onReceive(context: Context?, p1: Intent?) {
        try {
            if(p1?.action == Intent.ACTION_BOOT_COMPLETED && settingRepository.getCashedModel().startedByUser){
                val intent = Intent(context?.applicationContext, FloatingWindow::class.java)
                intent.putExtra("BOOT_COMPLETED" , "OK")
                FloatingWindow.persistIntent = intent
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context?.startForegroundService(FloatingWindow.persistIntent)
                }else{
                    context?.startService(Intent(context, FloatingWindow::class.java))
                }
            }
        }catch (e:Exception){
            exceptionRepository.saveException(e)
        }
    }
}