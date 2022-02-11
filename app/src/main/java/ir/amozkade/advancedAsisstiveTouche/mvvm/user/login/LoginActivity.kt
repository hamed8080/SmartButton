package ir.amozkade.advancedAsisstiveTouche.mvvm.user.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import dagger.hilt.android.AndroidEntryPoint
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.databinding.ActivityLoginBinding
import ir.amozkade.advancedAsisstiveTouche.helper.api.DataState
import ir.amozkade.advancedAsisstiveTouche.mvvm.BaseActivity
import ir.amozkade.advancedAsisstiveTouche.mvvm.user.forgotPassword.ForgotPasswordActivity
import ir.amozkade.advancedAsisstiveTouche.mvvm.user.loginWithSms.SMSActivity
import ir.amozkade.advancedAsisstiveTouche.mvvm.user.register.RegisterActivity
import ir.amozkade.advancedAsisstiveTouche.mvvm.user.login.utils.LoginStateEvent
import ir.mobitrain.applicationcore.helper.animations.CommonAnimation
import java.lang.Exception
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : BaseActivity() {

    @Inject
    lateinit var gso: GoogleSignInOptions
    private lateinit var mBinding: ActivityLoginBinding
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private val viewModel by viewModels<LoginViewModel>()

    private val loginWithSMSRequestContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
        if (activityResult.resultCode == RESULT_OK) {
            finish()
        }
    }

    private val registerContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
        if (activityResult.resultCode == RESULT_OK) {
            finish()
        }
    }

    private val googleSignInContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
        if (activityResult.resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(activityResult.data)
            viewModel.handleSignInResult(task)
        }
    }

    private val forgetContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
        if (activityResult.resultCode == RESULT_OK) {
            showWarn(getString(R.string.reset_password), getString(R.string.reset_password_successes))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUI()
        setupObservers()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun setupUI() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        mBinding.vm = viewModel
        mBinding.btnForgotPassword.setOnClickListener { openForgetPassword() }
        mBinding.btnOpenRegister.setOnClickListener { openRegister() }
        mBinding.btnSMSSignIn.setOnClickListener { loginWithSmsTaped() }
        mBinding.btnLogin.setOnClickListener { viewModel.setState(LoginStateEvent.DoLogin) }
        mBinding.btnGoogleSignIn.setOnClickListener { loginWithGoogleTaped() }
        addLoadingsToContainer(mBinding.mainContainer)
        CommonAnimation.doAnimation(mBinding.mainContainer)
    }

    private fun setupObservers() {
        viewModel.exceptionObserver.observe(this) {
            manageDataState(DataState.Error(it as Exception))
        }
        viewModel.response.observe(this) { dataState ->
            manageDataState(dataState)
        }
    }

    private fun openForgetPassword() {
        val intent = Intent(this, ForgotPasswordActivity::class.java)
        forgetContent.launch(intent)
    }

    private fun openRegister() {
        val intent = Intent(this, RegisterActivity::class.java)
        registerContent.launch(intent)
    }

    private fun loginWithSmsTaped() {
        loginWithSMSRequestContent.launch(Intent(cto, SMSActivity::class.java))
    }

    private fun loginWithGoogleTaped() {
        val signInIntent = mGoogleSignInClient.signInIntent
        //signOut to show chose account again to user
        mGoogleSignInClient.signOut()
        googleSignInContent.launch(signInIntent)
    }

}