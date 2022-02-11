package ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.buttons.screenRecorder

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.FloatingWindow
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.helperActivities.ScreenRecordingActivity
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class ScreenRecordingService : LifecycleService() {

    private var builder: NotificationCompat.Builder? = null
    private var remoteViews: RemoteViews? = null
    private var isStarted = false
    private var seconds = 0
    private var notificationManager:NotificationManager? = null

    companion object {
        const val STOP_RECORDING = 200
        const val SCREEN_RECORDING_NOTIF_ID = 300
    }

    private val task  =  object:TimerTask(){
        override fun run() {
            GlobalScope.launch(Main){
                seconds += 1
                val minute = if ((seconds / 60) < 10) "0${seconds / 60}" else "${seconds / 60}"
                val second = if ((seconds % 60) < 10) "0${seconds % 60}" else "${seconds % 60}"
                remoteViews?.setTextViewText(R.id.txtTime , "$minute:$second")
                notificationManager?.notify(SCREEN_RECORDING_NOTIF_ID , builder?.build())
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!isStarted) {
            startForegroundService()
        } else {
            task.cancel()
            FloatingWindow.floatingWindowService?.showButton()
            val path = ScreenRecordingActivity.recordingFileName?.replace("//" , "/")
            FloatingWindow.floatingWindowService?.showWarn(getString(R.string.screen_recording), String.format(getString(R.string.screen_recording_path) , path),R.drawable.img_recording)
            ScreenRecordingActivity.stopRecording()
            isStarted = false
            stopForeground(true)
            stopSelf()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startForegroundService() {
        notificationManager =  getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        stopForeground(true)
        isStarted = true
        val recordingIntent = Intent(this, ScreenRecordingService::class.java)
        val recordingPendingIntent = PendingIntent.getService(this, STOP_RECORDING, recordingIntent, PendingIntent.FLAG_CANCEL_CURRENT)
        val intent = Intent(this, FloatingWindow::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = NotificationCompat.Builder(this, createNotificationChannel())
        } else {
            @Suppress("DEPRECATION")
            builder = NotificationCompat.Builder(this)
        }
        remoteViews = RemoteViews(packageName, R.layout.screen_recording_notification)
        remoteViews?.setOnClickPendingIntent(R.id.stopRecording, recordingPendingIntent)
        remoteViews?.setTextViewText(R.id.txtTime , "00:00")
        builder?.setAutoCancel(false)
                ?.setOngoing(true)
                ?.setColor(ContextCompat.getColor(this, R.color.red))
                ?.setSmallIcon(R.drawable.notification_small)
                ?.setContentTitle(getString(R.string.screen_recording))
                ?.setCustomBigContentView(remoteViews)
                ?.setContent(remoteViews)
        startForeground(SCREEN_RECORDING_NOTIF_ID, builder?.build())
        FloatingWindow.persistIntent = intent
        Timer().schedule(task, 0 , 1000)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(): String {
        val channelId = "my_service"
        val chan = NotificationChannel(channelId, "ScreenRecording", NotificationManager.IMPORTANCE_NONE)
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }


}