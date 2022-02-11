package ir.amozkade.advancedAsisstiveTouche.mvvm.mainAssistiveTouch.utils


sealed class MainStateEvent {
    object RemoveApp :MainStateEvent()
    data class FirebaseAnalytics(val activityName:String):MainStateEvent()
    data class ExchangeCode(val serverAuthCode: String?) : MainStateEvent()
    object UploadToGoogleDrive:MainStateEvent()
    object DownloadBackupFromGoogleDrive:MainStateEvent()
    data class RequestGoogleApiRefreshToken(val uploadOrDownload: UploadOrDownload):MainStateEvent()
    object StartCheckingUpdateWorkManager:MainStateEvent()
    data class DownloadApkUpdate(val url:String):MainStateEvent()
}

enum class UploadOrDownload{
    Download,
    Upload
}
