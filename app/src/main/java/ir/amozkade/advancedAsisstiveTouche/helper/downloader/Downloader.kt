package ir.amozkade.advancedAsisstiveTouche.helper.downloader

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.amozkade.advancedAsisstiveTouche.JWT
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.helper.Common
import ir.amozkade.advancedAsisstiveTouche.helper.InAppException
import ir.amozkade.advancedAsisstiveTouche.helper.api.DataState
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import java.io.FileNotFoundException
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import javax.inject.Inject
import kotlin.math.ceil


@Suppress("BlockingMethodInNonBlockingContext", "EXPERIMENTAL_API_USAGE")
class Downloader @Inject constructor(private val common: Common, @ApplicationContext private val context: Context) {

    suspend fun download(
            fileUrl: String,
            saveFilePath: String,
            authorization: String? = null,
            callBackData: Any? = null
    ): Flow<DataState<DownloadResponse>> = flow {

        withContext(IO) {
            try {
                val url = URL(fileUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.doInput = true
                authorization?.let {
                    connection.setRequestProperty("Authorization", authorization)
                }
                connection.connect()
                val contentLength = connection.contentLength
                downloadAsync(saveFilePath, contentLength, connection, callBackData)
            } catch (e: MalformedURLException) {
                emitError(e)
            } catch (e: FileNotFoundException) {
                emitError(e)
            } catch (e: IOException) {
                emitError(e)
            }
        }
    }

    private suspend fun FlowCollector<DataState<DownloadResponse>>.emitError(e: IOException) {
        emit(DataState.Error(InAppException(
                context.getString(R.string.download_failed_title),
                String.format(context.getString(R.string.download_failed_message), e.message),
                R.drawable.img_download)))
    }

    private suspend fun FlowCollector<DataState.Success<DownloadResponse>>.downloadAsync(
            saveFilePath: String,
            contentLength: Int,
            connection: HttpURLConnection,
            callBackData: Any?
    ) {
        val otp = common.fileOutputStream(saveFilePath)
        val data = ByteArray(4096)
        var count: Int
        var downloadedSize = 0
        while (connection.inputStream.read(data).also { count = it } != -1) {
            otp.write(data, 0, count)
            downloadedSize += count
            val progress = ceil(100 * downloadedSize / contentLength.toDouble()).toInt()
            emit(DataState.Success(DownloadResponse.Downloading(callBackData, progress, contentLength)))
        }
        otp.flush()
        otp.close()
        emit(DataState.Success(DownloadResponse.Finished(callBackData)))
    }
}