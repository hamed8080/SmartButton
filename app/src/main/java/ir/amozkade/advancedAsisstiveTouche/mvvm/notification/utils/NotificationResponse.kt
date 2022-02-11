package ir.amozkade.advancedAsisstiveTouche.mvvm.notification.utils

import ir.amozkade.advancedAsisstiveTouche.mvvm.notification.Notification

sealed class NotificationResponse{
    data class AllNotifications(val notifications:ArrayList<Notification>):NotificationResponse()
    object DeleteAll:NotificationResponse()
    object ViewedAll:NotificationResponse()
}
