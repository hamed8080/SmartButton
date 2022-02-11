package ir.amozkade.advancedAsisstiveTouche.mvvm.editButtonPositions

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.PaintDrawable
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import ir.amozkade.advancedAsisstiveTouche.AppDir
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.databinding.ActivityEditButtonPositionBinding
import ir.amozkade.advancedAsisstiveTouche.helper.api.DataState
import ir.amozkade.advancedAsisstiveTouche.mvvm.BaseActivity
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.FloatingWindow
import ir.mobitrain.applicationcore.alertDialog.AlertDialogDelegate
import ir.mobitrain.applicationcore.alertDialog.CustomAlertDialog
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.models.ButtonModelInPanel
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.models.DefaultButtons
import ir.amozkade.advancedAsisstiveTouche.mvvm.editButtonPositions.adapters.GridEditButtonPositionsAdapter
import ir.amozkade.advancedAsisstiveTouche.mvvm.editButtonPositions.utils.EditPositionResponse
import ir.amozkade.advancedAsisstiveTouche.mvvm.editButtonPositions.utils.EditPositionStateEvent
import ir.amozkade.advancedAsisstiveTouche.mvvm.preferenceActivity.SettingRepository
import ir.mobitrain.applicationcore.helper.CommonHelpers
import ir.mobitrain.applicationcore.helper.Converters
import java.lang.Exception
import javax.inject.Inject

@AndroidEntryPoint
class EditButtonPositionsActivity : BaseActivity() {

    @Inject
    lateinit var defaultButtons: DefaultButtons

    @Inject
    @AppDir
    lateinit var appDir: String

    @Inject
    lateinit var settingRepository: SettingRepository

    private val viewModel: EditButtonPositionViewModel by viewModels()
    private lateinit var mBinding: ActivityEditButtonPositionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUI()
        setupObservers()
    }

    private fun setupUI() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_edit_button_position)
        mBinding.actionBar.btnAddSetOnClickListener { addNewBlankItemToAdapter() }
        mBinding.actionBar.btnDeleteSetOnClickListener { openDeleteAllActions() }
        mBinding.btnSave.setOnClickListener {
            val buttons = (mBinding.rcv.adapter as GridEditButtonPositionsAdapter).getEditedPositionOfButtons()
            viewModel.setState(EditPositionStateEvent.SavePositions(buttons))
        }

        mBinding.vm = viewModel
        setPanelBackground()
    }

    private fun setupObservers() {
        viewModel.exceptionObserver.observe(this) {
            manageDataState(DataState.Error(it as Exception))
        }
        viewModel.response.observe(this) { dataState ->
            if (dataState is DataState.Success) {
                if (dataState.data is EditPositionResponse.AllButtons) {
                    setupRecyclerView(dataState.data.buttons)
                }
                if (dataState.data is EditPositionResponse.Saved) {
                    FloatingWindow.restartButtonService(this, settingRepository)
                }
            }
            manageDataState(dataState)
        }
    }

    private fun setPanelBackground() {
        val setting = settingRepository.getCashedModel()
        val panel = mBinding.rcv
        val drawable = ContextCompat.getDrawable(this, R.drawable.progress_bar_container)
        when {
            setting.userSelectedPanelImageName != null -> {
                val options = BitmapFactory.Options()
                options.inPreferredConfig = Bitmap.Config.ARGB_8888
                val filePath: String = appDir + "/" + setting.userSelectedPanelImageName
                panel.background = CommonHelpers.getRoundedCornerDrawable(this, filePath, Converters.convertIntToDP(24, this))
            }
            setting.isLeftMenu -> {
                PaintDrawable(setting.panelColorOverlay).apply {
                    setCornerRadius(panel.layoutParams.width / 2f)
                    panel.background = this
                }
            }
            else -> {
                ((drawable as LayerDrawable).findDrawableByLayerId(R.id.backgroundShape) as GradientDrawable).setColor(setting.panelColorOverlay)
                panel.background = drawable
            }
        }
    }

    private fun addNewBlankItemToAdapter() {
        viewModel.getModel().listEmpty = false
        (mBinding.rcv.adapter as GridEditButtonPositionsAdapter).addNewBlankItem()
    }

    private fun setupRecyclerView(buttons: List<ButtonModelInPanel>) {
        viewModel.getModel().listEmpty = buttons.isEmpty()
        mBinding.rcv.run {
            adapter = GridEditButtonPositionsAdapter(ArrayList(buttons), defaultButtons, settingRepository, this@EditButtonPositionsActivity)
            adapter?.notifyDataSetChanged()
        }
    }

    private fun openDeleteAllActions() {
        CustomAlertDialog.showDialog(
                this,
                title = getString(R.string.delete_all_actions_title),
                submitTextColorId = ContextCompat.getColor(this, R.color.red),
                cancelTextColorId = ContextCompat.getColor(this, R.color.primary_color),
                submitTitle = getString(R.string.delete),
                message = getString(R.string.delete_actions_subtitle),
                imageId = R.drawable.img_delete,
                delegate = object : AlertDialogDelegate {
                    override fun alertCompletion(type: AlertDialogDelegate.AlertTapType, extraButton: MaterialButton?) {
                        if (type == AlertDialogDelegate.AlertTapType.Submit) {
                            viewModel.setState(EditPositionStateEvent.DeleteAll)
                        }
                    }
                })
    }

}