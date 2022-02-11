package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.reverseReview

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.databinding.ActivityReverseReviewBinding
import ir.amozkade.advancedAsisstiveTouche.helper.api.DataState
import ir.amozkade.advancedAsisstiveTouche.mvvm.BaseActivity
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.reverseReview.utils.ReverseReviewStateEvent
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.review.MeanAdapter
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.review.ReviewDelegate
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.review.utils.ReviewResponse
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.translator.TranslateResult
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.wordWebView.WordWebViewActivity
import ir.mobitrain.applicationcore.alertDialog.AlertDialogDelegate
import ir.mobitrain.applicationcore.alertDialog.CustomAlertDialog
import java.lang.Exception

@AndroidEntryPoint
class ReverseReviewActivity : BaseActivity(), ReviewDelegate {

    private val viewModel: ReverseReviewViewModel by viewModels()
    private lateinit var mBinding: ActivityReverseReviewBinding
    private var leitnerId: Int = 0
    private var level: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_reverse_review)
        setupUI(savedInstanceState == null)
        setupObservers()
    }

    private fun setupUI(saveInstanceIsNull: Boolean) {
        addLoadingsToContainer(mBinding.root as ViewGroup)
        leitnerId = intent.getIntExtra("leitnerId", 0)
        level = intent.getIntExtra("level", 0)
        if (saveInstanceIsNull) {
            viewModel.setState(ReverseReviewStateEvent.Init(leitnerId, level))
        }
        mBinding.btnTapToShow.setOnClickListener {
            showOrHideAnswer()
        }
        mBinding.actionBar.mBinding.title.text = String.format(getString(R.string.reverse_review_level),level.toString())
        mBinding.btnCheck.setOnClickListener { viewModel.setState(ReverseReviewStateEvent.CheckReverse) }
        mBinding.vm = viewModel
    }

    private fun showOrHideAnswer() {
        startFlipAnimationToShowOrHideAnswer()
        viewModel.getModel().answerVisible = !viewModel.getModel().answerVisible
    }

    private fun startFlipAnimationToShowOrHideAnswer() {
        mBinding.wordView.animate()
                .setDuration(150)
                .rotationY(90f)
                .withEndAction {
                    mBinding.wordView.rotationY = -90f
                    mBinding.wordView.animate()
                            .rotationY(0f)
                            .setDuration(150)
                            .start()
                }.start()

    }

    private fun setupObservers() {
        viewModel.exceptionObserver.observe(this) {
            manageDataState(DataState.Error(it as Exception))
        }

        viewModel.response.observe(this) { dataState ->
            if (dataState is DataState.Success) {
                if (dataState.data is ReviewResponse.ResetView) {
                    resetView()
                }
                if (dataState.data is ReviewResponse.MeansWithDictName) {
                    setupTranslatesResult(dataState.data.means)
                }
                if (dataState.data is ReviewResponse.Completed) {
                    finishedReview()
                }
            }
            manageDataState(dataState)
        }
    }

    override fun resetView() {
        mBinding.btnTapToShow.visibility = View.VISIBLE
        viewModel.getModel().answerVisible = false
    }

    private fun setupTranslatesResult(results: List<TranslateResult>) {
        results.forEach {
            (mBinding.rcvMeans.adapter as MeanAdapter).add(it)
        }
    }

    override fun finishedReview() {
        CustomAlertDialog.showDialog(this,
                cancelTitle = getString(R.string.close),
                title = getString(R.string.review_finished_title),
                message = getString(R.string.review_finished_subtitle),
                imageId = R.drawable.img_finished,
                delegate = object : AlertDialogDelegate {
                    override fun alertCompletion(type: AlertDialogDelegate.AlertTapType, extraButton: MaterialButton?) {
                        if (type == AlertDialogDelegate.AlertTapType.Cancel) {
                            finish()
                        }
                    }
                }
        )
    }

    override fun onMeanItemTaped(translateResult: TranslateResult) {
        val intent = Intent(this, WordWebViewActivity::class.java)
        intent.putExtra("Question", viewModel.getModel().question)
        intent.putExtra("TranslateResult", translateResult)
        intent.putExtra("leitnerId", leitnerId)
        startActivity(intent)
    }

}