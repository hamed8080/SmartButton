package ir.amozkade.advancedAsisstiveTouche.mvvm.notification

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.amozkade.advancedAsisstiveTouche.helper.api.DataState
import ir.amozkade.advancedAsisstiveTouche.mvvm.notification.di.NotificationDao
import ir.amozkade.advancedAsisstiveTouche.mvvm.notification.di.NotificationRetrofit
import ir.amozkade.advancedAsisstiveTouche.mvvm.notification.utils.NotificationResponse
import ir.amozkade.advancedAsisstiveTouche.mvvm.user.login.models.Payload
import ir.mobitrain.applicationcore.api.JWT
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class NotificationRepository @Inject constructor(
        private val notificationDao: NotificationDao,
        private val notificationRetrofit: NotificationRetrofit,
        @ApplicationContext private val context: Context) {

    suspend fun getAllNotifications(): Flow<DataState<NotificationResponse>> = flow {
        emit(DataState.Loading)
        JWT.instance.getPayload<Payload>()?.userId?.let { userId ->
            val notifications = notificationRetrofit.getAllNotifications(userId)
            val dbNotifications = notificationDao.getAll()
            val sum = ArrayList(notifications)
            sum.addAll(dbNotifications)
            emit(DataState.Success(NotificationResponse.AllNotifications(sum)))
        }
    }

    suspend fun viewedAll(): Flow<DataState<NotificationResponse>> = flow {
        emit(DataState.Loading)
        notificationDao.deleteAll()
        JWT.instance.getPayload<Payload>()?.userId?.let { userId ->
            notificationRetrofit.setSeenAll(userId)
            emit(DataState.Success(NotificationResponse.ViewedAll))
        }
    }

}