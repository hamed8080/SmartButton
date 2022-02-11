package ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.buttons

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.helperActivities.AppDrawerActivity
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.delegates.AssistiveButtonDelegate
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.delegates.FloatingWindowDelegate
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.models.ButtonModelInPanel
import javax.inject.Inject

class AppDrawerButton @Inject constructor(@ApplicationContext private val context: Context)  : AssistiveButtonDelegate {

    override fun action(delegate: FloatingWindowDelegate, buttonModel: ButtonModelInPanel) {
        val intent = Intent(context, AppDrawerActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    class AppInfo(val packageName: String, val appName: String, val drawable: Drawable?)
}