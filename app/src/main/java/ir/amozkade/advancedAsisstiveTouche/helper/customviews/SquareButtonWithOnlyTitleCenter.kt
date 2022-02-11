package ir.amozkade.advancedAsisstiveTouche.helper.customviews

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.databinding.SquareButtonWithOnlyTitleCenterBinding

class SquareButtonWithOnlyTitleCenter : ConstraintLayout {


    private lateinit var mBinding: SquareButtonWithOnlyTitleCenterBinding
    private var titleColor: Int = ContextCompat.getColor(context, R.color.white_darker_3X)
    private var title: String? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setTypedArrayValues(context.obtainStyledAttributes(attrs, R.styleable.SquareButtonWithOnlyTitleCenter))
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setTypedArrayValues(context.obtainStyledAttributes(attrs, R.styleable.SquareButtonWithOnlyTitleCenter))
        init()
    }

    private fun setTypedArrayValues(ta: TypedArray) {
        titleColor = ta.getColor(R.styleable.SquareButtonWithOnlyTitleCenter_square_with_titleOnly_title_color, ContextCompat.getColor(context, R.color.blue))
        title  = ta.getString(R.styleable.SquareButtonWithOnlyTitleCenter_square_with_titleOnly_title)
    }

    fun init() {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.square_button_with_only_title_center, this, true)
        mBinding.title.text = title
    }

    fun setTitle(title:String){
        mBinding.title.text = title
    }

}

@BindingAdapter("squareWithOnlyTitle")
fun textInputEdiTextWatcher(squareButtonWithOnlyTitleCenter: SquareButtonWithOnlyTitleCenter, squareWithOnlyTitle:String) {
    squareButtonWithOnlyTitleCenter.setTitle(squareWithOnlyTitle)
}