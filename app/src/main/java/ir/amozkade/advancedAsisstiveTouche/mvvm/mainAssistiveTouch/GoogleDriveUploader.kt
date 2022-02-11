package ir.amozkade.advancedAsisstiveTouche.mvvm.mainAssistiveTouch

import android.webkit.MimeTypeMap
import ir.amozkade.advancedAsisstiveTouche.mvvm.preferenceActivity.SettingRepository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.io.*
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.math.ceil

class GoogleDriveUploader(private val authorization: String, private val file: File, private val mimeType: String? = null, private val settingRepository: SettingRepository) {

    companion object {
        const val CHUNK_LIMIT: Long = 262144 // = (256*1024) = 256 kilobyte
        const val INCOMPLETE = 308
        const val FAILED = 500
    }
    private val fileSize = file.length()

    suspend fun uploadFile() : Flow<GoogleDriveUploadState> = flow {
        var i: Long = 1
        var j: Long = CHUNK_LIMIT

        if (settingRepository.getCashedModel().isFailedUploadToGoogleDrive == true) {
            i = getRangeOfFailedUpload() ?: 1
        } else {
            getSessionUrlFromGoogleDrive()
        }

        while (i <= fileSize) {
            if (i + CHUNK_LIMIT >= fileSize) {
                j = fileSize - i + 1
            }
            val progress = ceil(100 * i / fileSize.toDouble()).toInt()
            emit(GoogleDriveUploadState.Uploading(progress = progress, fileSize = fileSize.toInt(), callBackData = null))
            val responseCode: Int = uploadFileChunk(i - 1, j)
            if(responseCode == HttpURLConnection.HTTP_OK){
                emit(GoogleDriveUploadState.Finished)
                break
            }
            else if (responseCode == FAILED) {
                emit(GoogleDriveUploadState.Failed)
                break
            } else {
                settingRepository.setFailedUploadToGoogleDrive(null)
                i += CHUNK_LIMIT
            }
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun FlowCollector<GoogleDriveUploadState>.getSessionUrlFromGoogleDrive(mimeType: String? = null) {
        val request = createRequest("https://www.googleapis.com/upload/drive/v3/files?uploadType=resumable", "POST")
        request.setRequestProperty("Authorization", authorization)
        request.setRequestProperty("X-Upload-Content-Type", mimeType
                ?: MimeTypeMap.getSingleton().getMimeTypeFromExtension(file.extension))
        request.setRequestProperty("X-Upload-Content-Length", fileSize.toString())
        request.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
        val body = "{\"name\": \"${file.name}\"}"
        request.setRequestProperty("Content-Length", java.lang.String.format(Locale.ENGLISH, "%d", body.toByteArray().size))
        val outputStream: OutputStream = request.outputStream
        outputStream.write(body.toByteArray())
        outputStream.close()
        request.connect()
        if (request.responseCode == HttpURLConnection.HTTP_OK) {
            val sessionUri = request.getHeaderField("location")
            settingRepository.setGoogleDriveUploadSessionURL(sessionUri)
        }
        if (request.responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
            request.disconnect()
            emit(GoogleDriveUploadState.UnAuthorized)
        }
    }

    private fun uploadFileChunk(chunkStart: Long, uploadBytes: Long): Int {
        settingRepository.getCashedModel().googleDriveUploadSessionURL?.let {
            try {
                val request = createRequest(it, "PUT")
                (mimeType ?: MimeTypeMap.getSingleton().getMimeTypeFromExtension(file.extension))?.let {mimeType->
                    request.setRequestProperty("Content-Type", mimeType)
                }
                request.setRequestProperty("Content-Length", uploadBytes.toString())
                request.setRequestProperty("Content-Range", "bytes " + chunkStart.toString() + "-" + (chunkStart + uploadBytes - 1).toString() + "/" + fileSize)

                val outputStream = request.outputStream

                val buffer = ByteArray(uploadBytes.toInt())
                val fileInputStream = FileInputStream(file)
                fileInputStream.channel.position(chunkStart)
                if (fileInputStream.read(buffer, 0, uploadBytes.toInt()) == -1) {
                    fileInputStream.close()
                }

                outputStream.write(buffer)
                outputStream.close()
                request.connect()
                return request.responseCode
            } catch (e: Exception) {
                GlobalScope.launch(IO){
                    settingRepository.setGoogleDriveUploadSessionURL(null)
                    settingRepository.setFailedUploadToGoogleDrive(true)
                }
                return FAILED
            }
        }
        return INCOMPLETE
    }

    private fun getRangeOfFailedUpload(): Long? {
        settingRepository.getCashedModel().googleDriveUploadSessionURL?.let {
            try {
                val request = createRequest(it, "PUT")
                request.setRequestProperty("Content-Length", "0")
                request.setRequestProperty("Content-Range", "bytes */$fileSize")

                request.connect()
                request.getHeaderField("Range")?.let {
                    //first +1 for index 0 and second +1 for start index of next chunk
                    return request.getHeaderField("Range").replace("bytes=", "").split("-").last().toLong() + 1 + 1
                }

            } catch (e: Exception) {
                return null
            }
        }
        return null
    }

    private fun createRequest(path: String, method: String): HttpURLConnection {
        val url = URL(path)
        val request = url.openConnection() as HttpURLConnection
        request.requestMethod = method
        request.doOutput = true
        request.doInput = true
        request.connectTimeout = 10000
        return request
    }
}