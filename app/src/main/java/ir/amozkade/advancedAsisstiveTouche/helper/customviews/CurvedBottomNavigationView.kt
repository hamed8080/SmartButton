package ir.amozkade.advancedAsisstiveTouche.helper.customviews

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import ir.amozkade.advancedAsisstiveTouche.R
import ir.mobitrain.applicationcore.helper.Converters

class CurvedBottomNavigationView : BottomNavigationView {

    private lateinit var mPath: Path
    private lateinit var mPaint: Paint
    private lateinit var shader: Shader
    private var colorStart: Int = 0
    private var colorEnd: Int = 0
    private val shadowHeight:Float = 5F

    /** the radius represent the radius of the fab button  */
    private val radius = Converters.convertIntToDP(24 , context).toInt()
    // the coordinates of the first curve
    private var mFirstCurveStartPoint = Point()
    private var mFirstCurveEndPoint = Point()
    private var mFirstCurveControlPoint2 = Point()
    private var mFirstCurveControlPoint1 = Point()

    //the coordinates of the second curve
    private var mSecondCurveStartPoint = Point()
    private var mSecondCurveEndPoint = Point()
    private var mSecondCurveControlPoint1 = Point()
    private var mSecondCurveControlPoint2 = Point()
    private var mNavigationBarWidth: Int = 0
    private var mNavigationBarHeight: Int = 0

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
        mPath = Path()
        mPaint = Paint()
        mPaint.style = Paint.Style.FILL_AND_STROKE
        mPaint.isAntiAlias = true
        setBackgroundColor(Color.TRANSPARENT)
        colorStart = this.resources.getIntArray(R.array.blueGradient)[0]
        colorEnd = this.resources.getIntArray(R.array.blueGradient)[2]
        mPaint.setShadowLayer(shadowHeight,0F,0F,ContextCompat.getColor(this.context,R.color.primary_color))
        itemBackground = null
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        mNavigationBarWidth = width
        mNavigationBarHeight = height

        mFirstCurveStartPoint.set(mNavigationBarWidth / 2
                - radius * 2
                - radius / 3, 0)

        mFirstCurveEndPoint.set(mNavigationBarWidth / 2, (height / 6) * 4)
        mSecondCurveStartPoint = mFirstCurveEndPoint

        mSecondCurveEndPoint.set(mNavigationBarWidth / 2
                + radius * 2
                + radius / 3, 0)

        mFirstCurveControlPoint1.set(mFirstCurveStartPoint.x
                + radius + radius / 4,
                mFirstCurveStartPoint.y)

        mFirstCurveControlPoint2.set(mFirstCurveEndPoint.x - radius * 2 + radius,
                mFirstCurveEndPoint.y)

        mSecondCurveControlPoint1.set(mSecondCurveStartPoint.x + radius * 2 - radius,
                mSecondCurveStartPoint.y)
        mSecondCurveControlPoint2.set(mSecondCurveEndPoint.x - (radius + radius / 4),
                mSecondCurveEndPoint.y)

        shader = LinearGradient(0F, 0F, width.toFloat(), height.toFloat(), colorStart, colorEnd, Shader.TileMode.MIRROR)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        mPath.reset()
        mPath.moveTo(0f, shadowHeight)
        mPath.lineTo(mFirstCurveStartPoint.x.toFloat(), shadowHeight)

        mPath.cubicTo(mFirstCurveControlPoint1.x.toFloat(), mFirstCurveControlPoint1.y.toFloat(),
                mFirstCurveControlPoint2.x.toFloat(), mFirstCurveControlPoint2.y.toFloat(),
                mFirstCurveEndPoint.x.toFloat(), mFirstCurveEndPoint.y.toFloat()+ 8)

        mPath.cubicTo(mSecondCurveControlPoint1.x.toFloat(), mSecondCurveControlPoint1.y.toFloat(),
                mSecondCurveControlPoint2.x.toFloat(), mSecondCurveControlPoint2.y.toFloat(),
                mSecondCurveEndPoint.x.toFloat(), shadowHeight)

        mPath.lineTo(mNavigationBarWidth.toFloat(), shadowHeight)
        mPath.lineTo(mNavigationBarWidth.toFloat(), mNavigationBarHeight.toFloat())
        mPath.lineTo(0f, mNavigationBarHeight.toFloat())
        mPath.close()

        mPaint.shader = shader
        canvas.drawPath(mPath, mPaint)

    }
}
