package ir.amozkade.advancedAsisstiveTouche.helper.customviews

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.databinding.StepperButtonBinding
import java.util.*

class StepperButton : ConstraintLayout {


    private var maxValue: Int = 100
    private var currentPercent: Int = 40
    lateinit var mBinding: StepperButtonBinding
    private var title: String? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setTypedArrayValues(context.obtainStyledAttributes(attrs, R.styleable.StepperButton))
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setTypedArrayValues(context.obtainStyledAttributes(attrs, R.styleable.StepperButton))
        init()
    }

    private fun setTypedArrayValues(ta: TypedArray) {
        title = ta.getString(R.styleable.StepperButton_stepper_button_title)
        maxValue = ta.getInt(R.styleable.StepperButton_stepper_button_max_value, 100)
        currentPercent = ta.getInt(R.styleable.StepperButton_stepper_button_current_percent, 0)
    }

    var increaseTimer: Timer? = null
    var decreaseTimer: Timer? = null
    private var increaseTimerStarted = false
    private var decreaseTimerStarted = false

    @SuppressLint("ClickableViewAccessibility")
    fun init() {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.stepper_button, this, true)
        mBinding.title.text = title
        mBinding.btnIncrease.setOnClickListener {
            mBinding.stepper.increase()
        }
        mBinding.btnDecrease.setOnClickListener {
            mBinding.stepper.decrease()
        }
        mBinding.btnIncrease.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                increaseTimer = Timer()
                increaseTimer?.schedule(object : TimerTask() {
                    override fun run() {
                        increaseTimerStarted = true
                        mBinding.stepper.increaseOnHold()
                    }
                }, 500, 20)
            } else if (event.action == MotionEvent.ACTION_UP && !increaseTimerStarted) {
                mBinding.stepper.increase()
                mBinding.stepper.updateDelegate()
                increaseTimer?.cancel()
                increaseTimer = null
            } else if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
                increaseTimerStarted = false
                mBinding.stepper.updateDelegate()
                increaseTimer?.cancel()
                increaseTimer = null
            }
            return@setOnTouchListener true
        }

        mBinding.btnDecrease.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                decreaseTimer = Timer()
                decreaseTimer?.schedule(object : TimerTask() {
                    override fun run() {
                        decreaseTimerStarted = true
                        mBinding.stepper.decreaseOnHold()
                    }
                }, 500, 20)

            } else if (event.action == MotionEvent.ACTION_UP && !decreaseTimerStarted) {
                mBinding.stepper.decrease()
                mBinding.stepper.updateDelegate()
                decreaseTimer?.cancel()
                decreaseTimer = null
            } else if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
                decreaseTimerStarted = false
                mBinding.stepper.updateDelegate()
                decreaseTimer?.cancel()
                decreaseTimer = null
            }
            return@setOnTouchListener true
        }
        mBinding.stepper.maxValue = maxValue.toFloat()
        try {
            //try catch need to draw view correctly in render mode xml android studio
            mBinding.stepper.setProgress(currentPercent)
        } catch (e: Exception) {
        }
    }
}

@BindingAdapter("setStepperProgress")
fun setStepperProgress(stepperButton: StepperButton, percent: Int) {
    stepperButton.mBinding.stepper.setProgress(percent)
}

@BindingAdapter("setStepperProgressListener")
fun setStepperProgressListener(stepperButton: StepperButton, delegate: StepperView.StepperDelegate) {
    stepperButton.mBinding.stepper.delegate = delegate
}

@BindingAdapter("setStepperEnable")
fun setStepperEnable(stepperButton: StepperButton, enable: Boolean) {
    stepperButton.mBinding.stepper.isEnabled = enable
}