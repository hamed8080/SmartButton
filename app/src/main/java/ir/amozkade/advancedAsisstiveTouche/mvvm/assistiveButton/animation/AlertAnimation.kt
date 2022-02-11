package ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.animation

import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.graphics.PixelFormat
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.WindowManager
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.databinding.FloatingWindowAlertBinding
import ir.amozkade.advancedAsisstiveTouche.helper.extensions.capitalized
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.FloatingWindow
import ir.mobitrain.applicationcore.helper.Converters
import ir.mobitrain.applicationcore.helper.animations.AnimationListener
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main

class AlertAnimation(private val service: FloatingWindow, private val wm: WindowManager) {

    private val displayHeight = service.resources.displayMetrics.heightPixels
    private val displayWidth = service.resources.displayMetrics.widthPixels

    fun start(title: String, subtitle: String, imageId: Int? , imagePath:String? = null) {
        showWarn(title, subtitle, imageId , imagePath)
    }

    @SuppressLint("DefaultLocale")
    fun showWarn(title: String, subtitle: String, imageId: Int?, imagePath: String?) {
        val alertBinding = DataBindingUtil.inflate<FloatingWindowAlertBinding>(
            LayoutInflater.from(ContextThemeWrapper(service, R.style.AppTheme)),
            R.layout.floating_window_alert,
            null,
            false
        )
        imageId?.let {
            alertBinding.img.setImageResource(imageId)
        }
        imagePath?.let {
            val opt = BitmapFactory.Options()
            opt.inSampleSize = 2
            val bitmap = BitmapFactory.decodeFile(imagePath, opt)
            alertBinding.img.scaleType = ImageView.ScaleType.CENTER_INSIDE
            alertBinding.img.setImageBitmap(bitmap)
            alertBinding.img.cornerRadius = 0f
            alertBinding.img.y = 10f
        }
        alertBinding.txtTitle.text = title.capitalized()
        alertBinding.txtSubTitle.text = subtitle
        val alertParams = WindowManager.LayoutParams(
                FloatingWindow.getWindowParamType(),
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSPARENT)
        alertParams.width = displayWidth - Converters.convertIntToDP(24, service).toInt()
        alertParams.height = displayHeight / 2
        alertParams.x = 0
        alertParams.y = displayHeight - alertBinding.root.height
        wm.addView(alertBinding.root, alertParams)
        CoroutineScope(Main).launch{
            alertBinding.btnDismiss.setOnClickListener {
                animateToHideAlert(false, alertBinding)
                cancel()
            }
            delay(5000)
            animateToHideAlert(false, alertBinding)
        }
        animateToHideAlert(true, alertBinding)
    }

    private fun animateToHideAlert(show: Boolean = false, binding: FloatingWindowAlertBinding) {
        if (show) {
            binding.containerToAnimate.y = displayHeight.toFloat()
        }
        val from = if (show) displayHeight.toFloat() else 0f
        val to = if (show) 0f else binding.root.height.toFloat()
        val anim: ObjectAnimator = ObjectAnimator.ofFloat(binding.containerToAnimate, "y", from, to)
        anim.duration = 400L
        anim.interpolator = OvershootInterpolator(.8f)
        anim.addListener(object : AnimationListener() {
            override fun onAnimationEnd(animation: Animator?) {
                if (!show) {
                    wm.removeView(binding.root)
                }
            }
        })
        anim.start()
    }

}