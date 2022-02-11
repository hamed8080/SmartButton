package ir.amozkade.advancedAsisstiveTouche.mvvm.exception

import android.content.Context
import android.os.Build
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.amozkade.advancedAsisstiveTouche.BuildConfig
import ir.amozkade.advancedAsisstiveTouche.mvvm.exception.di.ExceptionDao
import ir.amozkade.advancedAsisstiveTouche.mvvm.preferenceActivity.SettingRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class ExceptionHandler @Inject constructor(@ApplicationContext private val context: Context,
                                           private val settingRepository: SettingRepository,
                                           private val exceptionDao: ExceptionDao) : Thread.UncaughtExceptionHandler {

    @Volatile
    var isExceptionLogged = false //prevent duplicate into room logger table from access another thread

    //TODO:Disabled oldHandler to terminate app after log exception but i think this is important and must be enabled again
    private var oldHandler: Thread.UncaughtExceptionHandler? = null

    companion object {

        fun getCustomExceptionModel(e: Throwable , settingRepository: SettingRepository): CustomException {
            val traces = loopInCause(e)
            val token = settingRepository.getCashedModel().firebaseToken
            return CustomException(message = e.message,
                    traces = traces,
                    date = Date(),
                    api = Build.VERSION.SDK_INT,
                    device = "${Build.MANUFACTURER}-${Build.MODEL}",
                    appVersion = BuildConfig.VERSION_NAME,
                    token = token
            )
        }

        private fun loopInCause(e: Throwable): String {
            var traces = ""
            var haveCause = e.cause != null
            var cause = e
            while (haveCause) {
                val firstTrace = cause.stackTrace.first()
                cause.stackTrace.map { trace ->
                    var message = ""
                    if (trace == firstTrace) {
                        message = "msg:${cause.message}\n"
                    }
                    message += "filename:${trace.fileName} lineNumber:${trace.lineNumber}methodName:${trace.methodName}\n"
                    message
                }.forEach { trace ->
                    traces += trace
                }
                if (cause.cause != null) {
                    val newCause = cause.cause ?: break
                    cause = newCause
                    haveCause = true
                } else {
                    haveCause = false
                }
            }
            return traces
        }
    }

    fun startSyncExceptionWithServer() {
        //oldHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(this)
        startSendJob()
    }

    private fun startSendJob() {
        val uploadWorkRequest = OneTimeWorkRequestBuilder<CrashReporterWorker>()
                .build()
        WorkManager.getInstance(context).enqueue(uploadWorkRequest)
    }

    override fun uncaughtException(t: Thread, exception: Throwable) {
        if (BuildConfig.DEBUG) {
            exception.printStackTrace()
        }
        logException(t , exception)
        isExceptionLogged = true
    }

    private fun logException(thread:Thread  ,e: Throwable) {
        if (!isExceptionLogged) {
            GlobalScope.launch {
                exceptionDao.insert(getCustomExceptionModel(e,settingRepository))
            }
            oldHandler?.uncaughtException(thread, e)
        }
    }
}