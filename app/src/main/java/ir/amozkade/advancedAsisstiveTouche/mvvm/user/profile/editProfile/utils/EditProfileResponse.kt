package ir.amozkade.advancedAsisstiveTouche.mvvm.user.profile.editProfile.utils

sealed class EditProfileResponse{
    object SuccessEdited:EditProfileResponse()
    object SuccessUploadedImage:EditProfileResponse()
}