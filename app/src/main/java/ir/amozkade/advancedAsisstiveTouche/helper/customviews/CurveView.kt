package ir.amozkade.advancedAsisstiveTouche.helper.customviews

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import ir.amozkade.advancedAsisstiveTouche.R


class CurveView : View {

    var paint: Paint = Paint()
    var path: Path = Path()
    private var curveHeight: Float = 128F


    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(context)
    }

    fun init(context: Context) {
        paint.isAntiAlias = true
        paint.style = Paint.Style.FILL_AND_STROKE
        paint.color = ContextCompat.getColor(context, R.color.white)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val height = height.toFloat()
        val width = width.toFloat()
        path.moveTo(0F, curveHeight)
        path.quadTo(width / 2, 0F, width, curveHeight)
        path.lineTo(width, height)
        path.lineTo(0F, height)
        path.close()
        canvas?.drawPath(path, paint)
    }
}