package ir.amozkade.advancedAsisstiveTouche.mvvm.user.profile.resetPassword

import android.app.Activity
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import dagger.hilt.android.AndroidEntryPoint
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.databinding.ActivityResetPasswordBinding
import ir.amozkade.advancedAsisstiveTouche.helper.api.DataState
import ir.amozkade.advancedAsisstiveTouche.mvvm.BaseActivity
import ir.amozkade.advancedAsisstiveTouche.mvvm.user.profile.resetPassword.utils.ResetPasswordResponse
import ir.amozkade.advancedAsisstiveTouche.mvvm.user.profile.resetPassword.utils.ResetPasswordStateEvent
import java.lang.Exception

@AndroidEntryPoint
class ResetPasswordActivity : BaseActivity() {

    private val viewModel: ResetPasswordViewModel by viewModels()
    private lateinit var mBinding: ActivityResetPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupObservers()
        setupUI()
    }

    private fun setupUI() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_reset_password)
        addLoadingsToContainer(mBinding.root as ViewGroup)
        mBinding.vm = viewModel
        title = getString(R.string.reset_password)
        mBinding.btnEdit.setOnClickListener { viewModel.setState(ResetPasswordStateEvent.ResetPassword) }
    }

    private fun setupObservers() {
        viewModel.exceptionObserver.observe(this) {
            manageDataState(DataState.Error(it as Exception))
        }
        viewModel.response.observe(this) { dataState ->
            if (dataState is DataState.Success) {
                if (dataState.data is ResetPasswordResponse.SuccessReset) {
                    successReset()
                }
            }
            manageDataState(dataState)
        }
    }

    private fun successReset() {
        setResult(Activity.RESULT_OK)
        finish()
    }
}
