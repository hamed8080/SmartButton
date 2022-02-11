package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.leitners

import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.databinding.ActivityLeitnerBinding
import ir.amozkade.advancedAsisstiveTouche.databinding.BottomSheetForCategotyBinding
import ir.amozkade.advancedAsisstiveTouche.helper.api.DataState
import ir.amozkade.advancedAsisstiveTouche.helper.customviews.CustomSwitch
import ir.amozkade.advancedAsisstiveTouche.helper.customviews.SwitchButton
import ir.amozkade.advancedAsisstiveTouche.helper.customviews.switchChangeListener
import ir.mobitrain.applicationcore.helper.animations.CommonAnimation
import ir.amozkade.advancedAsisstiveTouche.mvvm.BaseActivity
import ir.mobitrain.applicationcore.alertDialog.CustomAlertDialog
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.leitners.add.AddOrEditLeitnerActivity
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.leitners.models.Leitner
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.downloadDictionary.DownloadDictionaryActivity
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.leitners.utils.LeitnerResponse
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.leitners.utils.LeitnerStateEvent
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.levels.LevelsActivity
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.manageDictionaries.ManageDictionariesActivity
import ir.mobitrain.applicationcore.alertDialog.AlertDialogDelegate
import ir.mobitrain.applicationcore.helper.animations.AnimationListener
import java.lang.Exception

@AndroidEntryPoint
class LeitnerActivity : BaseActivity() , LeitnerAdapter.LeitnerListener {

    private val viewModel by viewModels<LeitnerViewModel>()
    private lateinit var mBinding: ActivityLeitnerBinding
    private var openMenu = false

    private val addOrEditLeitnerConsent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
        if (activityResult.resultCode == RESULT_OK) {
            activityResult?.data?.getParcelableExtra<Leitner>("Leitner")?.let {
                viewModel.setState(LeitnerStateEvent.AddOrEditLeitner(it))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_leitner)
        setupUI()
        setupObservers()
    }

    private fun setupUI() {
        addLoadingsToContainer(mBinding.root as ViewGroup)
        mBinding.rcv.adapter = LeitnerAdapter(arrayListOf(),this)
        mBinding.btnOpenActionMenu.setOnClickListener {
            if (mBinding.actionMenu.visibility == View.VISIBLE) {
                openMenu = false
                CommonAnimation.doAnimationHide(mBinding.actionMenu, listener = object : AnimationListener() {
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        if (!openMenu) {
                            mBinding.actionMenu.visibility = View.GONE
                        }
                    }
                })
            } else {
                openMenu = true
                mBinding.actionMenu.visibility = View.VISIBLE
                CommonAnimation.doAnimation(mBinding.actionMenu, 100)
            }
        }
        mBinding.btnAddLeitner.setOnClickListener {
            mBinding.btnOpenActionMenu.performClick()
            openAddOrEditLeitner()
        }

        mBinding.txtBtnAddLeitner.setOnClickListener {
            mBinding.btnOpenActionMenu.performClick()
            openAddOrEditLeitner()
        }
        mBinding.btnAddFromSource.setOnClickListener {
            mBinding.btnOpenActionMenu.performClick()
            openAddFromSource()
        }
        mBinding.btnManageDictionaries.setOnClickListener {
            mBinding.btnOpenActionMenu.performClick()
            openManageDictionaries()
        }
        mBinding.btnMinus.setOnClickListener {
            mBinding.btnOpenActionMenu.performClick()
        }
        mBinding.actionMenu.setOnClickListener {
            mBinding.btnOpenActionMenu.performClick()
        }
        viewModel.setState(LeitnerStateEvent.AllLeitner)
    }

    private fun setupObservers() {
        viewModel.exceptionObserver.observe(this) {
            manageDataState(DataState.Error(it as Exception))
        }

        viewModel.response.observe(this) { dataState ->
            if (dataState is DataState.Success) {
                when (dataState.data) {
                    is LeitnerResponse.AllLeitner -> {
                        setupRecyclerView(dataState.data.leitners)
                    }
                    is LeitnerResponse.AddOrEditedLeitner -> {
                        setupRecyclerView(dataState.data.leitners)
                    }
                    is LeitnerResponse.DeletedLeitner -> {
                        (mBinding.rcv.adapter as LeitnerAdapter).deleteLeitner(dataState.data.leitner)
                    }
                }
            }
        }
    }

    private fun setupRecyclerView(leitners: List<Leitner>) {
        mBinding.rcv.adapter = LeitnerAdapter(ArrayList(leitners),this)
    }

    private fun openManageDictionaries() {
        startActivity(Intent(this, ManageDictionariesActivity::class.java))
    }

    private fun openAddFromSource() {
        startActivity(Intent(this, DownloadDictionaryActivity::class.java))
    }

    private fun openAddOrEditLeitner(leitner: Leitner? = null) {
        val intent = Intent(cto, AddOrEditLeitnerActivity::class.java)
        leitner?.let {
            intent.putExtra("Leitner", it)
        }
        addOrEditLeitnerConsent.launch(intent)
    }

    override fun onClickLeitner(leitner: Leitner) {
        val intent = Intent(cto, LevelsActivity::class.java)
        intent.putExtra("Leitner", leitner)
        startActivity(intent)
    }

    override fun onLongPressOnLeitner(leitner: Leitner) {
        cto?.let { cto ->
            val bindingView = DataBindingUtil.inflate<BottomSheetForCategotyBinding>(cto.layoutInflater, R.layout.bottom_sheet_for_categoty, null, false)
            val sheet = BottomSheetDialog(cto)
            switchChangeListener(bindingView.swEnableTopLevel,object :SwitchButton.OnSwitchChangeListener{
                override fun onSwitchChanged(customSwitch: CustomSwitch, isChecked: Boolean) {
                    leitner.isBackToTopEnable = isChecked
                    viewModel.setState(LeitnerStateEvent.SetBackToTopEnable(leitner))
                }
            })

            switchChangeListener(bindingView.swEnableShowDefinition,object :SwitchButton.OnSwitchChangeListener{
                override fun onSwitchChanged(customSwitch: CustomSwitch, isChecked: Boolean) {
                    leitner.showDefinition = isChecked
                    viewModel.setState(LeitnerStateEvent.SetBackToTopEnable(leitner))
                }
            })
            bindingView.swEnableTopLevel.setSwitch(leitner.isBackToTopEnable)
            bindingView.swEnableShowDefinition.setSwitch(leitner.showDefinition)
            bindingView.btnDeleteLeitner.setOnClickListener {
                CustomAlertDialog.showDialog(
                        cto,
                        title = getString(R.string.delete_leitner_submit),
                        submitTextColorId = ContextCompat.getColor(cto, R.color.red),
                        cancelTextColorId = ContextCompat.getColor(cto, R.color.primary_color),
                        submitTitle = getString(R.string.delete),
                        message = getString(R.string.delete_leitner_subtitle),
                        imageId = R.drawable.img_delete,
                        delegate = object : AlertDialogDelegate {
                            override fun alertCompletion(type: AlertDialogDelegate.AlertTapType, extraButton: MaterialButton?) {
                                if (type == AlertDialogDelegate.AlertTapType.Submit) {
                                    viewModel.setState(LeitnerStateEvent.DeleteLeitner(leitner))
                                    sheet.hide()
                                }
                            }
                        })
            }
            sheet.setContentView(bindingView.root)
            sheet.show()
        }
    }

}
