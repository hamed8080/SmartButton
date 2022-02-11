package ir.amozkade.advancedAsisstiveTouche.mvvm.exception

import android.content.Context
import androidx.concurrent.futures.CallbackToFutureAdapter
import androidx.hilt.work.HiltWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.google.common.util.concurrent.ListenableFuture
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import ir.amozkade.advancedAsisstiveTouche.helper.api.DataState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@HiltWorker
class CrashReporterWorker @AssistedInject constructor(@Assisted context: Context,
                                                      @Assisted workerParams: WorkerParameters,
                                                      private val repository: ExceptionRepository) : ListenableWorker(context, workerParams) {


    override fun startWork(): ListenableFuture<Result> {
        return CallbackToFutureAdapter.getFuture { resultCallBack ->
            val handler = CoroutineExceptionHandler { _, _ ->
                Result.failure()
            }
            CoroutineScope(handler).launch {
                repository.logExceptions().collect { dataState ->
                    if (dataState is DataState.Success) {
                        resultCallBack.set(Result.success())
                    } else if (dataState is DataState.Loading) {
                        print("sending exceptions data to server")
                    }
                }
            }
        }
    }
}