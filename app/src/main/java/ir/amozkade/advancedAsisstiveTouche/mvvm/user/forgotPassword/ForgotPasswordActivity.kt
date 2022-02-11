package ir.amozkade.advancedAsisstiveTouche.mvvm.user.forgotPassword

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import dagger.hilt.android.AndroidEntryPoint
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.databinding.ActivityForgotPasswordBinding
import ir.amozkade.advancedAsisstiveTouche.helper.api.DataState
import ir.amozkade.advancedAsisstiveTouche.mvvm.BaseActivity
import ir.amozkade.advancedAsisstiveTouche.mvvm.user.forgotPassword.utils.ForgotPasswordResponse
import ir.amozkade.advancedAsisstiveTouche.mvvm.user.forgotPassword.utils.ForgotPasswordStateEvent
import java.lang.Exception

@AndroidEntryPoint
class ForgotPasswordActivity : BaseActivity() {

    val viewModel:ForgotPasswordViewModel by viewModels()
    private lateinit var mBinding: ActivityForgotPasswordBinding

    private val forgotPasswordContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
        if (activityResult.resultCode == RESULT_OK) {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUI()
        setupObservers()
    }

    private fun setupUI() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_password)
        mBinding.actionBar.setActionBarTitle(getString(R.string.forgotPassword))
        addLoadingsToContainer(mBinding.root as ViewGroup)
        mBinding.btnRequestResetCode.setOnClickListener {

            if (mBinding.tfEmailAddress.editText?.text.isNullOrEmpty()) {
                showWarn( getString(R.string.forgotPassword), getString(R.string.enterEmail))
                return@setOnClickListener
            }
            mBinding.tfEmailAddress.editText?.text?.toString()?.let {email->
                viewModel.setState(ForgotPasswordStateEvent.RequestReset(email))
            }
        }
    }

    private fun setupObservers() {
        viewModel.exceptionObserver.observe(this) {
            manageDataState(DataState.Error(it as Exception))
        }
        viewModel.response.observe(this) { dataState ->
            if (dataState is DataState.Success) {
                if (dataState.data is ForgotPasswordResponse.SendRequestSuccessFully) {
                    requestResult(dataState.data.sendRequestSuccessFully)
                }
            }
            manageDataState(dataState)
        }
    }

    private fun requestResult(sendRequestSuccessFully:Boolean){
        if (sendRequestSuccessFully){
            val intent = Intent(this , ForgotPasswordConfirmationActivity::class.java)
            intent.putExtra("email" , mBinding.tfEmailAddress.editText?.text.toString())
            forgotPasswordContent.launch(intent)
        }else{
            showWarn( getString(R.string.forgotPassword) , getString(R.string.email_not_valid))
        }
    }
}
