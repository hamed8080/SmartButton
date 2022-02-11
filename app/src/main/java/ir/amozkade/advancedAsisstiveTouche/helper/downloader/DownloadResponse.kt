package ir.amozkade.advancedAsisstiveTouche.helper.downloader

import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.manageDictionaries.Dictionary

sealed class DownloadResponse {
    data class Downloading(val callBackData: Any?,val progress: Int, val fileSize: Int) : DownloadResponse()
    data class Finished (val callBackData:Any?): DownloadResponse()
    data class Uncompressed(val dictionary: Dictionary):DownloadResponse()
}
