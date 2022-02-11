package ir.amozkade.advancedAsisstiveTouche.mvvm.user.forgotPassword

import android.app.Activity
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import dagger.hilt.android.AndroidEntryPoint
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.databinding.ActivityForgotPasswordConfirmationBinding
import ir.amozkade.advancedAsisstiveTouche.helper.api.DataState
import ir.amozkade.advancedAsisstiveTouche.mvvm.BaseActivity
import ir.amozkade.advancedAsisstiveTouche.mvvm.user.forgotPassword.utils.ForgotPasswordResponse
import ir.amozkade.advancedAsisstiveTouche.mvvm.user.forgotPassword.utils.ForgotPasswordStateEvent
import java.lang.Exception

@AndroidEntryPoint
class ForgotPasswordConfirmationActivity : BaseActivity() {

    val viewModel:ForgotPasswordViewModel by viewModels()
    private lateinit var mBinding: ActivityForgotPasswordConfirmationBinding
    private lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUI()
        setupObservers()
    }

    private fun setupUI() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_password_confirmation)
        email = intent?.getStringExtra("email") ?: return
        addLoadingsToContainer(mBinding.root as ViewGroup)
        mBinding.btnResetPassword.setOnClickListener {

            val emailCode = if (mBinding.tfEmailCode.editText?.text.isNullOrEmpty()) kotlin.run {
                showWarn(getString(R.string.forgot_password_confirmation), getString(R.string.enter_email_verify_code))
                return@setOnClickListener
            } else mBinding.tfEmailCode.editText?.text!!

            val newPassword = if (mBinding.tfNewPassword.editText?.text.isNullOrEmpty()) kotlin.run {
                showWarn(getString(R.string.forgot_password_confirmation), getString(R.string.enter_new_password))
                return@setOnClickListener
            } else mBinding.tfNewPassword.editText?.text!!

            val repeatPassword = if (mBinding.tfNewPasswordRepeat.editText?.text.isNullOrEmpty()) kotlin.run {
                showWarn(getString(R.string.forgot_password_confirmation), getString(R.string.enter_new_password_repeat))
                return@setOnClickListener
            } else mBinding.tfNewPasswordRepeat.editText?.text!!

            if (newPassword.toString() != repeatPassword.toString()) {
                showWarn(getString(R.string.forgot_password_confirmation), getString(R.string.password_not_match))
                return@setOnClickListener
            }
            viewModel.setState(ForgotPasswordStateEvent.VerifyResetPassword(email, newPassword.toString(), emailCode.toString().toInt()))
        }
    }

    private fun setupObservers() {
        viewModel.exceptionObserver.observe(this) {
            manageDataState(DataState.Error(it as Exception))
        }
        viewModel.response.observe(this) { dataState ->
            if (dataState is DataState.Success) {
                if (dataState.data is ForgotPasswordResponse.VerifyResult) {
                    resetPasswordResult(dataState.data.isVerified)
                }
            }
            manageDataState(dataState)
        }
    }

    private fun resetPasswordResult(verified:Boolean){
        if (verified) {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }
}
