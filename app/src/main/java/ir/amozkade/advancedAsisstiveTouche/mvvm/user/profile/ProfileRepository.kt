package ir.amozkade.advancedAsisstiveTouche.mvvm.user.profile

import ir.amozkade.advancedAsisstiveTouche.mvvm.user.profile.di.ProfileDao
import javax.inject.Inject

class ProfileRepository @Inject constructor(private val profileDao: ProfileDao) {

    suspend fun getProfile(): Profile? {
        return profileDao.getProfile()
    }

    suspend fun clearProfile() {
        getProfile()?.let {
            profileDao.clear(it)
        }
    }

    suspend fun saveProfile(profile: Profile) {
        profileDao.saveProfile(profile)
    }
}