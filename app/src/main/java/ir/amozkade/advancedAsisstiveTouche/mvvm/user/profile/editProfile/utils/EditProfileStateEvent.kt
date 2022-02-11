package ir.amozkade.advancedAsisstiveTouche.mvvm.user.profile.editProfile.utils

import android.graphics.Bitmap

sealed class EditProfileStateEvent{
    object EditProfile:EditProfileStateEvent()
    data class UploadImage(val bitmap:Bitmap):EditProfileStateEvent()
}
