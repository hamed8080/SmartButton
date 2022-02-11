package ir.amozkade.advancedAsisstiveTouche.mvvm.user.profile

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import dagger.hilt.android.AndroidEntryPoint
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.databinding.ActivityProfileBinding
import ir.amozkade.advancedAsisstiveTouche.mvvm.BaseActivity
import ir.amozkade.advancedAsisstiveTouche.mvvm.user.profile.editProfile.EditProfileActivity
import ir.amozkade.advancedAsisstiveTouche.mvvm.user.profile.resetPassword.ResetPasswordActivity
import ir.mobitrain.applicationcore.api.JWT

@AndroidEntryPoint
class ProfileActivity : BaseActivity() {

    private lateinit var mBinding: ActivityProfileBinding
    val viewModel by viewModels<ProfileViewModel>()

    private val editProfileContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
        if (activityResult.resultCode == RESULT_OK) {
            showWarn(getString(R.string.edit_profile), getString(R.string.user_edited_successfully), imageId = R.drawable.img_login)
        }
    }

    private val resetPasswordContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
        if (activityResult.resultCode == RESULT_OK) {
            showWarn(getString(R.string.reset_password), getString(R.string.reset_password_successes), imageId = R.drawable.img_password)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_profile)
        mBinding.vm = viewModel
        mBinding.btnEditProfile.setOnClickListener { openEditProfileActivity() }
        mBinding.btnResetProfile.setOnClickListener { openResetPasswordActivity() }
        mBinding.btnExit.setOnClickListener { exitProfile() }
    }

    private fun openResetPasswordActivity() {
        val intent = Intent(this, ResetPasswordActivity::class.java)
        resetPasswordContent.launch(intent)
    }

    private fun openEditProfileActivity() {
        val intent = Intent(this, EditProfileActivity::class.java)
        editProfileContent.launch(intent)
    }

    private fun exitProfile() {
        viewModel.setState(ir.amozkade.advancedAsisstiveTouche.mvvm.user.profile.utils.EditProfileStateEvent.ClearSyncFirebaseToken)
        JWT.instance.clearJwt()
        viewModel.setState(ir.amozkade.advancedAsisstiveTouche.mvvm.user.profile.utils.EditProfileStateEvent.ClearProfile)
        finish()
    }
}