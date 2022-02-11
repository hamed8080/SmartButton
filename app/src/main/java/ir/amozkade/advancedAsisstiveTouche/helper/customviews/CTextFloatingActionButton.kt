package ir.amozkade.advancedAsisstiveTouche.helper.customviews

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.util.AttributeSet
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ir.amozkade.advancedAsisstiveTouche.R
import ir.mobitrain.applicationcore.helper.Converters.Companion.convertIntToSP

class CTextFloatingActionButton : FloatingActionButton {

    private var fontId: Int = R.font.sf_pro_rounded_medium
    private var paint: Paint = Paint()
    private var text: String = ""
    private var textColor: Int = Color.WHITE
    private var textSize:Float = convertIntToSP(16, context)

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setTypedArrayValues(context.obtainStyledAttributes(attrs, R.styleable.CTextFloatingActionButton))
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setTypedArrayValues(context.obtainStyledAttributes(attrs, R.styleable.CTextFloatingActionButton))
        init()
    }

    private fun setTypedArrayValues(ta: TypedArray) {
        text = ta.getString(R.styleable.CTextFloatingActionButton_text) ?: ""
        textColor = ta.getColor(R.styleable.CTextFloatingActionButton_text_color, Color.WHITE)
        textSize =  ta.getDimension(R.styleable.CTextFloatingActionButton_text_size, 16f)
        fontId = ta.getResourceId(R.styleable.CTextFloatingActionButton_font_id , R.font.sf_pro_rounded_medium)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            paint.typeface = context.resources.getFont(R.font.app_core_icons)
        } else {
            paint.typeface = ResourcesCompat.getFont(context, R.font.app_core_icons)
        }
        paint.isAntiAlias = true
    }

    private fun init() {
        paint.color = textColor
        paint.textSize = textSize
        paint.textAlign =  Paint.Align.CENTER
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.save()
        canvas?.drawText(text, (width / 2f), height / 2f + (textSize / 2.5f), paint)
        canvas?.restore()
    }
}