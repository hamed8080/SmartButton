package ir.amozkade.advancedAsisstiveTouche.mvvm.preferenceActivity

import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.models.ButtonSize

sealed class PreferenceStateEvent{
    data class SaveButtonSize(val buttonSize: ButtonSize):PreferenceStateEvent()
    data class SavePanelButtonsColor(val color:Int):PreferenceStateEvent()
    data class SaveSelectedPanelColor(val color: Int):PreferenceStateEvent()
    data class SaveButtonColorOverlay(val color: Int):PreferenceStateEvent()
    data class SaveSpeedTextSize(val textSize:Int):PreferenceStateEvent()
    data class SaveEnableCircular(val isEnable:Boolean):PreferenceStateEvent()
    data class SaveEnableMarqueeAnimation(val isEnable:Boolean):PreferenceStateEvent()
    data class SaveLeftMenuEnable(val isEnable: Boolean):PreferenceStateEvent()
    data class SavePagerEnable(val isEnable: Boolean):PreferenceStateEvent()
    data class SaveAnimationEnabled(val isEnable: Boolean):PreferenceStateEvent()
    data class SaveSpeedEnable(val isEnable: Boolean):PreferenceStateEvent()
    data class SaveAutomaticEdgeEnable(val isEnable: Boolean):PreferenceStateEvent()
    data class SaveAutomaticAlphaEnable(val isEnable: Boolean):PreferenceStateEvent()
    data class SaveButtonIconSize(val size: Int):PreferenceStateEvent()
    data class SavePanelWidth(val width: Float):PreferenceStateEvent()
    data class SaveTextSizeInPanel(val textSize: Int):PreferenceStateEvent()
}