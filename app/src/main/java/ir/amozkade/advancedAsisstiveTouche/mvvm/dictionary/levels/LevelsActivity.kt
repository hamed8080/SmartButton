package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.levels

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.databinding.ActivityLevelsBinding
import ir.amozkade.advancedAsisstiveTouche.databinding.BottomSheetForLevelBinding
import ir.amozkade.advancedAsisstiveTouche.helper.api.DataState
import ir.amozkade.advancedAsisstiveTouche.mvvm.BaseActivity
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.addOrEditQuestion.AddOrEditQuestionActivity
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.leitnerQuestionListActivity.LeitnerQuestionListActivity
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.leitners.models.Leitner
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.leitners.models.LeitnerLevels
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.levels.utils.LevelResponse
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.levels.utils.LevelStateEvent
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.review.ReviewActivity

@AndroidEntryPoint
class LevelsActivity : BaseActivity(), LevelsAdapter.OnLevelListener, LevelsDelegate {

    private val viewModel: LevelsViewModel by viewModels()
    private lateinit var mBinding: ActivityLevelsBinding
    override lateinit var leitner : Leitner
    private val argsScrollLState = "recyclerState"

    private val addQuestionConsent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
        if (activityResult.resultCode == RESULT_OK) {
            viewModel.setState(LevelStateEvent.GetAllLevelsInLeitner(leitner))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_levels)
        leitner = intent?.getParcelableExtra("Leitner") ?: return
        setupUI()
        setupObservers()
        if (savedInstanceState == null){
            viewModel.setState(LevelStateEvent.GetAllLevelsInLeitner(leitner))
        }else{
            val adapterState: Parcelable? = savedInstanceState.getParcelable(argsScrollLState)
            mBinding.rcvLevels.layoutManager?.onRestoreInstanceState(adapterState)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (mBinding.rcvLevels.adapter != null){
            outState.putParcelable(argsScrollLState, mBinding.rcvLevels.layoutManager?.onSaveInstanceState())
        }
    }

    private fun setupUI() {
        mBinding.vm = viewModel
        addLoadingsToContainer(mBinding.root as ViewGroup)
        viewModel.setLeitner(leitner)
        mBinding.actionBar.btnBackSetOnClickListener { finish() }
        mBinding.actionBar.setActionBarTitle(leitner.name)
        mBinding.btnList.setOnClickListener { openQuestionListInLeitner() }
        mBinding.btnAddQuestion.setOnClickListener {
            openAddNewQuestion()
        }
    }

    private fun openQuestionListInLeitner() {
        val intent = Intent(this , LeitnerQuestionListActivity::class.java)
        intent.putExtra("leitnerId", leitner.id)
        startActivity(intent)
    }

    private fun setupObservers() {
        viewModel.exceptionObserver.observe(this) {
            manageDataState(DataState.Error(it as Exception))
        }
        viewModel.response.observe(this) { dataState ->
            if (dataState is DataState.Success) {
                if (dataState.data is LevelResponse.Levels) {
                    val levels = dataState.data.levels
                    setupRecyclerView(ArrayList(levels))
                }
            }
            manageDataState(dataState)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupRecyclerView(levels: ArrayList<LeitnerLevels>) {
        mBinding.rcvLevels.run {
            adapter = LevelsAdapter(levels, this@LevelsActivity)
            adapter?.notifyDataSetChanged()
        }
    }

    private fun openAddNewQuestion() {
        val intent = Intent(this, AddOrEditQuestionActivity::class.java)
        intent.putExtra("leitnerId", leitner.id)
        addQuestionConsent.launch(intent)
    }

    override fun onLevelTaped(level: LeitnerLevels) {
        if (level.reviewableCount == 0) {
            showWarn(getString(R.string.not_found_any_question_title), getString(R.string.not_found_any_question_subtitle), imageId = R.drawable.ic_empty_level)
            return
        }
        val intent = Intent(this, ReviewActivity::class.java)
        intent.putExtra("level", level.level)
        intent.putExtra("leitnerId", leitner.id)
        intent.putExtra("showDefinition", leitner.showDefinition)
        startActivity(intent)
    }

    override fun onLongPressOnLevel(level: LeitnerLevels) {
        cto?.let { cto ->
            val bindingView = DataBindingUtil.inflate<BottomSheetForLevelBinding>(cto.layoutInflater, R.layout.bottom_sheet_for_level, null, false)
            val sheet = BottomSheetDialog(cto)
            bindingView.tfDay.setText(level.time.toString())
            bindingView.vm = viewModel
            viewModel.getModel().time = level.time.toString()
            bindingView.btnSaveDay.setOnClickListener {
                val day = bindingView.tfDay.text.toString().toInt()
                (mBinding.rcvLevels.adapter as LevelsAdapter).updateLevelTime(day, level)
                viewModel.getModel().time = day.toString()
                viewModel.setState(LevelStateEvent.UpdateTimeOfLevel(level, day))
                sheet.hide()
            }
            sheet.setContentView(bindingView.root)
            sheet.show()
        }
    }

}
