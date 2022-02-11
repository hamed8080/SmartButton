package ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton

import android.Manifest
import android.accessibilityservice.AccessibilityServiceInfo
import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.helperActivities.RequestAccessibilityInstructionActivity
import ir.amozkade.advancedAsisstiveTouche.mvvm.transparentActivity.TransparentActivity
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class Permissions {
    companion object {
        const val overlayRequestPermissionCode = 100
        private const val writeSettingPermissionRequestCode = 8000
        private val permissions = arrayListOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.CHANGE_NETWORK_STATE,
                Manifest.permission.WRITE_SETTINGS,
                Manifest.permission.SYSTEM_ALERT_WINDOW,
                Manifest.permission.RECEIVE_BOOT_COMPLETED)


        fun requestAllPermissions(cto: AppCompatActivity, permissionRequestCode: Int) {
            ActivityCompat.requestPermissions(
                    cto,
                    permissions.toTypedArray(),
                    permissionRequestCode
            )
            canWriteSetting(cto)//if cant request
        }

        /** Notice this method only work correctly in  Release mode
         *  in debug mode we get always zero enabledServices array
         *  so whe always return true even accessibility is disabled for app **/
        fun isAccessibilityServiceEnabled(cto: Context): Boolean {
            val am = cto.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
            val enabledServices = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
            for (enabledService in enabledServices) {
                val enabledServiceInfo: ServiceInfo = enabledService.resolveInfo.serviceInfo
                if (enabledServiceInfo.packageName == cto.packageName) return true
            }
            return false
        }

        private fun canOverlay(cto: Context): Boolean {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(cto)) return true
            return false
        }

        @SuppressLint("InlinedApi")
        fun openOverlayPermissionIfNotGranted(cto: AppCompatActivity, overlayRequestPermissionCode: Int): Boolean {
            if (!canOverlay(cto)) {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + cto.packageName))
                cto.startActivityForResult(intent, overlayRequestPermissionCode)
                return false
            }
            return true
        }

        private fun canWriteSetting(context: Activity): Boolean {
            val result: Boolean = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Settings.System.canWrite(context)
            } else {
                ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_SETTINGS) == PackageManager.PERMISSION_GRANTED
            }
            if (!result) {
                //request permission
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
                    intent.data = Uri.parse("package:" + context.packageName)
                    context.startActivityForResult(intent, writeSettingPermissionRequestCode)
                } else {
                    ActivityCompat.requestPermissions(context, arrayOf(Manifest.permission.WRITE_SETTINGS), writeSettingPermissionRequestCode)
                }
            }
            return result
        }

        fun isMiUi(): Boolean {
            return getSystemProperty("ro.miui.ui.version.name")?.isNotBlank() == true
        }

        fun isMiuiWithApi29OrMore(): Boolean {
            return isMiUi() && Build.VERSION.SDK_INT >= 29
        }

        fun goToXiaomiPermissions(context: Context) {
            val intent = Intent("miui.intent.action.APP_PERM_EDITOR")
            intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity")
            intent.putExtra("extra_pkgname", context.packageName)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }

        @Suppress("SameParameterValue")
        private fun getSystemProperty(propName: String): String? {
            val line: String
            var input: BufferedReader? = null
            try {
                val p = Runtime.getRuntime().exec("getprop $propName")
                input = BufferedReader(InputStreamReader(p.inputStream), 1024)
                line = input.readLine()
                input.close()
            } catch (ex: IOException) {
                return null
            } finally {
                if (input != null) {
                    try {
                        input.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            return line
        }

        @RequiresApi(Build.VERSION_CODES.N)
        fun requestPermissionNotifAndroidNAndAbove(context: Context, title: String, subtitle: String, action: String) {
            val fullScreenIntent = Intent(context, TransparentActivity::class.java)
            fullScreenIntent.action = action
            val fullScreenPendingIntent = PendingIntent.getActivity(context, 0,
                    fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT)

            val notificationBuilder =
                    NotificationCompat.Builder(context, "SmartButton")
                            .setSmallIcon(R.drawable.notification_small)
                            .setContentTitle(title)
                            .setContentText(subtitle)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setCategory(NotificationCompat.CATEGORY_CALL)
                            .setFullScreenIntent(fullScreenPendingIntent, true)

            val incomingCallNotification = notificationBuilder.build()
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel("SmartButton", "SmartButton", NotificationManager.IMPORTANCE_HIGH)
                channel.enableLights(true)
                channel.lightColor = ContextCompat.getColor(context, R.color.primary_color)
                channel.enableVibration(true)
                notificationManager.createNotificationChannel(channel)
            }
            notificationManager.notify(0, incomingCallNotification)
        }

        fun showAccessibilityRequestPagePermission(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val fullScreenIntent = Intent(context, RequestAccessibilityInstructionActivity::class.java)
                val fullScreenPendingIntent = PendingIntent.getActivity(context, 0,
                        fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

                val notificationBuilder =
                        NotificationCompat.Builder(context, "SmartButton")
                                .setSmallIcon(R.drawable.notification_small)
                                .setContentTitle(context.getString(R.string.request_permission_accessibility_notif_title))
                                .setContentText(context.getString(R.string.request_permission_accessibility_notif_sub_title))
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setCategory(NotificationCompat.CATEGORY_CALL)
                                .setFullScreenIntent(fullScreenPendingIntent, true)

                val incomingCallNotification = notificationBuilder.build()
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val channel = NotificationChannel("SmartButton", "SmartButton", NotificationManager.IMPORTANCE_HIGH)
                    channel.enableLights(true)
                    channel.lightColor = ContextCompat.getColor(context, R.color.primary_color)
                    channel.enableVibration(true)
                    notificationManager.createNotificationChannel(channel)
                }
                notificationManager.notify(0, incomingCallNotification)
                return
            } else {
                val intent = Intent(context, RequestAccessibilityInstructionActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            }
        }
    }
}