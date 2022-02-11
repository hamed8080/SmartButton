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
import ir.amozkade.advancedAsisstiveTouche.databinding.SwitchButtonBinding

class SwitchButton : ConstraintLayout {

    interface OnSwitchChangeListener {
        fun onSwitchChanged(customSwitch: CustomSwitch, isChecked: Boolean)
    }

    lateinit var mBinding: SwitchButtonBinding
    private var titleColor: Int = ContextCompat.getColor(context, R.color.white_darker_3X)
    private var title: String? = null
    private var subTitle: String? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setTypedArrayValues(context.obtainStyledAttributes(attrs, R.styleable.SwitchButton))
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setTypedArrayValues(context.obtainStyledAttributes(attrs, R.styleable.SwitchButton))
        init()
    }

    private fun setTypedArrayValues(ta: TypedArray) {
        titleColor = ta.getColor(R.styleable.SwitchButton_switch_button_title_color, ContextCompat.getColor(context, R.color.white_darker_3X))
        title = ta.getString(R.styleable.SwitchButton_switch_button_title)
        subTitle = ta.getString(R.styleable.SwitchButton_switch_button_sub_title)
    }

    fun init() {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.switch_button, this, true)
        mBinding.title.text = title
        mBinding.subTitle.text = subTitle
    }

    fun setSwitch(enable: Boolean) {
        mBinding.sw.setCheckedProgrammatically(enable)
    }
}

@BindingAdapter("setSwitchButtonChecked")
fun setSwitchButtonChecked(switchButton: SwitchButton, checked: Boolean) {
    switchButton.mBinding.sw.setCheckedProgrammatically(checked)
}

@BindingAdapter("switchChangeListener")
fun switchChangeListener(switchButton: SwitchButton, onSwitchChangeListener: SwitchButton.OnSwitchChangeListener) {
    switchButton.mBinding.sw.setOnChangeListener(onSwitchChangeListener)
}

@BindingAdapter("setSwitchEnabled")
fun setSwitchEnabled(switchButton: SwitchButton, enable: Boolean) {
    switchButton.mBinding.sw.isEnabled = enable
}