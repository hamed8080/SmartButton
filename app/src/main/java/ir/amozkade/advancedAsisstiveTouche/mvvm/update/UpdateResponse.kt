package ir.amozkade.advancedAsisstiveTouche.mvvm.update

sealed class UpdateResponse {
    object ShowMandatoryDialog : UpdateResponse()
    object FinishDownloading : UpdateResponse()
    data class ShowUpdateAlert(val updateApp: UpdateApp) : UpdateResponse()
    data class Downloading(val progress: Int) : UpdateResponse()
}
