package ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.buttons

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.Permissions
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.delegates.FloatingWindowDelegate
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.delegates.AssistiveButtonDelegate
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.delegates.LongActionDelegate
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.models.ButtonModelInPanel
import ir.amozkade.advancedAsisstiveTouche.mvvm.transparentActivity.TransparentActivity
import javax.inject.Inject

class ContactButton @Inject constructor(@ApplicationContext private val context: Context): AssistiveButtonDelegate  ,LongActionDelegate{

    override fun action(delegate: FloatingWindowDelegate, buttonModel: ButtonModelInPanel) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
//            val intent = Intent(Intent.ACTION_CALL) // action call rejected by cafe bazaar
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:" + buttonModel.phoneNumber)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_FROM_BACKGROUND)
        context.startActivity(intent)
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Permissions.requestPermissionNotifAndroidNAndAbove(context,
                        context.getString(R.string.call_permission_title),
                        context.getString(R.string.call_permission_subtitle),
                        TransparentActivity.RequestCallPhoneAction)
            } else {
                val intent = Intent(context, TransparentActivity::class.java)
                intent.action = TransparentActivity.RequestCallPhoneAction
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            }
        }
    }

    override fun actionLong(floatingWindow: FloatingWindowDelegate, buttonModel: ButtonModelInPanel) {

    }

    class Contact(val name: String, val phoneNumber: String, val imageUri: String? = null, val id: String, val icon: Drawable? = null)

}