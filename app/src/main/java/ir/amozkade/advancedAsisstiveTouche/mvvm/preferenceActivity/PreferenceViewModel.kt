package ir.amozkade.advancedAsisstiveTouche.mvvm.preferenceActivity

import android.content.Context
import androidx.fragment.app.FragmentActivity
import javax.inject.Inject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.helper.customviews.CustomSwitch
import ir.amozkade.advancedAsisstiveTouche.helper.customviews.StepperView
import ir.amozkade.advancedAsisstiveTouche.helper.customviews.SwitchButton
import ir.amozkade.advancedAsisstiveTouche.mvvm.BaseActivityDelegate
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.FloatingWindow
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.models.ButtonSize
import kotlinx.coroutines.*

@HiltViewModel
class PreferenceViewModel @Inject constructor(
        @ApplicationContext private val context: Context,
        private val settingRepository: SettingRepository,
        val exceptionObserver: MutableLiveData<Throwable>
) : ViewModel() {

    private var isFromInit: Boolean = true
    private lateinit var delegate: BaseActivityDelegate

    var swAutoAlphaListener = object : SwitchButton.OnSwitchChangeListener {
        override fun onSwitchChanged(customSwitch: CustomSwitch, isChecked: Boolean) {
            model.autoAlphaEnabled = isChecked
            setState(PreferenceStateEvent.SaveAutomaticAlphaEnable(isChecked))
            restartButtonService()
        }
    }

    var swMoveToEdgeListener = object : SwitchButton.OnSwitchChangeListener {
        override fun onSwitchChanged(customSwitch: CustomSwitch, isChecked: Boolean) {
            model.moveToEdgeEnabled = isChecked
            setState(PreferenceStateEvent.SaveAutomaticEdgeEnable(isChecked))
            restartButtonService()
        }
    }

    var swShowSpeedListener = object : SwitchButton.OnSwitchChangeListener {
        override fun onSwitchChanged(customSwitch: CustomSwitch, isChecked: Boolean) {
            model.showSpeedEnabled = isChecked
            setState(PreferenceStateEvent.SaveSpeedEnable(isChecked))
            restartButtonService()
        }
    }

    var swIsLeftMenuListener = object : SwitchButton.OnSwitchChangeListener {
        override fun onSwitchChanged(customSwitch: CustomSwitch, isChecked: Boolean) {
            if (model.pagerEnabled) {
                delegate.showWarn(context.getString(R.string.warning), context.getString(R.string.pager_and_edge_cant_enable_same_time))
                customSwitch.setCheckedProgrammatically(false)
                return
            }
            model.leftPanelEnabled = isChecked
            setState(PreferenceStateEvent.SaveLeftMenuEnable(isChecked))
            restartButtonService()
        }
    }

    var swEnableAnimationListener = object : SwitchButton.OnSwitchChangeListener {
        override fun onSwitchChanged(customSwitch: CustomSwitch, isChecked: Boolean) {
            model.animationEnabled = isChecked
            setState(PreferenceStateEvent.SaveAnimationEnabled(isChecked))
            restartButtonService()
        }
    }

    var swEnablePagerListener = object : SwitchButton.OnSwitchChangeListener {
        override fun onSwitchChanged(customSwitch: CustomSwitch, isChecked: Boolean) {
            if (model.leftPanelEnabled) {
                delegate.showWarn(context.getString(R.string.warning), context.getString(R.string.pager_and_edge_cant_enable_same_time))
                customSwitch.setCheckedProgrammatically(false)
                return
            }
            model.pagerEnabled = isChecked
            setState(PreferenceStateEvent.SavePagerEnable(isChecked))
            restartButtonService()
        }
    }

    var swEnableMarqueeListener = object : SwitchButton.OnSwitchChangeListener {
        override fun onSwitchChanged(customSwitch: CustomSwitch, isChecked: Boolean) {
            model.enableMarqueeAnimation = isChecked
            setState(PreferenceStateEvent.SaveEnableMarqueeAnimation(isChecked))
            restartButtonService()
        }
    }

    var swEnableCircularButtonListener = object : SwitchButton.OnSwitchChangeListener {
        override fun onSwitchChanged(customSwitch: CustomSwitch, isChecked: Boolean) {
            model.enableCircularButton = isChecked
            setState(PreferenceStateEvent.SaveEnableCircular(isChecked))
            restartButtonService()
        }
    }

    var stepperButtonSizeListener = object : StepperView.StepperDelegate {
        override fun onNewValue(value: Int) {
            model.buttonSizeSliderValue = value
            setState(PreferenceStateEvent.SaveButtonSize(ButtonSize(value, value)))
            restartButtonService()
        }
    }

    var stepperSpeedTextSizeListener = object : StepperView.StepperDelegate {
        override fun onNewValue(value: Int) {
            model.speedTextSize = value
            setState(PreferenceStateEvent.SaveSpeedTextSize(value))
            restartButtonService()
        }
    }

    var stepperButtonSizeInPanelListener = object : StepperView.StepperDelegate {
        override fun onNewValue(value: Int) {
            model.panelButtonsIconSize = value
            setState(PreferenceStateEvent.SaveButtonIconSize(value))
            restartButtonService()
        }
    }

    var stepperPanelWidthPercentListener = object : StepperView.StepperDelegate {
        override fun onNewValue(value: Int) {
            model.panelWidthPercent = value
            setState(PreferenceStateEvent.SavePanelWidth(value.toFloat()))
            restartButtonService()
        }
    }

    var stepperTextSizeInPanelListener = object : StepperView.StepperDelegate {
        override fun onNewValue(value: Int) {
            model.buttonTextSizeInPanel = value
            setState(PreferenceStateEvent.SaveTextSizeInPanel(value))
            restartButtonService()
        }
    }

    private var model: PreferenceActivityModel = PreferenceActivityModel()
    fun initViewModel(delegate: BaseActivityDelegate) {
        this.delegate = delegate
        isFromInit = true
        val prefModel = settingRepository.getCashedModel()
        model.autoAlphaEnabled = prefModel.autoAlphaButtonEnable
        model.moveToEdgeEnabled = prefModel.isAutomaticEdgeEnabled
        model.showSpeedEnabled = prefModel.showSpeedEnabled
        model.showSpeedEnabledFor = true
        model.buttonSizeSliderEnabled = true
        model.buttonSizeSliderValue = getPixelValueFromDP(prefModel.buttonWidth)
        model.speedTextSize = prefModel.speedTextSize
        model.panelButtonsIconSize = prefModel.panelButtonsIconSize
        model.buttonTextSizeInPanel = prefModel.panelButtonsTextSize
        model.leftPanelEnabled = prefModel.isAutomaticEdgeEnabled
        model.pagerEnabled = prefModel.pagerEnable
        model.animationEnabled = prefModel.animationEnabled
        model.enableMarqueeAnimation = prefModel.enableMarqueeAnimation
        model.panelWidthPercent = prefModel.panelWidthPercent.toInt()
        model.buttonSelectedColor = prefModel.buttonColorOverlay
        model.panelSelectedColor = prefModel.panelColorOverlay
        model.panelButtonsColor = prefModel.panelButtonsColor
        model.enableCircularButton = prefModel.enableCircularButton
        GlobalScope.launch(Dispatchers.Main) {
            //screw checkbox init value listener
            delay(500)
            isFromInit = false
        }
    }

    fun getModel(): PreferenceActivityModel {
        return model
    }

    fun selectButtonColorTaped() {
        val color = settingRepository.getCashedModel().buttonColorOverlay
        ColorPickerDialog.newBuilder().setShowAlphaSlider(true).setDialogId(100).setColor(color).show(delegate as FragmentActivity)
    }

    fun saveSelectedButtonColor(color: Int) {
        model.buttonSelectedColor = color
        setState(PreferenceStateEvent.SaveButtonColorOverlay(color))
        restartButtonService()
    }

    fun selectPanelButtonsColorTaped() {
        val color = settingRepository.getCashedModel().panelButtonsColor
        ColorPickerDialog.newBuilder().setShowAlphaSlider(true).setDialogId(300).setColor(color).show(delegate as FragmentActivity)
    }

    fun selectPanelColorTaped() {
        val color = settingRepository.getCashedModel().panelColorOverlay
        model.panelSelectedColor = color
        ColorPickerDialog.newBuilder().setShowAlphaSlider(true).setDialogId(200).setColor(color).show(delegate as FragmentActivity)
    }

    fun saveSelectedPanelColor(color: Int) {
        model.panelSelectedColor = color
        setState(PreferenceStateEvent.SaveSelectedPanelColor(color))
        restartButtonService()
    }

    private fun restartButtonService() {
        if (!isFromInit) {
            FloatingWindow.restartButtonService(context, settingRepository)
        }
    }

    fun saveSelectedPanelButtonsColor(color: Int) {
        model.panelButtonsColor = color
        setState(PreferenceStateEvent.SavePanelButtonsColor(color))
        restartButtonService()
    }

    private fun getPixelValueFromDP(dpValue: Int): Int {
        return (dpValue.toFloat() / context.resources.displayMetrics.density).toInt()
    }

    fun setState(event: PreferenceStateEvent) {
        val handler = CoroutineExceptionHandler { _, exception ->
            exceptionObserver.postValue(exception)
        }

        viewModelScope.launch(Dispatchers.IO + handler) {
            supervisorScope {

                when (event) {
                    is PreferenceStateEvent.SaveButtonSize -> {
                        settingRepository.saveUserButtonSize(event.buttonSize)
                    }
                    is PreferenceStateEvent.SavePanelButtonsColor -> {
                        settingRepository.savePanelButtonsColor(event.color)
                    }
                    is PreferenceStateEvent.SaveSelectedPanelColor -> {
                        settingRepository.saveSelectedPanelColor(event.color)
                    }
                    is PreferenceStateEvent.SaveButtonColorOverlay -> {
                        settingRepository.saveButtonColorOverlay(event.color)
                    }
                    is PreferenceStateEvent.SaveSpeedTextSize -> {
                        settingRepository.saveSpeedTextSize(event.textSize)
                    }
                    is PreferenceStateEvent.SaveEnableCircular -> {
                        settingRepository.saveEnableCircular(event.isEnable)
                    }
                    is PreferenceStateEvent.SaveEnableMarqueeAnimation -> {
                        settingRepository.saveEnableMarqueeAnimation(event.isEnable)
                    }
                    is PreferenceStateEvent.SaveLeftMenuEnable -> {
                        settingRepository.saveLeftMenuEnable(event.isEnable)
                    }
                    is PreferenceStateEvent.SavePagerEnable -> {
                        settingRepository.savePagerEnable(event.isEnable)
                    }
                    is PreferenceStateEvent.SaveAnimationEnabled -> {
                        settingRepository.saveAnimationEnabled(event.isEnable)
                    }
                    is PreferenceStateEvent.SaveSpeedEnable -> {
                        settingRepository.saveSpeedEnable(event.isEnable)
                    }
                    is PreferenceStateEvent.SaveAutomaticEdgeEnable -> {
                        settingRepository.saveAutomaticEdgeEnable(event.isEnable)
                    }
                    is PreferenceStateEvent.SaveAutomaticAlphaEnable -> {
                        settingRepository.saveAutomaticAlphaEnable(event.isEnable)
                    }
                    is PreferenceStateEvent.SaveButtonIconSize -> {
                        settingRepository.saveButtonIconSize(event.size)
                    }
                    is PreferenceStateEvent.SavePanelWidth -> {
                        settingRepository.savePanelWidth(event.width)
                    }
                    is PreferenceStateEvent.SaveTextSizeInPanel -> {
                        settingRepository.saveTextSizeInPanel(event.textSize)
                    }
                }
            }
        }
    }
}