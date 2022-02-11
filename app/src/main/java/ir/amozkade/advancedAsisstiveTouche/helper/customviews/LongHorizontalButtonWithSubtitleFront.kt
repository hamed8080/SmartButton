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
import ir.amozkade.advancedAsisstiveTouche.databinding.LongHorizontalButtonWithSubtitleFrontBinding

class LongHorizontalButtonWithSubtitleFront : ConstraintLayout {


    lateinit var mBinding: LongHorizontalButtonWithSubtitleFrontBinding
    private var titleColor: Int = ContextCompat.getColor(context, R.color.white_darker_3X)
    private var subTitleColor: Int = ContextCompat.getColor(context, R.color.white_darker_2X)
    private var icon = 0
    private var title: String? = null
    private var subTitle: String? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setTypedArrayValues(context.obtainStyledAttributes(attrs, R.styleable.LongHorizontalButtonWithSubtitleFront))
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setTypedArrayValues(context.obtainStyledAttributes(attrs, R.styleable.LongHorizontalButtonWithSubtitleFront))
        init()
    }

    private fun setTypedArrayValues(ta: TypedArray) {
        titleColor = ta.getColor(R.styleable.LongHorizontalButtonWithSubtitleFront_long_horizontal_title_color, ContextCompat.getColor(context, R.color.white_darker_3X))
        subTitleColor = ta.getColor(R.styleable.LongHorizontalButtonWithSubtitleFront_long_horizontal_sub_title_color, ContextCompat.getColor(context, R.color.white_darker_2X))
        icon = ta.getResourceId(R.styleable.LongHorizontalButtonWithSubtitleFront_long_horizontal_icon, -1)
        title = ta.getString(R.styleable.LongHorizontalButtonWithSubtitleFront_long_horizontal_title)
        subTitle = ta.getString(R.styleable.LongHorizontalButtonWithSubtitleFront_long_horizontal_sub_title)
    }

    fun init() {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.long_horizontal_button_with_subtitle_front, this, true)
        if (icon != -1) {
            mBinding.img.setImageResource(icon)
        }
        mBinding.title.text = title
        mBinding.subTitle.text = subTitle
        mBinding.title.setTextColor(titleColor)
    }
}

@BindingAdapter("setLongHorizontalSubTitle")
fun setLongHorizontalSubTitle(longHorizontalButtonWithSubtitleFrontBinding: LongHorizontalButtonWithSubtitleFront, subTitle: String?) {
    longHorizontalButtonWithSubtitleFrontBinding.mBinding.subTitle.text = subTitle
}


@BindingAdapter("setLongHorizontalTitle")
fun setLongHorizontalTitle(longHorizontalButtonWithSubtitleFrontBinding: LongHorizontalButtonWithSubtitleFront, title: String?) {
    longHorizontalButtonWithSubtitleFrontBinding.mBinding.title.text = title
}