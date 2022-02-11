package ir.amozkade.advancedAsisstiveTouche.mvvm.user.register

import android.app.Activity
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import dagger.hilt.android.AndroidEntryPoint
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.databinding.ActivityRegisterBinding
import ir.amozkade.advancedAsisstiveTouche.helper.api.DataState
import ir.amozkade.advancedAsisstiveTouche.mvvm.BaseActivity
import ir.amozkade.advancedAsisstiveTouche.mvvm.user.register.utils.RegisterResponse
import ir.amozkade.advancedAsisstiveTouche.mvvm.user.register.utils.RegisterStateEvent
import java.lang.Exception

@AndroidEntryPoint
class RegisterActivity : BaseActivity() {

    private lateinit var mBinding: ActivityRegisterBinding
    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUI()
        setupObservers()
    }

    private fun setupUI() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_register)
        addLoadingsToContainer(mBinding.root as ViewGroup)
        mBinding.vm = viewModel
        mBinding.btnRegister.setOnClickListener { viewModel.setState(RegisterStateEvent.Register) }
    }

    private fun setupObservers() {
        viewModel.exceptionObserver.observe(this) {
            manageDataState(DataState.Error(it as Exception))
        }

        viewModel.response.observe(this) { dataState ->
            if (dataState is DataState.Success) {
                if (dataState.data is RegisterResponse.Registered) {
                    registered()
                }
            }
            manageDataState(dataState)
        }
    }

    private fun registered() {
        setResult(Activity.RESULT_OK)
        finish()
    }
}