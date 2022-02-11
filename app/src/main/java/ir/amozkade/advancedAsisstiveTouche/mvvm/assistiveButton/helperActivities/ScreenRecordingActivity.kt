package ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.helperActivities

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.MediaRecorder
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.DisplayMetrics
import androidx.annotation.RequiresApi
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.buttons.screenRecorder.ScreenRecordingService
import ir.amozkade.advancedAsisstiveTouche.mvvm.mainAssistiveTouch.MainAssistiveTouchActivity
import java.io.File
import java.util.*

@Suppress("WrongConstant", "DEPRECATION")
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
@TargetApi(Build.VERSION_CODES.O_MR1)
class ScreenRecordingActivity : Activity() {

    companion object {
        const val REQUEST_SCREEN_RECORDING_PERMISSION_CODE = 300
        var mediaRecorder: MediaRecorder? = null
        var virtualDisplay: VirtualDisplay? = null
        var mediaProjection: MediaProjection? = null
        var callBack : MediaProjectionStopCallback? = null
        var recordingFileName:String? = null

        fun stopRecording() {
            mediaRecorder?.stop()
            mediaRecorder?.reset()
            virtualDisplay?.release()
            mediaProjection?.stop()
            mediaProjection?.unregisterCallback(callBack)
            mediaRecorder = null
            mediaProjection = null
            virtualDisplay = null
            recordingFileName = null
        }
    }


    private lateinit var mediaProjectionManager: MediaProjectionManager
    private var virtualDisplayName = "SmartButton"
    private var startFileName = "SmartButton"
    private var dir: String? = null
    private lateinit var mDisplayMetrics: DisplayMetrics
    private val displayFlagsPermissions = DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY or DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainAssistiveTouchActivity.mainActivityInstance?.get()?.finish()
        createScreenRecordingFolderIfNotExist()
        mediaRecorder = MediaRecorder()
        mDisplayMetrics = resources.displayMetrics
        mediaProjectionManager = this.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        windowManager.defaultDisplay.getMetrics(mDisplayMetrics)
        prepareRecording()
        startRecording()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && data != null && requestCode == REQUEST_SCREEN_RECORDING_PERMISSION_CODE) {
            mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data)
            callBack = MediaProjectionStopCallback()
            mediaProjection?.registerCallback(callBack , Handler())
            startRecording()
        }
    }

    private fun startRecording() {
        // If mMediaProjection is null that means we didn't get a context, lets ask the user
        if (mediaProjection == null) {
            // This asks for user permissions to capture the screen
            startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), REQUEST_SCREEN_RECORDING_PERMISSION_CODE)
            return
        }
        startService(Intent(this, ScreenRecordingService::class.java))
        virtualDisplay = createVirtualDisplay()
        mediaRecorder?.start()
        finish()
    }

    private fun prepareRecording() {
        val width: Int = mDisplayMetrics.widthPixels
        val height: Int = mDisplayMetrics.heightPixels

        mediaRecorder?.setVideoSource(MediaRecorder.VideoSource.SURFACE)
        mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        mediaRecorder?.setVideoEncoder(MediaRecorder.VideoEncoder.H264)
        mediaRecorder?.setVideoEncodingBitRate(17500 * 1000)
        mediaRecorder?.setVideoFrameRate(60)
        mediaRecorder?.setVideoSize(width, height)
        recordingFileName = newFileName
        mediaRecorder?.setOutputFile(recordingFileName)
        try {
            mediaRecorder?.prepare()
        } catch (e: Exception) {
            e.printStackTrace()
            return
        }

    }

    private fun createVirtualDisplay(): VirtualDisplay? {
        val screenDensity = mDisplayMetrics.densityDpi
        val width = mDisplayMetrics.widthPixels
        val height = mDisplayMetrics.heightPixels
        return mediaProjection?.createVirtualDisplay(virtualDisplayName,
                width, height, screenDensity,
                displayFlagsPermissions,
                mediaRecorder?.surface, null /*Callbacks*/, null /*Handler*/)
    }

    private fun createScreenRecordingFolderIfNotExist() {
        val externalFilesDir: File? = Environment.getExternalStorageDirectory()
        dir = externalFilesDir?.absolutePath + "/ScreenRecording/"
        val storeDirectory = File(dir ?: "")
        if (!storeDirectory.exists()) {
            storeDirectory.mkdirs()
        }
    }

    private val newFileName: String
        get() {
            val formatter = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale("en"))
            return "$dir/$startFileName-${formatter.format(Date())}.mp4"
        }

    inner class MediaProjectionStopCallback : MediaProjection.Callback() {
        override fun onStop() {
            virtualDisplay?.release()
            finish()
        }
    }
}