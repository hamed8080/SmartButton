package ir.amozkade.advancedAsisstiveTouche

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import ir.amozkade.advancedAsisstiveTouche.mvvm.notification.Notification
import ir.amozkade.advancedAsisstiveTouche.mvvm.notification.NotificationTicketReceiver
import ir.amozkade.advancedAsisstiveTouche.mvvm.notification.di.NotificationDao
import ir.amozkade.advancedAsisstiveTouche.mvvm.preferenceActivity.SettingRepository
import ir.amozkade.advancedAsisstiveTouche.mvvm.splash.SplashActivity
import ir.amozkade.advancedAsisstiveTouche.mvvm.tickets.TicketsActivity
import ir.amozkade.advancedAsisstiveTouche.mvvm.tickets.conversation.model.TicketMessage
import ir.amozkade.advancedAsisstiveTouche.mvvm.tickets.di.TicketDao
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class FCMService : FirebaseMessagingService() {

    @Inject
    lateinit var notificationDao: NotificationDao

    @Inject
    lateinit var ticketDao: TicketDao

    @Inject
    lateinit var settingRepository: SettingRepository

    override fun onCreate() {
        super.onCreate()
        FirebaseMessaging.getInstance().subscribeToTopic("ALL")
                .addOnCompleteListener { task ->
                    Log.d(TAG, if (task.isSuccessful) "success to subscribe ALL" else "failed to subscribe ALL")
                }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val isGeneral = remoteMessage.data["General"] == "1"
        val isTicket = remoteMessage.data["IsTicket"] == "1"
        if (isGeneral) {
            insertGeneralToDatabase(remoteMessage)
        }
        if (isTicket) {
            insertTicketToDatabase(remoteMessage)
            return
        }
        val intent = Intent(this, SplashActivity::class.java)
        sendLocalNotification(remoteMessage.notification?.title, remoteMessage.notification?.body, intent)
    }

    private fun insertGeneralToDatabase(remoteMessage: RemoteMessage) {
        GlobalScope.launch {
            notificationDao.insert(Notification(0, remoteMessage.notification?.body
                    ?: "", remoteMessage.notification?.title ?: "", Date(), null, true))
        }
    }

    private fun insertTicketToDatabase(remoteMessage: RemoteMessage) {

        val ticketMessage = ObjectMapper().readValue<TicketMessage>(remoteMessage.data["ticketMessage"] as String)
        GlobalScope.launch(IO) {
            ticketDao.insertTicketMessage(ticketMessage)
        }
        val intent = Intent()
        intent.action = NotificationTicketReceiver.NOTIFICATION_TICKET_ACTION
        intent.putExtra("TicketMessageNotification" , ticketMessage)
        sendBroadcast(intent)
        sendLocalNotification(getString(R.string.ticket), ticketMessage.message, intent = Intent(this@FCMService, TicketsActivity::class.java))
    }

    private fun sendLocalNotification(title: String?, body: String?, intent: Intent) {
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationCompat = NotificationCompat.Builder(this, "SmartButton")
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setSmallIcon(R.drawable.notification_small)
                .setColor(ContextCompat.getColor(this, R.color.primary_color))
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("SmartButton", "SmartButton", NotificationManager.IMPORTANCE_HIGH)
            channel.enableLights(true)
            channel.lightColor = ContextCompat.getColor(this, R.color.primary_color)
            channel.enableVibration(true)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0, notificationCompat.build())
    }

    override fun onNewToken(firebaseToken: String) {
        super.onNewToken(firebaseToken)
        GlobalScope.launch(IO){
            settingRepository.saveNewFirebaseToken(firebaseToken)
        }
    }
}