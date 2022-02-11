package ir.amozkade.advancedAsisstiveTouche.mvvm.user.profile.utils


sealed class EditProfileStateEvent {
    object Init : EditProfileStateEvent()
    object ClearProfile:EditProfileStateEvent()
    object ClearSyncFirebaseToken:EditProfileStateEvent()
}