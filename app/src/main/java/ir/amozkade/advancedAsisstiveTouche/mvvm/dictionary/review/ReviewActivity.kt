package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.review

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.databinding.ActivityReviewLevelBinding
import ir.amozkade.advancedAsisstiveTouche.helper.api.DataState
import ir.amozkade.advancedAsisstiveTouche.mvvm.BaseActivity
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.addOrEditQuestion.AddOrEditQuestionActivity
import ir.mobitrain.applicationcore.alertDialog.AlertDialogDelegate
import ir.mobitrain.applicationcore.alertDialog.CustomAlertDialog
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.levels.models.Level
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.levels.questionAnswer.QuestionAnswer
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.reverseReview.ReverseReviewActivity
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.review.models.Synonym
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.review.utils.ReviewResponse
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.review.utils.ReviewStateEvent
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.translator.TranslateResult
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.wordWebView.WordWebViewActivity
import ir.mobitrain.applicationcore.DividerItemDecoration
import java.lang.Exception

@AndroidEntryPoint
class ReviewActivity : BaseActivity(), ReviewDelegate {

    private val viewModel: ReviewViewModel by viewModels()
    private lateinit var mBinding: ActivityReviewLevelBinding
    private var level: Int = 0
    private var leitnerId: Int = 0
    private var showDefinition = true

