package ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.animation

import android.animation.Animator
import android.content.res.Configuration
import android.view.View
import android.view.WindowManager
import android.view.animation.OvershootInterpolator
import androidx.core.view.ViewCompat
import ir.amozkade.advancedAsisstiveTouche.databinding.ButtonSmartButtonBinding
import ir.amozkade.advancedAsisstiveTouche.databinding.PanelSmartButtonBinding
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.FloatingWindow
import ir.amozkade.advancedAsisstiveTouche.mvvm.preferenceActivity.SettingRepository
import ir.mobitrain.applicationcore.helper.Converters
import ir.mobitrain.applicationcore.helper.animations.AnimationListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.reflect.Field

class Animation(
        private val buttonBinding: ButtonSmartButtonBinding,
        private val panelSmartButtonBinding: PanelSmartButtonBinding,
        private val buttonWindowManager: WindowManager,
        private val panelWindowManager: WindowManager,
        private val buttonParams: WindowManager.LayoutParams,
        private val panelParams: WindowManager.LayoutParams,
        private val service: FloatingWindow,
        private val animationEnabled: Boolean,
        private val isPagerEnable: Boolean,
        private val isLeftOrRightPanel: Boolean,
        private val settingRepository: SettingRepository
) {
    private var displayWidth = service.resources.displayMetrics.widthPixels
    private var displayHeight = service.resources.displayMetrics.heightPixels
    private val centerX = (displayWidth / 2).toFloat()
    private val centerY = (displayHeight / 2).toFloat()
    private var panelWidth: Int = 0
    private var panelHeight: Int = 0
    private var lastButtonX: Float = 0f
    private var lastButtonY: Float = 0f
    private var panel = if (isPagerEnable) panelSmartButtonBinding.vp else panelSmartButtonBinding.rcvButton
    private var fakeButton = panelSmartButtonBinding.fakeButtonImageView
    private var button = buttonBinding.button
    private val pageIndicator = panelSmartButtonBinding.pageIndicator
    private var isInAnimation = false

    init {
        disableAnimationUpdateFor(buttonParams)
        disableAnimationUpdateFor(panelParams)
    }

    private fun initMeasurePanelWidthAndHeight() {
        if (panelWidth == 0 && panelHeight == 0) {
            if (isPagerEnable) {
                panelWidth = service.resources.displayMetrics.widthPixels - Converters.convertIntToDP(24, service).toInt()
                val height = 2 * Converters.convertIntToDP(48, service)
                panelHeight = (3 * height).toInt() + Converters.convertIntToDP(36, service).toInt()
                panel.layoutParams.width = panelWidth
                panel.layoutParams.height = panelHeight
            } else {
                val spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                panelSmartButtonBinding.rcvButton.measure(spec, spec)
                if (!settingRepository.getCashedModel().isLeftMenu) {
                    panelSmartButtonBinding.rcvButton.layoutParams.width = (displayWidth * (settingRepository.getCashedModel().panelWidthPercent / 100f)).toInt()
                    panelWidth = panelSmartButtonBinding.rcvButton.layoutParams.width
                } else {
                    panelWidth = panelSmartButtonBinding.rcvButton.measuredWidth
                }
                panelHeight = panelSmartButtonBinding.rcvButton.measuredHeight
            }
        }
        pageIndicator.pivotX = 0f
        pageIndicator.pivotY = 0f
    }

    private fun animateEdgeWindow(showPanel: Boolean, isAnimationEnabled: Boolean) {
        if (!ViewCompat.isAttachedToWindow(panelSmartButtonBinding.root)) {
            panelWindowManager.addView(panelSmartButtonBinding.root, panelParams)
        }
        val startXForLocale = startXForLocale()
        val toX = toXForLocale()
        if (showPanel) {
            setMainButtonParamsAlphaTo(0f)
            setFlagToTouchablePanel(0)
            panel.x = toX
        } else {
            setMainButtonParamsAlphaTo(1f)
            panel.visibility = View.GONE
            setFlagToTouchablePanel(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
        }

        if (isAnimationEnabled) {
            panel.visibility = View.VISIBLE

            panel.alpha = if (showPanel) .0f else 1f
            panel.x = if (showPanel) startXForLocale else toX
            panel.animate()
                    ?.alpha(if (showPanel) 1f else .0f)
                    ?.translationX(if (showPanel) toX else startXForLocale)
                    ?.setInterpolator(OvershootInterpolator(.8f))
                    ?.setDuration(300)
                    ?.start()
        }
    }

    fun startAnimation(showPanel: Boolean) {
        initMeasurePanelWidthAndHeight()
        if (isLeftOrRightPanel) {
            animateEdgeWindow(showPanel, animationEnabled)
        } else {
            animateCenterWindow(showPanel, animationEnabled)
        }
    }

    private fun animateCenterWindow(showPanel: Boolean, animationEnabled: Boolean) {
        if (isInAnimation) return
        isInAnimation = true
        val maxPanelWidth = displayWidth - Converters.convertIntToSP(48, service)
        val maxPanelHeight = (panelHeight).toFloat()
        if (!ViewCompat.isAttachedToWindow(panelSmartButtonBinding.root)) {
            panelWindowManager.addView(panelSmartButtonBinding.root, panelParams)
        }

        if (animationEnabled) {
            if (showPanel) {
                setFlagToTouchablePanel(0)
                setMainButtonParamsAlphaTo(0f)
            }
            panelSmartButtonBinding.fakeButtonImageView.visibility = View.VISIBLE
            panel.x = if (showPanel) correctEdge else panel.x
            panel.y = if (showPanel) lastButtonY else panel.y
            panel.alpha = if (showPanel) .1f else 1f
            val toX = (displayWidth / 2f) - (panelWidth / 2f)
            val toY = (displayHeight / 2) - (panelHeight / 2f)
            val fromScaleX = button.width / maxPanelWidth
            val fromScaleY = button.height / maxPanelHeight
            panel.scaleX = if (showPanel) fromScaleX else 1f
            panel.scaleY = if (showPanel) fromScaleY else 1f
            panel.pivotX = 0f
            panel.pivotY = 0f

            //paging indicator must place here after panel set scale to get correct position
            setPagingIndicatorToPosition(if (showPanel) View.VISIBLE else View.GONE)
            panel.animate()
                    ?.scaleX(if (showPanel) 1f else fromScaleX)
                    ?.scaleY(if (showPanel) 1f else fromScaleY)
                    ?.alpha(if (showPanel) 1f else .1f)
                    ?.translationX(if (showPanel) toX else correctEdge)
                    ?.translationY(if (showPanel) toY else lastButtonY)
                    ?.setInterpolator(OvershootInterpolator(.8f))
                    ?.setDuration(300)
                    ?.setListener(object : AnimationListener() {
                        override fun onAnimationEnd(animation: Animator?) {
                            isInAnimation = false
                            if (!showPanel) {
                                panel.visibility = View.GONE
                                GlobalScope.launch(Dispatchers.Main) {
                                    delay(30)
                                    panelSmartButtonBinding.fakeButtonImageView.visibility = View.GONE
                                    setFlagToTouchablePanel(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
                                }
                            }
                        }
                    })
                    ?.start()

////        1- full root view to size of device width and height
            fakeButton.setImageDrawable(buttonBinding.imageView.drawable)
            fakeButton.layoutParams.width = button.width
            fakeButton.layoutParams.height = button.height
            fakeButton.x = if (showPanel) correctEdge else centerX
            fakeButton.y = if (showPanel) lastButtonY else centerY
            fakeButton.alpha = if (showPanel) 1f else 0f
            fakeButton.pivotX = 0f
            fakeButton.pivotY = 0f
            fakeButton.animate()
                    .scaleX(if (showPanel) 0f else 1f)
                    .scaleY(if (showPanel) 0f else 1f)
                    .alpha(if (showPanel) 0f else 1f)
                    .translationX(if (showPanel) centerX else correctEdge)
                    .translationY(if (showPanel) centerY else lastButtonY)
                    .setInterpolator(OvershootInterpolator(.8f))
                    .setDuration(300)
                    .setListener(object : AnimationListener() {
                        override fun onAnimationEnd(animation: Animator?) {
                            if (!showPanel) {
                                setMainButtonParamsAlphaTo(1f)
                            } else {
                                setPagingIndicatorToPosition(View.VISIBLE)
                            }
                        }
                    })
                    .start()
        } else {
            isInAnimation = false
            panel.pivotX = 0f
            panel.pivotY = 0f
            if (showPanel) {
                panel.visibility = View.VISIBLE
                panel.x = (displayWidth / 2f) - (panelWidth / 2f)
                panel.y = (displayHeight / 2) - (panelHeight / 2f)
                button.visibility = View.GONE
                setFlagToTouchablePanel(0)
                setPagingIndicatorToPosition(View.VISIBLE)
            } else {
                panel.visibility = View.GONE
                setPagingIndicatorToPosition(View.GONE)
                button.visibility = View.VISIBLE
                setFlagToTouchablePanel(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
            }
        }
    }

    private fun setPagingIndicatorToPosition(visibility: Int) {
        if (isPagerEnable) {
            pageIndicator.y = (panel.y + panel.height) - pageIndicator.height
            pageIndicator.visibility = visibility
        }
    }

    private fun setMainButtonParamsAlphaTo(alpha: Float) {
        buttonParams.alpha = alpha
        if (ViewCompat.isAttachedToWindow(buttonBinding.root)) {
            buttonWindowManager.updateViewLayout(buttonBinding.root, buttonParams)
        }
    }

    fun setFlagToTouchablePanel(flags: Int) {
        panelParams.flags = flags
        if (ViewCompat.isAttachedToWindow(panelSmartButtonBinding.root)) {
            panelWindowManager.updateViewLayout(panelSmartButtonBinding.root, panelParams)
        }
    }

    fun update(lastButtonX: Float, lastButtonY: Float) {
        this.lastButtonX = lastButtonX
        this.lastButtonY = lastButtonY
    }

    fun updateLayoutWidthHeightOnOrientationChange(newConfig: Configuration) {
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            val tempWidth = displayWidth
            displayWidth = displayHeight
            displayHeight = tempWidth
        } else {
            val tempWidth = displayWidth
            displayWidth = displayHeight
            displayHeight = tempWidth
        }
    }

    private val correctEdge: Float
        get() {
            return if (lastButtonX >= displayWidth) {
                lastButtonX - service.buttonSize.width
            } else lastButtonX
        }


    private fun disableAnimationUpdateFor(params: WindowManager.LayoutParams) {
        val className = "android.view.WindowManager\$LayoutParams"
        try {
            val layoutParamsClass = Class.forName(className)
            val privateFlags: Field = layoutParamsClass.getField("privateFlags")
            val noAnim: Field = layoutParamsClass.getField("PRIVATE_FLAG_NO_MOVE_ANIMATION")
            var privateFlagsValue: Int = privateFlags.getInt(params)
            val noAnimFlag: Int = noAnim.getInt(params)
            privateFlagsValue = privateFlagsValue or noAnimFlag
            privateFlags.setInt(params, privateFlagsValue)
        } catch (e: Exception) {
        }
    }

    private fun isButtonInLeftOfScreen(): Boolean {
        return lastButtonX < (displayWidth / 2)
    }

    private fun startXForLocale(): Float {
        val isButtonOnLeftOfScreen = isButtonInLeftOfScreen()
        return if (isButtonOnLeftOfScreen) -(panelWidth).toFloat() else displayWidth.toFloat()
    }

    private fun toXForLocale(): Float {
        val isButtonOnLeftOfScreen = isButtonInLeftOfScreen()
        return if (isButtonOnLeftOfScreen) 0f else (displayWidth.toFloat()) - panelWidth
    }
}

