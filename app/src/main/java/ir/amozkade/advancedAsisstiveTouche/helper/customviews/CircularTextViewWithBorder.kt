package ir.amozkade.advancedAsisstiveTouche.helper.customviews

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import ir.amozkade.advancedAsisstiveTouche.AppModule
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.buttons.AppButton
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.buttons.ContactButton
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.buttons.NoActionButton
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.models.ButtonModelInPanel
import ir.amozkade.advancedAsisstiveTouche.mvvm.preferenceActivity.Setting
import ir.amozkade.advancedAsisstiveTouche.mvvm.preferenceActivity.SettingRepository
import ir.mobitrain.applicationcore.helper.Converters
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.lang.Exception
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.math.min

class CircularTextViewWithBorder constructor(context: Context,attrs: AttributeSet): View(context,attrs) {

    private var showsEmptyActions: Boolean = false
    private var canvas: Canvas? = null
    private var widthOfSubtitle: Float = 23f
    private lateinit var iconDrawablePaint: Paint
    private var button: ButtonModelInPanel? = null
    private var bitmap: Bitmap? = null
    private lateinit var subTitleTextPaint: TextPaint
    private lateinit var circlePaint: Paint
    private lateinit var textPaint: Paint
    private var userSelectedColor: Int = Color.WHITE
    private var textIcon: String? = null
    private var subTitle: String? = null
    private var fontSize = 24f
    private var margin = Converters.convertIntToDP(4, context)
    private var maxRadius = min(width, height) * (80f / 100f)
    private var bottomOfCircle = maxRadius + margin * 2
    private var rectOfDrawable: Rect = Rect(0, 0, 0, 0)
    private var subtitleFontSize = 36
    private var center = width / 2
    private var isCircularEnabled = false
    private var isMarqueeEnabled = false
    private var inDecreaseMode = true
    private var inIncreaseMode = false
    private val isFa = Locale.getDefault().language.contains("fa")
    private var firstTime: Boolean = true
    private var isEndOfAnimation = false
    private var typeface: Typeface? = null
    private lateinit var setting:Setting


    fun init(settingRepository: SettingRepository) {
        setting = settingRepository.getCashedModel()
        isMarqueeEnabled = setting.enableMarqueeAnimation
        isCircularEnabled = setting.enableCircularButton
        subtitleFontSize = setting.panelButtonsTextSize
        userSelectedColor = setting.panelButtonsColor

        val userSelectedFontName: String = setting.userSelectedFontName
                ?: "fonts/sfSymbol.ttf"
        typeface = if (userSelectedFontName == "fonts/sfSymbol.ttf") {
            try {
                Typeface.createFromAsset(context.assets, userSelectedFontName)
            } catch (e: Exception) {
                null
            }
        } else {

            Typeface.createFromFile(File("${AppModule.provideAppDir(context)}/$userSelectedFontName"))
        }

        textPaint = Paint()
        textPaint.isAntiAlias = true
        textPaint.textSize = fontSize
        textPaint.color = userSelectedColor
        textPaint.typeface = typeface


        subTitleTextPaint = TextPaint()
        subTitleTextPaint.isAntiAlias = true
        subTitleTextPaint.textSize = subtitleFontSize.toFloat()
        subTitleTextPaint.color = userSelectedColor
        subTitleTextPaint.textAlign = Paint.Align.LEFT
        subTitleTextPaint.isLinearText = true
        val fontId = if (isFa) R.font.iransans_bold else R.font.sf_pro_rounded_bold
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            subTitleTextPaint.typeface = context.resources.getFont(fontId)
        } else {
            subTitleTextPaint.typeface = ResourcesCompat.getFont(context, fontId)
        }

        circlePaint = Paint()
        circlePaint.strokeWidth = 4f
        circlePaint.isAntiAlias = true

        circlePaint.style = Paint.Style.FILL
        circlePaint.color = userSelectedColor

        circlePaint.style = Paint.Style.STROKE
        circlePaint.color = userSelectedColor

        iconDrawablePaint = Paint()
        iconDrawablePaint.isAntiAlias = true
    }

    private var textX = 0f
    private var service = Executors.newSingleThreadScheduledExecutor()
    private var isRunning = false


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (!showsEmptyActions && button?.button is NoActionButton) return
        center = width / 2
        maxRadius = min(width, height) * (80f / 100f)
        bottomOfCircle = maxRadius + Converters.convertIntToDP(16, context)
        textIcon?.let {
            fontSize = maxRadius * (60f / 100f)
            textPaint.textSize = fontSize
        }
        widthOfSubtitle = subTitleTextPaint.measureText(subTitle ?: "")
        calculateDrawable()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (!showsEmptyActions && button?.button is NoActionButton) return
        this.canvas = canvas
        canvas?.save()
        clearCanvas(canvas)
        if (button?.button is AppButton || button?.button is ContactButton && bitmap == null) {
            calculateDrawable()
        }
        bitmap?.let {
            canvas?.drawBitmap(it, rectOfDrawable, rectOfDrawable, iconDrawablePaint)
        }

//      ************* Draw Circle *************
        drawCircleIfNeeded(canvas)