    private val editQuestionContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){activityResult ->
        if(activityResult.resultCode == RESULT_OK){
            activityResult?.data?.getParcelableExtra<QuestionAnswer>("questionAnswer")?.let {
                val isNewAdded = activityResult.data?.getBooleanExtra("isNewAdded",false)
                if (isNewAdded == true){
                    viewModel.addQuestionAnswer(it)
                }else{
                    viewModel.updateQuestionAnswer(it)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_review_level)
        setupUI(savedInstanceState == null)
        setupObservers()
    }

    private fun setupUI(saveInstanceIsNull: Boolean) {
        addLoadingsToContainer(mBinding.root as ViewGroup)
        leitnerId = intent.getIntExtra("leitnerId", 0)
        level = intent.getIntExtra("level", 0)
        showDefinition = intent.getBooleanExtra("showDefinition" ,true)
        if (saveInstanceIsNull) {
            viewModel.setState(ReviewStateEvent.Init(leitnerId, level))
        }
        mBinding.vm = viewModel
        mBinding.btnDeleteFromLeitner.setOnClickListener { showDeleteConfirmDialog() }
        mBinding.btnTapToShow.setOnClickListener {
            showOrHideAnswer()
        }

        mBinding.translationView.setOnClickListener {
            showOrHideAnswer()
        }
        mBinding.btnGuess.setOnClickListener { viewModel.setState(ReviewStateEvent.Guessed) }
        mBinding.btnFail.setOnClickListener { viewModel.setState(ReviewStateEvent.Failed) }
        mBinding.btnSpeakWord.setOnClickListener { viewModel.setState(ReviewStateEvent.Speak) }
        mBinding.actionBar.setActionBarTitle(Level.getLocalizedLevelName(this, level))
        mBinding.rcvDefinitions.addItemDecoration(DividerItemDecoration(this))
        mBinding.rcvSynonyms.addItemDecoration(DividerItemDecoration(this))
        mBinding.rcvMeans.adapter = MeanAdapter(arrayListOf(), this)
        mBinding.rcvMeans.addItemDecoration(DividerItemDecoration(this))
        mBinding.actionBar.btnBackSetOnClickListener{ showExitReviewConfirmationDialog() }
        mBinding.btnEditQuestion.setOnClickListener { openEditQuestionActivity() }
        mBinding.btnOpenReverse.setOnClickListener { openReverseActivity() }
        mBinding.btnAddNewWordToLeitner.setOnClickListener { openAddQuestionActivity() }
        mBinding.btnFav.setOnClickListener {
            setFavorite(!(viewModel.getModel().favorite))
            viewModel.setState(ReviewStateEvent.Favorite)
        }
    }

    private fun openReverseActivity() {
        val intent = Intent(this , ReverseReviewActivity::class.java)
        intent.putExtra("leitnerId", leitnerId)
        intent.putExtra("level", level)
        startActivity(intent)
        finish()
    }

    private fun showOrHideAnswer() {
        startFlipAnimationToShowOrHideAnswer()
        if (viewModel.getModel().manual) {
            viewModel.getModel().answerVisible = !viewModel.getModel().answerVisible
            mBinding.txtAnswer.visibility = if (viewModel.getModel().answerVisible) View.VISIBLE else View.GONE
            mBinding.rcvMeans.visibility = View.GONE
            mBinding.txtAnswer.typeface = ResourcesCompat.getFont(this, R.font.iransans_bold)
            showAnswerTaped( viewModel.getModel().answerVisible )
        } else {
            mBinding.txtAnswer.visibility = View.GONE
            val isAnswerVisible = viewModel.getModel().answerVisible
            viewModel.getModel().answerVisible = !isAnswerVisible
            showAnswerTaped(!isAnswerVisible)
        }
    }

    private fun openEditQuestionActivity() {
        val intent = Intent(this, AddOrEditQuestionActivity::class.java)
        intent.putExtra("QuestionAnswer", viewModel.getModel().questionAnswer)
        editQuestionContent.launch(intent)
    }

    private fun openAddQuestionActivity() {
        val intent = Intent(this, AddOrEditQuestionActivity::class.java)
        intent.putExtra("leitnerId", viewModel.getModel().leitner.id)
        editQuestionContent.launch(intent)
    }

    private fun setupObservers() {
        viewModel.exceptionObserver.observe(this) {
            manageDataState(DataState.Error(it as Exception))
        }
        viewModel.definitions.observe(this) {
            setupDefinitionsAdapter(it)
        }
        viewModel.synonyms.observe(this) {
            setupSynonymAdapter(it)
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
                if (dataState.data is ReviewResponse.Favorite){
                    setFavorite(dataState.data.favorite)
                }
            }
            manageDataState(dataState)
        }
    }

    private fun setFavorite(favorite:Boolean){
        val icon = if (favorite) R.drawable.ic_baseline_star_24 else R.drawable.ic_baseline_star_outline_24
        mBinding.btnFav.setIconResource(icon)
    }

    private fun startFlipAnimationToShowOrHideAnswer() {
        mBinding.translationView.animate()
                .setDuration(150)
                .rotationY(90f)
                .withEndAction {
                    mBinding.translationView.rotationY = -90f
                    mBinding.translationView.animate()
                            .rotationY(0f)
                            .setDuration(150)
                            .start()
                }.start()

    }

    private fun setupTranslatesResult(results: List<TranslateResult>) {
        results.forEach {
            (mBinding.rcvMeans.adapter as MeanAdapter).add(it)
        }
    }

    override fun onMeanItemTaped(translateResult: TranslateResult) {
        val intent = Intent(this, WordWebViewActivity::class.java)
        intent.putExtra("Question", viewModel.getModel().question)
        intent.putExtra("TranslateResult", translateResult)
        intent.putExtra("leitnerId", leitnerId)
        startActivity(intent)
    }

    private fun setupDefinitionsAdapter(definitions: List<String>) {
        mBinding.definitionsView.visibility = if (definitions.isEmpty() || !showDefinition) View.GONE else View.VISIBLE
        mBinding.rcvDefinitions.adapter = DefinitionsAdapter(definitions)
        (mBinding.rcvDefinitions.adapter as DefinitionsAdapter).notifyDataSetChanged()
    }

    private fun setupSynonymAdapter(synonyms: List<Synonym>) {
        mBinding.synonymsView.visibility = if (synonyms.isEmpty() || !showDefinition) View.GONE else View.VISIBLE
        mBinding.rcvSynonyms.adapter = SynonymsAdapter(synonyms)
        (mBinding.rcvSynonyms.adapter as SynonymsAdapter).notifyDataSetChanged()
    }

    private fun showAnswerTaped(showAnswer: Boolean) {
        mBinding.rcvMeans.visibility = View.VISIBLE
        mBinding.rcvMeans.alpha = if (showAnswer) 0f else 1f
        mBinding.rcvMeans.scaleX = if (showAnswer) 0f else 1f
        mBinding.rcvMeans.scaleY = if (showAnswer) 0f else 1f
        mBinding.rcvMeans.animate()
                .alpha(if (showAnswer) 1f else 0f)
                .scaleX(if (showAnswer) 1f else 0f)
                .scaleY(if (showAnswer) 1f else 0f).duration = 200


        mBinding.btnTapToShow.visibility = View.VISIBLE
        mBinding.btnTapToShow.alpha = if (showAnswer) 1f else 0f
        mBinding.btnTapToShow.scaleX = if (showAnswer) 1f else 0f
        mBinding.btnTapToShow.scaleY = if (showAnswer) 1f else 0f
        mBinding.btnTapToShow.animate()
                .alpha(if (showAnswer) 0f else 1f)
                .scaleX(if (showAnswer) 0f else 1f)
                .scaleY(if (showAnswer) 0f else 1f).duration = 200
        if(!showDefinition){
            mBinding.definitionsView.visibility = if(showAnswer) View.VISIBLE else View.INVISIBLE
            mBinding.synonymsView.visibility = if(showAnswer) View.VISIBLE else View.INVISIBLE
        }
    }

    override fun resetView() {
        viewModel.getModel().answerVisible = false
        mBinding.btnTapToShow.visibility = View.VISIBLE
        mBinding.rcvMeans.visibility = View.GONE
        mBinding.txtAnswer.visibility = View.GONE
        mBinding.txtAnswer.text = null
        mBinding.rcvMeans.adapter = MeanAdapter(arrayListOf(), this)
        (mBinding.rcvMeans.adapter as MeanAdapter).notifyDataSetChanged()
        showAnswerTaped(false)
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

    private fun showDeleteConfirmDialog() {
        CustomAlertDialog.showDialog(
                this,
                title = getString(R.string.delete_question_title),
                submitTextColorId = ContextCompat.getColor(this, R.color.red),
                cancelTextColorId = ContextCompat.getColor(this, R.color.primary_color),
                submitTitle = getString(R.string.delete),
                message = getString(R.string.delete_question_subtitle),
                imageId = R.drawable.img_delete,
                delegate = object : AlertDialogDelegate {
                    override fun alertCompletion(type: AlertDialogDelegate.AlertTapType, extraButton: MaterialButton?) {
                        if (type == AlertDialogDelegate.AlertTapType.Submit) {
                            viewModel.setState(ReviewStateEvent.DeleteQuestion)
                        }
                    }
                })
    }

    override fun onBackPressed() {
        showExitReviewConfirmationDialog()
    }

    private fun showExitReviewConfirmationDialog() {

        CustomAlertDialog.showDialog(
                this,
                title = getString(R.string.exit_from_review_title),
                submitTextColorId = ContextCompat.getColor(this, R.color.red),
                cancelTextColorId = ContextCompat.getColor(this, R.color.primary_color),
                submitTitle = getString(R.string.yes),
                message = getString(R.string.exit_from_review_title),
                delegate = object : AlertDialogDelegate {
                    override fun alertCompletion(type: AlertDialogDelegate.AlertTapType, extraButton: MaterialButton?) {
                        if (type == AlertDialogDelegate.AlertTapType.Submit) {
                            finish()
                        }
                    }
                })
    }
}
