package ir.amozkade.advancedAsisstiveTouche.helper.customviews

import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.View
import ir.amozkade.advancedAsisstiveTouche.R

class GradientView : View {

    var colorId: Int? = null
    var isUpToBottom:Boolean? = false

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setTypedArrayValues(context.obtainStyledAttributes(attrs, R.styleable.GradientView))
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        setTypedArrayValues(context.obtainStyledAttributes(attrs, R.styleable.GradientView))
        init(context)
    }


    private fun setTypedArrayValues(ta: TypedArray?) {
        colorId = ta?.getResourceId(R.styleable.GradientView_GradientViewColorArrayId, R.array.blueGradient)
        isUpToBottom = ta?.getBoolean(R.styleable.GradientView_GradientViewIsUpToBottom, false)
    }

    fun init(context: Context) {
        if (colorId != null) {
            val colors = context.resources.getIntArray(colorId!!.toInt())
            val gradientDrawable = GradientDrawable(if (isUpToBottom!!) GradientDrawable.Orientation.TOP_BOTTOM else  GradientDrawable.Orientation.RIGHT_LEFT , colors)
            background = gradientDrawable
        }
    }

}