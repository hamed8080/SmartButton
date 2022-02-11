package ir.amozkade.advancedAsisstiveTouche.mvvm.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import dagger.hilt.android.AndroidEntryPoint
import ir.amozkade.advancedAsisstiveTouche.BuildConfig
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.databinding.ActivitySplashBinding
import ir.amozkade.advancedAsisstiveTouche.mvvm.BaseActivity
import ir.amozkade.advancedAsisstiveTouche.mvvm.mainAssistiveTouch.MainAssistiveTouchActivity

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : BaseActivity() {

    lateinit var mBinding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        hideLoading()
        if (BuildConfig.DEBUG) {
            startMainActivity()
            finish()
        } else {
            animateTextScaleUp()
        }
    }

    private fun animateTextScaleUp() {

        val animation: Animation = AnimationUtils.loadAnimation(this, R.anim.splash_anim)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                startMainActivity()
                finish()
            }

            override fun onAnimationStart(animation: Animation?) {
            }

        })
        mBinding.txtTitle.startAnimation(animation)
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainAssistiveTouchActivity::class.java)
        startActivity(intent)
    }
}