//      ************* Draw Circle *************

        textIcon?.let {
            canvas?.drawText(textIcon
                    ?: "", center.toFloat() - (fontSize / 2), (maxRadius / 2) + (fontSize / 2) + (margin / 2), textPaint)
        }
        drawSubtitle(canvas)
        canvas?.restore()
    }

    private fun drawSubtitle(canvas: Canvas?) {
        if (isMarqueeEnabled && widthOfSubtitle > width) {
            if (!isRunning) {
                textX = widthOfSubtitle
                isRunning = true
                service.scheduleWithFixedDelay({
                    if (!isEndOfAnimation) {
                        GlobalScope.launch(Main) {
                            invalidate()
                        }
                    }
                }, 200, 16, TimeUnit.MILLISECONDS)
            } else {
                if (inDecreaseMode) {
                    inIncreaseMode = false
                    inDecreaseMode = true
                    textX -= 1f
                    if (textX <= -widthOfSubtitle + width - margin) {
                        inDecreaseMode = false
                        inIncreaseMode = true
                        waitTimerInEndOfWidth()
                    }
                } else if (inIncreaseMode) {

                    inDecreaseMode = false
                    inIncreaseMode = true
                    textX += 1f
                    if (textX >= widthOfSubtitle - width + margin) {
                        inIncreaseMode = false
                        inDecreaseMode = true
                        waitTimerInEndOfWidth()
                    }
                }
                if (firstTime) {
                    firstTime = false
                    textX = 0f
                }
                canvas?.drawText(subTitle ?: "", 0, subTitle?.length
                        ?: 0, textX, bottomOfCircle, subTitleTextPaint)
            }

        } else {
            subTitleTextPaint.textAlign = Paint.Align.CENTER
            canvas?.drawText(subTitle ?: "", center.toFloat(), bottomOfCircle, subTitleTextPaint)
        }
    }

    private fun waitTimerInEndOfWidth() {
        isEndOfAnimation = true
        Executors.newSingleThreadScheduledExecutor().schedule({
            isEndOfAnimation = false
        }, 1000, TimeUnit.MILLISECONDS)
    }

    private fun clearCanvas(canvas: Canvas?) {
        if (button?.button !is AppButton && button?.button !is ContactButton && button?.button !is NoActionButton) {
            bitmap?.let {
                it.eraseColor(Color.TRANSPARENT)
                canvas?.drawBitmap(it, 0f, 0f, iconDrawablePaint)
            }
        } else {
            textIcon = ""
            canvas?.drawText("", center.toFloat(), maxRadius - subtitleFontSize, textPaint)
        }
    }

    private fun drawCircleIfNeeded(canvas: Canvas?) {
        if (bitmap != null || !isCircularEnabled) return
        circlePaint.style = Paint.Style.FILL
        val red = Color.red(userSelectedColor)
        val green = Color.green(userSelectedColor)
        val blue = Color.blue(userSelectedColor)
        circlePaint.color = Color.argb(50, red, green, blue)
        canvas?.drawCircle(center.toFloat(), (maxRadius / 2) + margin, (maxRadius / 2f) - margin, circlePaint)

        circlePaint.style = Paint.Style.STROKE
        circlePaint.color = userSelectedColor
        canvas?.drawCircle(center.toFloat(), (maxRadius / 2) + margin, (maxRadius / 2f) - margin, circlePaint)
    }

    fun drawButton(button: ButtonModelInPanel, showsEmptyActions: Boolean) {
        this.button = button // don't move down we need init button class
        this.showsEmptyActions = showsEmptyActions
        bitmap = null //clear bitmap
        if (!showsEmptyActions && button.button is NoActionButton) return
        button.unicodeIcon?.let {
            textIcon = it
        }
        subTitle = button.getTitleBaseOnButtonType(context)
        invalidate()
    }

    private fun calculateDrawable() {
        val drawable = when (button?.button) {
            is AppButton -> {
                button?.icon
            }
            is NoActionButton -> {
                ContextCompat.getDrawable(context, R.drawable.ic_dashed_add)
            }
            is ContactButton -> {
                button?.icon
            }
            else -> {
                null
            }
        }
        drawable?.let {
            bitmap = getBitmapFromDrawable(it)
        }
    }

    private fun getBitmapFromDrawable(drawable: Drawable): Bitmap? {

        val maxRadius = maxRadius.toInt()
        val margin = margin.toInt()
        val bmp = Bitmap.createBitmap(center + (maxRadius / 2) - margin, maxRadius, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        rectOfDrawable = Rect(
                (center - (maxRadius / 2)) + margin,
                margin * 2,
                center + (maxRadius / 2) - margin,
                maxRadius
        )
        drawable.bounds = rectOfDrawable
        drawable.draw(canvas)
        return if (button?.button is AppButton) bmp else getRoundedCornerBitmap(bmp, maxRadius)
    }

    private fun getRoundedCornerBitmap(bitmap: Bitmap, pixels: Int): Bitmap? {
        val output = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val color = setting.panelColorOverlay
        val paint = Paint()
        val rectF = RectF(rectOfDrawable)
        val roundPx = pixels.toFloat()
        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = color
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rectOfDrawable, rectOfDrawable, paint)
        return output
    }

    fun onDestroyCalled() {
        service.shutdown()
    }
}