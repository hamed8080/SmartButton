package ir.amozkade.advancedAsisstiveTouche.mvvm.user.profile.editProfile

import android.app.Activity
import android.content.Intent
import android.graphics.ImageDecoder
import android.graphics.ImageDecoder.decodeBitmap
import android.os.Build
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.databinding.ActivityEditProfileBinding
import ir.amozkade.advancedAsisstiveTouche.helper.bindings.ImageViewBinding
import ir.amozkade.advancedAsisstiveTouche.mvvm.BaseActivity
import android.provider.MediaStore.Images.Media.getBitmap
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import ir.amozkade.advancedAsisstiveTouche.helper.api.DataState
import ir.amozkade.advancedAsisstiveTouche.mvvm.user.profile.ProfileRepository
import ir.amozkade.advancedAsisstiveTouche.mvvm.user.profile.editProfile.utils.EditProfileResponse
import ir.amozkade.advancedAsisstiveTouche.mvvm.user.profile.editProfile.utils.EditProfileStateEvent
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import javax.inject.Inject

@AndroidEntryPoint
class EditProfileActivity : BaseActivity() {

    private val viewModel: EditProfileViewModel by viewModels()
    private lateinit var mBinding: ActivityEditProfileBinding


    private val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
        if (activityResult.resultCode == RESULT_OK) {
            activityResult?.data?.data?.run {
                @Suppress("DEPRECATION")
                val bitmap = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                    decodeBitmap(ImageDecoder.createSource(contentResolver, this))
                } else {
                    getBitmap(contentResolver, this)
                }
                viewModel.setState(EditProfileStateEvent.UploadImage(bitmap))
            }
        }
    }

    @Inject
    lateinit var profileRepository: ProfileRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile)
        addLoadingsToContainer(mBinding.container as ViewGroup)
        mBinding.vm = viewModel
        setupObservers()
        setupUI()
    }

    private fun setupUI() {
        mBinding.imgProfile.setOnClickListener { openImagePicker() }
        mBinding.btnEdit.setOnClickListener { viewModel.setState(EditProfileStateEvent.EditProfile) }
        lifecycleScope.launch(IO) {
            profileRepository.getProfile()?.let { profile ->
                withContext(Main) {
                    viewModel.getModel().firstName = profile.firstName
                    viewModel.getModel().lastName = profile.lastName
                    viewModel.getModel().phone = profile.phone
                    downloadProfileImage(profile.img)
                }
            }
        }
    }

    private fun setupObservers() {
        viewModel.exceptionObserver.observe(this) {
            manageDataState(DataState.Error(it as Exception))
        }
        viewModel.response.observe(this) { dataState ->
            if (dataState is DataState.Success) {
                if (dataState.data is EditProfileResponse.SuccessEdited) {
                    successEditedProfile()
                }

                if (dataState.data is EditProfileResponse.SuccessUploadedImage) {
                    successUploadedImageProfile()
                }
            }
            manageDataState(dataState)
        }
    }

    private fun successEditedProfile() {
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun successUploadedImageProfile() {
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun openImagePicker() {
        val intent = Intent()
        intent.type = "image/*"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.action = Intent.ACTION_GET_CONTENT
        getContent.launch(intent)
    }

    private fun downloadProfileImage(imageUrl: String?) {
        if (imageUrl != null) {
            ImageViewBinding.downloadProfileImage(mBinding.imgProfile, imageUrl)
        } else {
            mBinding.imgProfile.setImageResource(R.drawable.img_avatar)
        }
    }

}
