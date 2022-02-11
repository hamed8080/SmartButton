package ir.amozkade.advancedAsisstiveTouche.mvvm.user.loginWithSms


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.databinding.ActivityLoginWithSmsBinding
import ir.amozkade.advancedAsisstiveTouche.helper.api.DataState
import ir.amozkade.advancedAsisstiveTouche.mvvm.BaseActivity
import ir.amozkade.advancedAsisstiveTouche.mvvm.user.loginWithSms.utils.LoginWithSMSResponse
import ir.amozkade.advancedAsisstiveTouche.mvvm.user.loginWithSms.utils.LoginWithSMSStateEvent
import java.lang.Exception
import java.util.*

@AndroidEntryPoint
class SMSActivity : BaseActivity() , View.OnKeyListener{

    private lateinit var mBinding: ActivityLoginWithSmsBinding
    private var currentId: Int = R.id.edt1

    private val viewModel:LoginWithSMSViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUI()
        setupObservers()
    }

    private fun setupUI() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login_with_sms)
        addLoadingsToContainer(mBinding.root as ViewGroup)
        mBinding.btnRequestVerificationCode.setOnClickListener {  tapedRequestVerificationCode() }
        mBinding.btnSendVerificationCode.setOnClickListener { tapedSendVerificationCode() }

        mBinding.edt1.addTextChangedListener(object : CustomTextWatcher(mBinding.edt1.id, mBinding.edt2.id) {})
        mBinding.edt2.addTextChangedListener(object : CustomTextWatcher(mBinding.edt1.id, mBinding.edt3.id) {})
        mBinding.edt3.addTextChangedListener(object : CustomTextWatcher(mBinding.edt2.id, mBinding.edt4.id) {})
        mBinding.edt4.addTextChangedListener(object : CustomTextWatcher(mBinding.edt3.id, mBinding.edt4.id) {})

        mBinding.edt4.setOnKeyListener(this)
        mBinding.edt3.setOnKeyListener(this)
        mBinding.edt2.setOnKeyListener(this)

        mBinding.tfPhoneNumber.editText?.requestFocus()
        showKeyboard()
    }

    private fun setupObservers() {
        viewModel.exceptionObserver.observe(this) {
            manageDataState(DataState.Error(it as Exception))
        }

        viewModel.response.observe(this) { dataState ->
            if (dataState is DataState.Success) {
                if (dataState.data is LoginWithSMSResponse.RequestedVerificationCode) {
                    animateToVerificationCode()
                }
                if (dataState.data is LoginWithSMSResponse.VerifiedSMSCode) {
                    successVerifyAndLoggedIn()
                }
                if (dataState.data is LoginWithSMSResponse.FailedVerifySMSCode) {
                    failedRequest()
                }
            }
            manageDataState(dataState)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun tapedRequestVerificationCode() {
        val phoneNumber =  mBinding.tfPhoneNumber.editText?.text
        mBinding.txtPhoneNumber.text = phoneNumber
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return
        }
        closeKeyboard()
        viewModel.setState(LoginWithSMSStateEvent.RequestVerificationCode(phoneNumber.toString()))
    }

    private fun tapedSendVerificationCode() {
        val phoneNumber =  mBinding.tfPhoneNumber.editText?.text
        val vrfCode = mBinding.edt1.text.toString() + mBinding.edt2.text.toString() + mBinding.edt3.text.toString() + mBinding.edt4.text.toString()
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return
        }
        closeKeyboard()
        viewModel.setState(LoginWithSMSStateEvent.VerifyCode(phoneNumber.toString() , vrfCode))
    }

    private fun successVerifyAndLoggedIn() {
        closeKeyboard()
        setResult(Activity.RESULT_OK)
        finish()
    }

    var seconds = 5 * 60
    val timer = Timer()
    private fun animateToVerificationCode() {

        val animateToShow = AnimationUtils.loadAnimation(this, R.anim.alpha_show)
        animateToShow.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) = Unit
            override fun onAnimationStart(animation: Animation?) = Unit
            override fun onAnimationEnd(animation: Animation?) {
                mBinding.verificationContainer.visibility = View.VISIBLE
            }
        })
        mBinding.verificationContainer.startAnimation(animateToShow)
        showKeyboard()
        mBinding.edt1.requestFocus()
        val animateToHide = AnimationUtils.loadAnimation(this, R.anim.alpha_hide)
        animateToHide.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) = Unit
            override fun onAnimationStart(animation: Animation?) = Unit
            override fun onAnimationEnd(animation: Animation?) {
                mBinding.sendContainer.visibility = View.GONE
                mBinding.edt1.requestFocus()
            }
        })
        mBinding.sendContainer.startAnimation(animateToHide)

        timer.schedule(object : TimerTask() {
            @SuppressLint("SetTextI18n")
            override fun run() {
                if (seconds == 0) {
                    timer.cancel()
                    runOnUiThread {
                        animateToHideVerification()
                    }
                    return
                }
                seconds -= 1
                val minute = if ((seconds / 60) < 10) "0${seconds / 60}" else "${seconds / 60}"
                val second = if ((seconds % 60) < 10) "0${seconds % 60}" else "${seconds % 60}"
                runOnUiThread {
                    mBinding.txtSmsTitle.text = "${getString(R.string.remaining_time)} $minute:$second"
                }
            }

        }, 0, 1000)
    }

    private fun animateToHideVerification() {
        mBinding.verificationContainer.visibility = View.GONE
        val animation = AnimationUtils.loadAnimation(this, R.anim.alpha_show)
        mBinding.txtSmsTitle.text = getString(R.string.enter_phone_number)
        mBinding.sendContainer.visibility = View.VISIBLE
        mBinding.sendContainer.startAnimation(animation)
    }

    private fun failedRequest() {
        hideLoading()
        showKeyboard()
        mBinding.edt4.requestFocus()
    }

    private fun showKeyboard() {
        val inputMethodManager: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(mBinding.root.windowToken, 0)
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    private fun closeKeyboard() {
        val inputMethodManager: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }

    open inner class CustomTextWatcher(private val prevId: Int, private val nextId: Int) : TextWatcher {

        override fun afterTextChanged(s: Editable?) {
            if (s != null && s.length == 1) {
                currentId = nextId
                (mBinding.root.findViewById(nextId) as TextInputEditText).requestFocus()
                if (mBinding.edt4.id == nextId && mBinding.edt4.text != null && mBinding.edt4.text.toString() != "") {
                    tapedSendVerificationCode()
                }
            } else {
                currentId = prevId
                (mBinding.root.findViewById(prevId) as TextInputEditText).requestFocus()
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    }

    override fun onKey(p0: View?, keyCode: Int, p2: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_DEL) {
            if (currentId == mBinding.edt4.id && mBinding.edt4.text.toString() == "") {
                mBinding.edt3.requestFocus()
            }
            if (currentId == mBinding.edt3.id && mBinding.edt3.text.toString() == "") {
                mBinding.edt2.requestFocus()
            }
            if (currentId == mBinding.edt2.id && mBinding.edt2.text.toString() == "") {
                mBinding.edt1.requestFocus()
            }
        }
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
    }

}