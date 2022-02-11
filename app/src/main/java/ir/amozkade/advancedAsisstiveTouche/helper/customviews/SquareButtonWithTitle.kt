package ir.amozkade.advancedAsisstiveTouche.helper.customviews

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.databinding.SquareButtonWithTitleBinding

class SquareButtonWithTitle : ConstraintLayout {


    private lateinit var mBinding: SquareButtonWithTitleBinding
    private var titleColor: Int = ContextCompat.getColor(context, R.color.white_darker_3X)
    private var icon = 0
    private var title: String? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setTypedArrayValues(context.obtainStyledAttributes(attrs, R.styleable.SquareButtonWithTitle))
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setTypedArrayValues(context.obtainStyledAttributes(attrs, R.styleable.SquareButtonWithTitle))
        init()
    }

    private fun setTypedArrayValues(ta: TypedArray) {
        titleColor = ta.getColor(R.styleable.SquareButtonWithTitle_square_title_color, ContextCompat.getColor(context, R.color.white_darker_3X))
        icon = ta.getResourceId(R.styleable.SquareButtonWithTitle_square_icon, -1)
        title  = ta.getString(R.styleable.SquareButtonWithTitle_square_title)
    }

    fun init() {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.square_button_with_title, this, true)
        if (icon != -1) {
            mBinding.img.setImageResource(icon)
        }
        mBinding.title.text = title
    }
}