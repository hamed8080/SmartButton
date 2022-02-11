package ir.amozkade.advancedAsisstiveTouche.mvvm.mainAssistiveTouch

sealed class GoogleDriveUploadState {
    data class Uploading(val callBackData: Any?, val progress: Int, val fileSize: Int) : GoogleDriveUploadState()
    object Failed : GoogleDriveUploadState()
    object UnAuthorized : GoogleDriveUploadState()
    object Finished : GoogleDriveUploadState()
    object SignOutFromGoogleSignInToGetNewToken : GoogleDriveUploadState()
}