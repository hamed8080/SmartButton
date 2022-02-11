package ir.amozkade.advancedAsisstiveTouche.helper.customviews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import ir.amozkade.advancedAsisstiveTouche.R

class CircleColor : View {

    private lateinit var paint: Paint
    var selectedColor :Int = ContextCompat.getColor(context , R.color.white)
    set(value) {
        field = value
        invalidate()
    }

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        paint = Paint()
        paint.isAntiAlias = true
        paint.strokeWidth  = 1f
        paint.style = Paint.Style.FILL
        paint.color = selectedColor
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.save()
        paint.style = Paint.Style.FILL
        paint.color = selectedColor
        canvas?.drawCircle(width / 2f , height/2f , height / 2f, paint)

        paint.style = Paint.Style.STROKE
        paint.color = ContextCompat.getColor(context , R.color.white_darker_3X)
        canvas?.drawCircle(width / 2f , height/2f , height / 2f, paint)
        canvas?.restore()
    }
}