package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.addOrEditQuestion

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import dagger.hilt.android.AndroidEntryPoint
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.databinding.ActivityAddOrEditQuestionBinding
import ir.amozkade.advancedAsisstiveTouche.helper.api.DataState
import ir.amozkade.advancedAsisstiveTouche.mvvm.BaseActivity
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.addOrEditQuestion.utils.AddOrEditQuestionResponse
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.addOrEditQuestion.utils.AddOrEditQuestionStateEvent
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.levels.questionAnswer.QuestionAnswer
import ir.mobitrain.applicationcore.helper.animations.CommonAnimation
import java.lang.Exception

@AndroidEntryPoint
class AddOrEditQuestionActivity : BaseActivity() {

    private val viewModel by viewModels<AddOrEditQuestionViewModel>()
    private lateinit var mBinding: ActivityAddOrEditQuestionBinding
    private var level: Int = 0
    private var leitnerId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_or_edit_question)
        setupUI()
        setupObservers()
    }

    private fun setupUI() {
        addLoadingsToContainer(mBinding.root as ViewGroup)
        leitnerId = intent.getIntExtra("leitnerId", 0)
        viewModel.leitnerId = leitnerId
        intent.getParcelableExtra<QuestionAnswer>("QuestionAnswer")?.let {
            leitnerId = it.leitnerId
            viewModel.leitnerId = it.leitnerId
            viewModel.getModel().questionAnswer = it
            mBinding.chkManual.isChecked = viewModel.getModel().isManual
            mBinding.tfAnswer.visibility = if ( mBinding.chkManual.isChecked ) View.VISIBLE else View.GONE
        }
        mBinding.actionBar.setActionBarTitle(level.toString())
        mBinding.vm = viewModel
        mBinding.btnSubmit.setOnClickListener {
            viewModel.setState(AddOrEditQuestionStateEvent.Save)
        }
        mBinding.chkManual.setOnCheckedChangeListener { _, isChecked ->
            mBinding.tfAnswer.visibility = if (isChecked) View.VISIBLE else View.GONE
            viewModel.getModel().isManual = isChecked
        }
        CommonAnimation.doAnimation(mBinding.container)
    }

    private fun setupObservers() {
        viewModel.exceptionObserver.observe(this) {
            manageDataState(DataState.Error(it as Exception))
        }

        viewModel.response.observe(this) { dataState ->
            if (dataState is DataState.Success) {
                if (dataState.data is AddOrEditQuestionResponse.SuccessAdded) {
                    finishAndSetIntent(dataState.data.questionAnswer, isNewAdded = true)
                }
                if (dataState.data is AddOrEditQuestionResponse.SuccessEdited) {
                    finishAndSetIntent(dataState.data.questionAnswer, isNewAdded = false)
                }
            }
            manageDataState(dataState)
        }
    }

    private fun finishAndSetIntent(questionAnswer: QuestionAnswer,isNewAdded:Boolean) {
        val intent = Intent()
        intent.putExtra("level", level)
        intent.putExtra("isNewAdded", isNewAdded)
        intent.putExtra("questionAnswer", questionAnswer)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}
