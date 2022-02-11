package ir.amozkade.advancedAsisstiveTouche.mvvm.user.profile.di

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import ir.amozkade.advancedAsisstiveTouche.mvvm.user.profile.Profile


@Dao
interface ProfileDao {

    @Query("SELECT * FROM PROFILE LIMIT 1")
    suspend fun getProfile(): Profile?

    @Delete
    suspend fun clear(profile: Profile)

    @Insert
    suspend fun saveProfile(profile: Profile)

}