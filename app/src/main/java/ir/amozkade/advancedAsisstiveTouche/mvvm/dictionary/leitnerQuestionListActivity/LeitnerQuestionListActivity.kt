package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.leitnerQuestionListActivity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.databinding.ActivityLeitnerQuestionListBinding
import ir.amozkade.advancedAsisstiveTouche.helper.api.DataState
import ir.amozkade.advancedAsisstiveTouche.mvvm.BaseActivity
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.addOrEditQuestion.AddOrEditQuestionActivity
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.leitnerQuestionListActivity.utils.LeitnerQuestionListResponse
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.leitnerQuestionListActivity.utils.LeitnerQuestionListStateEvent
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.leitners.models.Leitner
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.levels.models.Level
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.levels.questionAnswer.QuestionAnswer
import ir.mobitrain.applicationcore.DividerItemDecoration
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import java.lang.Exception

@AndroidEntryPoint
class LeitnerQuestionListActivity : BaseActivity(), LeitnerQuestionListAdapter.OnQuestionListener {

    private val viewModel by viewModels<LeitnerQuestionListViewModel>()
    private lateinit var mBinding: ActivityLeitnerQuestionListBinding
    var leitnerId: Int? = null
    private val argsScrollLState = "recyclerState"

    private val addQuestionConsent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            if (activityResult.data?.getBooleanExtra("isNewAdded",false) == false){
                viewModel.setState(LeitnerQuestionListStateEvent.Edited(activityResult?.data?.getParcelableExtra("questionAnswer")))
            }else{
                viewModel.setState(LeitnerQuestionListStateEvent.Add(activityResult?.data?.getParcelableExtra("questionAnswer")))
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_leitner_question_list)
        leitnerId = intent?.getIntExtra("leitnerId", 0) ?: return
        setupUI()
        setupObservers()
        if (savedInstanceState == null){
            leitnerId?.let {
                viewModel.setState(LeitnerQuestionListStateEvent.GetAllLeitnerQuestions(it))
            }
        }else{
            val adapterState:Parcelable? = savedInstanceState.getParcelable(argsScrollLState)
            mBinding.rcvQuestions.layoutManager?.onRestoreInstanceState(adapterState)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (mBinding.rcvQuestions.adapter != null){
            outState.putParcelable(argsScrollLState, mBinding.rcvQuestions.layoutManager?.onSaveInstanceState())
        }
    }

    private fun setupUI() {
        addLoadingsToContainer(mBinding.root as ViewGroup)
        mBinding.actionBar.btnBackSetOnClickListener { finish() }
        mBinding.rcvQuestions.addItemDecoration(DividerItemDecoration(this))
        mBinding.vm = viewModel
        mBinding.chipGroup.setOnCheckedChangeListener { _, checkedId ->
            viewModel.setState(LeitnerQuestionListStateEvent.Sort(checkedId))
        }

        mBinding.actionBar.mBinding.btnAdd.setTextColor( ContextCompat.getColor(this,R.color.primary_color))
        mBinding.actionBar.btnAddSetOnClickListener {
            val intent = Intent(this, AddOrEditQuestionActivity::class.java)
            intent.putExtra("leitnerId", leitnerId)
            addQuestionConsent.launch(intent)
        }
    }

    private fun setupObservers() {
        viewModel.exceptionObserver.observe(this) {
            manageDataState(DataState.Error(it as Exception))
        }
        viewModel.response.observe(this) { dataState ->
            if (dataState is DataState.Success) {
                if (dataState.data is LeitnerQuestionListResponse.AllQuestions) {
                    setupRecyclerView(
                        ArrayList(dataState.data.questionAnswers),
                        dataState.data.levels
                    )
                }

                if (dataState.data is LeitnerQuestionListResponse.QuestionAnswerUpdated) {
                    (mBinding.rcvQuestions.adapter as? LeitnerQuestionListAdapter)?.update(dataState.data.questionAnswer)
                }

                if (dataState.data is LeitnerQuestionListResponse.Removed) {
                    (mBinding.rcvQuestions.adapter as? LeitnerQuestionListAdapter)?.remove(dataState.data.questionAnswer)
                }

                if (dataState.data is LeitnerQuestionListResponse.Added) {
                    (mBinding.rcvQuestions.adapter as? LeitnerQuestionListAdapter)?.add(dataState.data.questionAnswer)
                }
            }
            manageDataState(dataState)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupRecyclerView(questionAnswers: ArrayList<QuestionAnswer>, levels: List<Level>) {
        mBinding.rcvQuestions.run {
            adapter = LeitnerQuestionListAdapter(
                questionAnswers,
                levels,
                this@LeitnerQuestionListActivity
            )
            adapter?.notifyDataSetChanged()
        }
    }

    override fun onQuestionDeleteTaped(questionAnswer: QuestionAnswer) {
        viewModel.setState(LeitnerQuestionListStateEvent.Delete(questionAnswer))
    }

    override fun onQuestionEditTaped(questionAnswer: QuestionAnswer) {
        val intent = Intent(this, AddOrEditQuestionActivity::class.java)
        intent.putExtra("QuestionAnswer", questionAnswer)
        addQuestionConsent.launch(intent)
    }

    override fun onBackToListTaped(questionAnswer: QuestionAnswer) {
        viewModel.setState(LeitnerQuestionListStateEvent.BackToList(questionAnswer))
    }

    override fun onFavTaped(questionAnswer: QuestionAnswer) {
        viewModel.setState(LeitnerQuestionListStateEvent.Fav(questionAnswer))
    }

    override fun onMoveToLeitner(questionAnswer: QuestionAnswer) {
        CoroutineScope(IO).launch {
            val categories = viewModel.getAllLeitners()
            withContext(Dispatchers.Main) {
                showImportDialog(categories, questionAnswer)
            }
        }
    }

    private var leitnerSelectedIndex: Int = 0
    private fun showImportDialog(leitners: List<Leitner>, questionAnswer: QuestionAnswer) {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.chose_leitner_to_move))
            .setNeutralButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.move)) { dialog, _ ->
                val leitnerSelectedIndex = leitnerSelectedIndex
                if (leitners.count() == 0) {
                    showWarn(
                        getString(R.string.add_new_leitner),
                        getString(R.string.add_new_leitner)
                    )
                    return@setPositiveButton
                }
                val leitner = leitners[leitnerSelectedIndex]
                viewModel.setState(
                    LeitnerQuestionListStateEvent.MoveToLeitner(
                        questionAnswer,
                        leitner.id
                    )
                )
                dialog.dismiss()
            }
            .setSingleChoiceItems(leitners.map { it.name }.toTypedArray(), 0) { _, which ->
                leitnerSelectedIndex = which
            }
            .show()
    }
}
