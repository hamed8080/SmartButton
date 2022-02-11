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
import ir.amozkade.advancedAsisstiveTouche.databinding.LongButtonWithTitleSubtitleBinding

class LongButtonWithTitleAndSubtitle : ConstraintLayout {


    lateinit var mBinding: LongButtonWithTitleSubtitleBinding
    private var titleColor: Int = ContextCompat.getColor(context, R.color.white_darker_3X)
    private var subTitleColor: Int = ContextCompat.getColor(context, R.color.white_darker_2X)
    private var icon = 0
    private var title: String? = null
    private var subTitle: String? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setTypedArrayValues(context.obtainStyledAttributes(attrs, R.styleable.LongButtonWithTitleAndSubtitle))
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setTypedArrayValues(context.obtainStyledAttributes(attrs, R.styleable.LongButtonWithTitleAndSubtitle))
        init()
    }

    private fun setTypedArrayValues(ta: TypedArray) {
        titleColor = ta.getColor(R.styleable.LongButtonWithTitleAndSubtitle_title_color, ContextCompat.getColor(context, R.color.white_darker_3X))
        subTitleColor = ta.getColor(R.styleable.LongButtonWithTitleAndSubtitle_sub_title_color, ContextCompat.getColor(context, R.color.white_darker_2X))
        icon = ta.getResourceId(R.styleable.LongButtonWithTitleAndSubtitle_icon, -1)
        title = ta.getString(R.styleable.LongButtonWithTitleAndSubtitle_title)
        subTitle = ta.getString(R.styleable.LongButtonWithTitleAndSubtitle_sub_title)
    }

    fun init() {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.long_button_with_title_subtitle, this, true)
        if (icon != -1) {
            mBinding.img.setImageResource(icon)
        }
        mBinding.title.text = title
        mBinding.subTitle.text = subTitle
    }

    companion object {

        @BindingAdapter("longButtonTitleString" , "longButtonIconResourceId")
        @JvmStatic
        fun setTitleAndIcon(longButtonWithTitleAndSubtitle: LongButtonWithTitleAndSubtitle,longButtonTitleString:String, longButtonIconResourceId: Int){
            longButtonWithTitleAndSubtitle.mBinding.title.text = longButtonTitleString
            longButtonWithTitleAndSubtitle.mBinding.img.setImageResource(longButtonIconResourceId)
        }
    }
}