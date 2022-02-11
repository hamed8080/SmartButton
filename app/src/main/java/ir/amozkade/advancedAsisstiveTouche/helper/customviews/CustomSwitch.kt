package ir.amozkade.advancedAsisstiveTouche.helper.customviews

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import ir.amozkade.advancedAsisstiveTouche.R
import ir.mobitrain.applicationcore.helper.Converters

class CustomSwitch : View {


    private var delegate: SwitchButton.OnSwitchChangeListener? = null
    private lateinit var thumbPaint: Paint
    private lateinit var trackRect: RectF
    private lateinit var trackPaint: Paint
    private var thumbWidth: Float = 32f
    private var round: Float = Converters.convertIntToDP(14, context)
    val padding = Converters.convertIntToDP(10, context)
    private val marginRightThumb = Converters.convertIntToDP(2, context)
    private var isChecked = false

    private fun setToUnChecking() {
        trackPaint.color = trackColor
        invalidate()
    }

    private fun setToChecking() {
        trackPaint.color = trackColor
        invalidate()
    }


    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setTypedAttributes(context.obtainStyledAttributes(attrs, R.styleable.CustomSwitch))
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setTypedAttributes(context.obtainStyledAttributes(attrs, R.styleable.CustomSwitch))
        init()
    }

    private fun setTypedAttributes(ta: TypedArray) {
        isChecked = ta.getBoolean(R.styleable.CustomSwitch_custom_switch_is_checked, false)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        thumbWidth = (height/2)  - padding - marginRightThumb
        trackRect.top = padding
        trackRect.left = padding
        trackRect.right = width.toFloat() - padding
        trackRect.bottom = height.toFloat() - padding
    }

    fun init() {
        trackPaint = Paint()
        trackPaint.isAntiAlias = true
        trackPaint.style = Paint.Style.FILL
        trackPaint.color = trackColor
        trackRect = RectF()

        thumbPaint = Paint()
        thumbPaint.isAntiAlias = true
        thumbPaint.style = Paint.Style.FILL
        thumbPaint.color = ContextCompat.getColor(context, R.color.white)
        thumbPaint.setShadowLayer(4f, 0f, 0f, ContextCompat.getColor(context, R.color.white_darker_3X))
        invalidate()
        setOnClickListener {
            isChecked = !isChecked
            invalidate()
            delegate?.onSwitchChanged(this , isChecked)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.save()
        canvas?.drawRoundRect(trackRect, round, round, trackPaint)
        if (isChecked) {
            setToChecking()
            canvas?.drawCircle((width.toFloat() / 2f) + thumbWidth - marginRightThumb, height / 2f, thumbWidth, thumbPaint)
        } else {
            setToUnChecking()
            canvas?.drawCircle(padding + thumbWidth + marginRightThumb, (height / 2f), thumbWidth, thumbPaint)
        }
        canvas?.restore()
    }

    private val trackColor: Int
        get() {
            return ContextCompat.getColor(context, if (isChecked) R.color.red else R.color.white_darker_3X)
        }

    fun setCheckedProgrammatically(checked: Boolean) {
        isChecked = checked
        invalidate()
    }

    fun setOnChangeListener(onSwitchChangeListener: SwitchButton.OnSwitchChangeListener) {
        delegate = onSwitchChangeListener
    }
}