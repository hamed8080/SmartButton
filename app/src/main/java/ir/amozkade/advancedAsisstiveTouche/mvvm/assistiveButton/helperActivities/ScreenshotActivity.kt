package ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.helperActivities

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.Image
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import androidx.annotation.RequiresApi
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.FloatingWindow
import ir.amozkade.advancedAsisstiveTouche.mvvm.mainAssistiveTouch.MainAssistiveTouchActivity
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

@Suppress("WrongConstant", "DEPRECATION")
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
@TargetApi(Build.VERSION_CODES.O_MR1)
class ScreenshotActivity : Activity(), ImageReader.OnImageAvailableListener {

    private lateinit var callback: MediaProjectionStopCallback
    private var mediaProjection: MediaProjection? = null
    private lateinit var mediaProjectionManager: MediaProjectionManager
    private val mediaProjectionRequestCode = 999
    private var dir: String? = null
    private var virtualDisplayName = "SmartButton"
    private var startFileName = "SmartButton"
    private val displayFlagsPermissions = DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY or DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC
    private var mImageReader: ImageReader? = null
    private var mVirtualDisplay: VirtualDisplay? = null
    private var mDensity = 0
    private var mWidth = 0
    private var mHeight = 0
    private var isTacked = false
    private var imagePath = ""
    private var mHandler = Handler()


    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainAssistiveTouchActivity.mainActivityInstance?.get()?.finish()
        createMediaProjectionInstance()
    }

    private fun createMediaProjectionInstance() {
        mediaProjectionManager = this.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        this.startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), mediaProjectionRequestCode)
    }

    private fun stopProjection() {
        mHandler.post {
            mediaProjection?.stop()
        }
    }

    private fun createVirtualDisplay() {
        mWidth = this.resources?.displayMetrics?.widthPixels ?: 0
        mHeight = this.resources?.displayMetrics?.heightPixels ?: 0
        mDensity = this.resources?.displayMetrics?.densityDpi ?: 0

        // start capture reader
        mImageReader = ImageReader.newInstance(mWidth, mHeight, PixelFormat.RGBA_8888, 5)
        mVirtualDisplay = mediaProjection?.createVirtualDisplay(virtualDisplayName, mWidth, mHeight, mDensity, displayFlagsPermissions, mImageReader?.surface, null, mHandler)
        mImageReader?.setOnImageAvailableListener(this, mHandler)
        callback = MediaProjectionStopCallback()
        mediaProjection?.registerCallback(callback, mHandler)
    }


    private fun createScreenshotFolderIfNotExist() {
        val externalFilesDir: File? = Environment.getExternalStorageDirectory()
        dir = externalFilesDir?.absolutePath + "/Screenshots/"
        val storeDirectory = File(dir ?: "")
        if (!storeDirectory.exists()) {
            storeDirectory.mkdirs()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && resultCode == RESULT_OK && data != null && requestCode == mediaProjectionRequestCode) {
            mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data)
            createScreenshotFolderIfNotExist()
            createVirtualDisplay()
        }
    }

    override fun onImageAvailable(reader: ImageReader) {
        var image: Image? = null
        var fos: FileOutputStream? = null
        var bitmap: Bitmap? = null
        try {
            image = reader.acquireLatestImage()
            if (image != null && !isTacked) {
                val planes = image.planes
                val buffer = planes[0].buffer
                // create bitmap
                val pixelStride = planes[0].pixelStride
                val rowStride = planes[0].rowStride
                val rowPadding = rowStride - pixelStride * mWidth
                bitmap = Bitmap.createBitmap(mWidth + rowPadding / pixelStride, mHeight, Bitmap.Config.ARGB_8888)
                bitmap.copyPixelsFromBuffer(buffer)

                // write bitmap to a file
                val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale("en"))
                val dateString = formatter.format(Date())
                fos = FileOutputStream("$dir/$startFileName-$dateString.png")
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
                isTacked = true // prevent multiple screenshot file
                imagePath = "$dir/$startFileName-$dateString.png"
                showSuccessAlert()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            stopProjection()
        } finally {
            if (fos != null) {
                try {
                    fos.close()
                } catch (ioe: IOException) {
                    ioe.printStackTrace()
                }
            }
            bitmap?.recycle()
            image?.close()
        }
        stopProjection()
    }


    private fun showSuccessAlert() {
        FloatingWindow.floatingWindowService?.showWarn(title = getString(R.string.screenshot),
                subtitle = String.format(getString(R.string.screenshot_saved_at), "Screenshot/${imagePath.split("/").last()}"),
                imageId = null,
                imagePath = imagePath)
    }


    private inner class MediaProjectionStopCallback : MediaProjection.Callback() {
        override fun onStop() {
            mVirtualDisplay?.release()
            mImageReader?.setOnImageAvailableListener(null, null)
            finish()
        }
    }

}