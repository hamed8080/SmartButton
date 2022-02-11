package ir.amozkade.advancedAsisstiveTouche.mvvm.mainAssistiveTouch

sealed class GoogleDriveDownloadState{
    data class Downloading(val callBackData: Any?, val progress: Int, val fileSize: Int) : GoogleDriveDownloadState()
    object Failed : GoogleDriveDownloadState()
    object UnAuthorized : GoogleDriveDownloadState()
    object Finished : GoogleDriveDownloadState()
}
