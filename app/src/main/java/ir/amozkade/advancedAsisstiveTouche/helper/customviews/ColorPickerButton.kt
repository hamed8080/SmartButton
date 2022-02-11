package ir.amozkade.advancedAsisstiveTouche.helper.customviews

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.databinding.ColorPickerButtonBinding

class ColorPickerButton : ConstraintLayout {

    lateinit var mBinding: ColorPickerButtonBinding
    private var title: String? = null
    private var subTitle: String? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setTypedArrayValues(context.obtainStyledAttributes(attrs, R.styleable.ColorPickerButton))
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setTypedArrayValues(context.obtainStyledAttributes(attrs, R.styleable.ColorPickerButton))
        init()
    }

    private fun setTypedArrayValues(ta: TypedArray) {
        title = ta.getString(R.styleable.ColorPickerButton_color_picker_title)
        subTitle = ta.getString(R.styleable.ColorPickerButton_color_picker_sub_title)
    }

    fun init() {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.color_picker_button, this, true)
        mBinding.title.text = title
        mBinding.subTitle.text = subTitle
    }

}

@BindingAdapter("setButtonColorSelectedColor")
fun setButtonColorSelectedColor(colorPickerButton: ColorPickerButton, color: Int) {
    colorPickerButton.mBinding.selectedColorCircle.selectedColor = color
}