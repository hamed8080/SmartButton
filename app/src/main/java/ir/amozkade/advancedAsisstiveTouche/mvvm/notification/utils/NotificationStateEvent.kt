package ir.amozkade.advancedAsisstiveTouche.mvvm.notification.utils


sealed class NotificationStateEvent {
    object GetAll:NotificationStateEvent()
    object ViewedAll:NotificationStateEvent()
}