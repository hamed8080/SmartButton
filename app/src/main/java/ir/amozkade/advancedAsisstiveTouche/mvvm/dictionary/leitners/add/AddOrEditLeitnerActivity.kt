package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.leitners.add

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import dagger.hilt.android.AndroidEntryPoint
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.helper.api.DataState
import ir.amozkade.advancedAsisstiveTouche.mvvm.BaseActivity
import java.lang.Exception
import ir.amozkade.advancedAsisstiveTouche.databinding.ActivityAddOrEditLeitnerBinding
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.leitners.add.utils.AddOrEditLeitnerResponse
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.leitners.add.utils.LeitnerAddOrEditStateEvent

@AndroidEntryPoint
class AddOrEditLeitnerActivity : BaseActivity() {

    private val viewModel: AddOrEditLeitnerViewModel by viewModels()
    private lateinit var mBinding: ActivityAddOrEditLeitnerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_or_edit_leitner)
        setupUI()
        setupObservers()
    }

    private fun setupUI() {
        addLoadingsToContainer(mBinding.root as ViewGroup)
        mBinding.vm = viewModel
        mBinding.tfLeitnerName.editText?.setText(viewModel.getModel().name)
        viewModel.setLeitner(intent.getParcelableExtra("Leitner"))
        mBinding.actionBar.setActionBarTitle(viewModel.getLeitner().name)
        mBinding.btnSubmit.setOnClickListener {
            viewModel.setState(LeitnerAddOrEditStateEvent.CheckDuplicate)
        }
    }

    private fun setupObservers() {
        viewModel.exceptionObserver.observe(this) {
            manageDataState(DataState.Error(it as Exception))
        }

        viewModel.response.observe(this) { state ->
            if (state is DataState.Success && state.data is AddOrEditLeitnerResponse.CheckedDuplicateResult) {
                setIntentBack()
            }
        }
    }

    private fun setIntentBack() {
        val intent = Intent()
        intent.putExtra("Leitner", viewModel.getLeitner())
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}
