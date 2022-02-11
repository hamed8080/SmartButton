package ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.graphics.Insets
import android.graphics.Point
import android.view.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import javax.inject.Inject

class ButtonGesture @Inject constructor(@ApplicationContext private val context: Context) : View.OnTouchListener, GestureDetector.SimpleOnGestureListener() {

    private lateinit var buttonParam: WindowManager.LayoutParams
    private lateinit var gestureDelegate: CustomButtonGestureDelegate
    private var display:Display? = null
    private var isAutomaticEdgeEnabled: Boolean = false
    private var isAutoAlphaEnabled: Boolean = false

    private var isLandscape: Boolean = false
    private var lastButtonX: Float = 0f
    private var lastButtonY: Float = 0f
    private var lastXOnScroll: Float = 0f
    private var mDetector: GestureDetector = GestureDetector(context, this)
    private var isMoving: Boolean = false
    private var currentAlpha = 1f

    interface CustomButtonGestureDelegate {
        fun doubleTap()
        fun longPress()
        fun singleTap()
        fun updateLayoutParamsOnGesture(lastButtonX: Float, lastButtonY: Float)
        fun updateButtonOpacity(alpha: Float)
    }

    fun setDelegate(gestureDelegate: CustomButtonGestureDelegate){
        this.gestureDelegate = gestureDelegate
    }

    fun setButtonParams(buttonParam:WindowManager.LayoutParams){
        this.buttonParam = buttonParam
    }

    fun setDisplay(display: Display?){
        //in api 30 and above crash
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.R){
            this.display = display
        }
    }

    fun setIsAutomaticEdgeEnabled(isAutomaticEdgeEnabled: Boolean){
        this.isAutomaticEdgeEnabled = isAutomaticEdgeEnabled
    }

    fun setIsAutoAlphaEnabled(isAutoAlphaEnabled:Boolean){
        this.isAutoAlphaEnabled = isAutoAlphaEnabled
    }

    override fun onDown(event: MotionEvent): Boolean {
        currentAlpha = 1f
        isMoving = false
        lastButtonX = buttonParam.x.toFloat()
        lastButtonY = buttonParam.y.toFloat()
        return true
    }

    override fun onLongPress(e: MotionEvent?) {
        gestureDelegate.longPress()
        super.onLongPress(e)
    }

    override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
        gestureDelegate.singleTap()
        return super.onSingleTapConfirmed(e)
    }

    override fun onDoubleTap(e: MotionEvent?): Boolean {
        gestureDelegate.doubleTap()
        return super.onDoubleTap(e)
    }

    override fun onScroll(startScrollEeven: MotionEvent, currentScrollEvent: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
        isMoving = true
        buttonParam.x = (lastButtonX + (currentScrollEvent.rawX - startScrollEeven.rawX)).toInt()
        buttonParam.y = (lastButtonY + (currentScrollEvent.rawY - startScrollEeven.rawY)).toInt()
        lastXOnScroll = currentScrollEvent.rawX
        gestureDelegate.updateLayoutParamsOnGesture(buttonParam.x.toFloat(), buttonParam.y.toFloat())
        return false
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(view: View?, event: MotionEvent?): Boolean {
        mDetector.setOnDoubleTapListener(this)
        if (event?.action == MotionEvent.ACTION_DOWN) {
            minOpacityJob?.cancel()
            setToMaxOpacityImmediately()
        } else if (event?.action == MotionEvent.ACTION_UP) {
            goToEdgeOfScreenIfAutomaticEdgeEnabled()
            setMinOpacityIfEnabled()
        }

        return mDetector.onTouchEvent(event)
    }

    private var minOpacityJob: Job? = null
    fun setMinOpacityIfEnabled() {
        if (isAutoAlphaEnabled) {
            minOpacityJob = GlobalScope.launch(Dispatchers.Main) {
                delay(3000)
                val anim = ValueAnimator.ofFloat(currentAlpha, .3f)
                anim.addUpdateListener {
                    if ((gestureDelegate as FloatingWindow).isPanelOpen) {
                        return@addUpdateListener
                    }
                    gestureDelegate.updateButtonOpacity(it.animatedValue as Float)
                    currentAlpha = it.animatedValue as Float
                }
                anim.start()
            }
        }
    }

    private fun setToMaxOpacityImmediately() {
        if (!isAutoAlphaEnabled) return
        gestureDelegate.updateButtonOpacity(1f)
    }

    fun configurationChanged(configuration: Configuration) {
        isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        goToEdgeOfScreenIfAutomaticEdgeEnabled()
    }

    private fun goToEdgeOfScreenIfAutomaticEdgeEnabled() {
        if (isAutomaticEdgeEnabled && isMoving) {
            GlobalScope.launch(Dispatchers.Main) {
                delay(100)
                val point = getPoint()
                val xOfDisplayInOrientation = if (isLandscape) point.y else point.x
                val leftOfScreen = 0
                val newXPosition = if (lastXOnScroll > (xOfDisplayInOrientation / 2)) xOfDisplayInOrientation else leftOfScreen
                val anim = ValueAnimator.ofInt(buttonParam.x, newXPosition)
                anim.duration = 200
                anim.addUpdateListener {
                    buttonParam.x = it.animatedValue as Int
                    gestureDelegate.updateLayoutParamsOnGesture(buttonParam.x.toFloat(), buttonParam.y.toFloat())
                }
                anim.start()
            }
        }
    }

    @Suppress("DEPRECATION")
    fun getPoint():Point{
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            val windowManager:WindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val bounds = windowManager.currentWindowMetrics.bounds

            val currentWindowMetrics = windowManager.currentWindowMetrics
            val windowInsets = currentWindowMetrics.windowInsets
            var insets: Insets = windowInsets.getInsets(WindowInsets.Type.navigationBars())
            windowInsets.displayCutout?.run {
                insets = Insets.max(insets, Insets.of(safeInsetLeft, safeInsetTop, safeInsetRight, safeInsetBottom))
            }
            val insetsWidth = insets.right + insets.left
            val insetsHeight = insets.top + insets.bottom
            Point(bounds.width() - insetsWidth,bounds.height() - insetsHeight)
        }else{
            val point = Point()
            display?.getSize(point)
            point
        }
    }

}