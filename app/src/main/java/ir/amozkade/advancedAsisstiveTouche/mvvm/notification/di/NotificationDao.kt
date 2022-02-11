package ir.amozkade.advancedAsisstiveTouche.mvvm.notification.di

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ir.amozkade.advancedAsisstiveTouche.mvvm.notification.Notification

@Dao
interface NotificationDao {
    @Query("SELECT * FROM Notification")
    suspend fun getAll(): List<Notification>

    @Query("DELETE FROM Notification")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(notification: Notification)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(notifications: List<Notification>)
}