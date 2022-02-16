package ir.amozkade.advancedAsisstiveTouche.helper.customviews

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.databinding.ActionBarBinding
import java.util.*

class ActionBar : ConstraintLayout {

    lateinit var mBinding: ActionBarBinding
    private var title: String? = null
    private var loginButton: Boolean = false
    private var notifButton: Boolean = false
    private var deleteButton: Boolean = false
    private var doneButton: Boolean = false
    private var backButton: Boolean = false
    private var addButton: Boolean = false
    private var customButton: Boolean = false
    private var isMainFont: Boolean = false

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setTypedArrayValues(context.obtainStyledAttributes(attrs, R.styleable.ActionBar))
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setTypedArrayValues(context.obtainStyledAttributes(attrs, R.styleable.ActionBar))
        init()
    }

    private fun setTypedArrayValues(ta: TypedArray) {
        title = ta.getString(R.styleable.ActionBar_action_bar_title)
        doneButton = ta.getBoolean(R.styleable.ActionBar_action_bar_done_button, false)
        backButton = ta.getBoolean(R.styleable.ActionBar_action_bar_back_button, true)
        deleteButton = ta.getBoolean(R.styleable.ActionBar_action_bar_delete_button, false)
        notifButton = ta.getBoolean(R.styleable.ActionBar_action_bar_notification_button, false)
        loginButton = ta.getBoolean(R.styleable.ActionBar_action_bar_login_button, false)
        addButton = ta.getBoolean(R.styleable.ActionBar_action_bar_add_button, false)
        customButton = ta.getBoolean(R.styleable.ActionBar_action_bar_custom_button, false)
        isMainFont = ta.getBoolean(R.styleable.ActionBar_action_bar_is_main_font, false)
    }

    fun init() {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.action_bar, this, true)
        mBinding.title.text = title
        mBinding.btnBack.visibility = if (backButton) VISIBLE else GONE
        mBinding.btnDeleteAll.visibility = if (deleteButton) VISIBLE else GONE
        mBinding.btnSubmit.visibility = if (doneButton) VISIBLE else GONE
        mBinding.btnNotification.visibility = if (notifButton) VISIBLE else GONE
        mBinding.btnLogin.visibility = if (loginButton) VISIBLE else GONE
        mBinding.btnAdd.visibility = if (addButton) VISIBLE else GONE
        mBinding.btnCustom.visibility = if (customButton) VISIBLE else GONE
        mBinding.btnBack.setOnClickListener {
            (context as AppCompatActivity).finish()
        }
        if (isMainFont) {
            val isFa = Locale.getDefault().language.contains("fa")
            mBinding.title.typeface = ResourcesCompat.getFont(context, if (isFa) R.font.mostanad else R.font.pacifico)
        }
    }

    fun btnBackSetOnClickListener(function: () -> Unit) {
        mBinding.btnBack.setOnClickListener { function.invoke() }
    }

    fun btnAddSetOnClickListener(function: () -> Unit) {
        mBinding.btnAdd.setOnClickListener { function.invoke() }
    }

    fun btnDeleteSetOnClickListener(function: () -> Unit) {
        mBinding.btnDeleteAll.setOnClickListener { function.invoke() }
    }

    fun btnLoginSetOnClickListener(function: () -> Unit) {
        mBinding.btnLogin.setOnClickListener { function.invoke() }
    }

    fun btnDoneSetOnClickListener(function: () -> Unit) {
        mBinding.btnSubmit.setOnClickListener { function.invoke() }
    }

    fun btnCustomButtonSetOnClickListener(function: () -> Unit) {
        mBinding.btnCustom.setOnClickListener { function.invoke() }
    }

    fun btnNotifSetOnClickListener(function: () -> Unit) {
        mBinding.btnNotification.setOnClickListener { function.invoke() }
    }

    fun setActionBarTitle(title: String) {
        mBinding.title.text = title
    }

}