package ir.amozkade.advancedAsisstiveTouche.helper

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.mvvm.BaseActivityDelegate
import ir.mobitrain.applicationcore.logger.LoggerActivity.Companion.logRequest
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.withContext
import retrofit2.Call
import java.io.InterruptedIOException
import java.lang.Exception
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException
import javax.inject.Inject

class ExceptionServerResponse @Inject constructor(@ApplicationContext private val context: Context) {

    suspend fun <T> handleException(e: Exception, delegate: BaseActivityDelegate, call: Call<T>) = withContext(Main) {
        delegate.hideLoading()
        if (e is InterruptedIOException || e is TimeoutException || e is SocketTimeoutException) {
            delegate.showWarn(context.getString(R.string.timeout_title), context.getString(R.string.timeout), imageId = R.drawable.ic_server_error)
        } else if (e is ConnectException) {
            delegate.showWarn(context.getString(R.string.failed_connect_title), context.getString(R.string.failed_connect), imageId = R.drawable.ic_server_error)
        }
        logRequest(call, e.cause, context)
    }
}