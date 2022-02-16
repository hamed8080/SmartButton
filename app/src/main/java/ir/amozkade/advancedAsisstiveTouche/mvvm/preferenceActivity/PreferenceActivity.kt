package ir.amozkade.advancedAsisstiveTouche.mvvm.preferenceActivity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import dagger.hilt.android.AndroidEntryPoint
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.databinding.ActivityPreferenceBinding
import ir.amozkade.advancedAsisstiveTouche.mvvm.BaseActivity
import ir.mobitrain.applicationcore.helper.animations.CommonAnimation

@AndroidEntryPoint
class PreferenceActivity :BaseActivity() ,ColorPickerDialogListener{

    lateinit var mBinding: ActivityPreferenceBinding
    private val viewModel: PreferenceViewModel by viewModels()
    private val argsScrollLState = "scrollViewStatePosition"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUI()
        if (savedInstanceState != null){
            mBinding.scrollView.scrollY = savedInstanceState.getInt(argsScrollLState)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(argsScrollLState, mBinding.scrollView.scrollY)
    }

    private fun setupUI() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_preference)
        mBinding.vm = viewModel
        viewModel.initViewModel(this)
        CommonAnimation.doAnimation(mBinding.mainContainer)
    }

    override fun onDialogDismissed(dialogId: Int)  = Unit

    override fun onColorSelected(dialogId: Int, color: Int) {
        when (dialogId) {
            100 -> {
                viewModel.saveSelectedButtonColor(color)
            }
            200 -> {
                viewModel.saveSelectedPanelColor(color)
            }
            300 -> {
                viewModel.saveSelectedPanelButtonsColor(color)
            }
        }
    }

}