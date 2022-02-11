package ir.amozkade.advancedAsisstiveTouche.mvvm.preferenceActivity

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import ir.amozkade.advancedAsisstiveTouche.BR
import ir.amozkade.advancedAsisstiveTouche.R

class PreferenceActivityModel : BaseObservable() {

    @Bindable
    var buttonTextSizeInPanel: Int=16
        set(value) {
            field = value
            notifyPropertyChanged(BR.buttonTextSizeInPanel)
        }
    @Bindable
    var panelWidthPercent: Int=80
        set(value) {
            field = value
            notifyPropertyChanged(BR.panelWidthPercent)
        }

    @Bindable
    var panelButtonsIconSize: Int=16
        set(value) {
            field = value
            notifyPropertyChanged(BR.panelButtonsIconSize)
        }

    @Bindable
    var speedTextSize: Int = 12
    set(value) {
        field = value
        notifyPropertyChanged(BR.speedTextSize)
    }

    @Bindable
    var buttonSizeSliderValue: Int = 64
        set(value) {
            field = value
            notifyPropertyChanged(BR.buttonSizeSliderValue)
        }

    @Bindable
    var buttonSizeSliderEnabled: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.buttonSizeSliderEnabled)
        }

    @Bindable
    var autoAlphaEnabled: Boolean = true
        set(value) {
            field = value
            notifyPropertyChanged(BR.autoAlphaEnabled)
        }

    @Bindable
    var moveToEdgeEnabled: Boolean = true
        set(value) {
            field = value
            notifyPropertyChanged(BR.moveToEdgeEnabled)
        }

    @Bindable
    var showSpeedEnabled: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.showSpeedEnabled)
        }

    @Bindable
    var showSpeedEnabledFor: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.showSpeedEnabledFor)
        }


    @Bindable
    var leftPanelEnabled: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.leftPanelEnabled)
        }

    @Bindable
    var animationEnabled: Boolean = true
        set(value) {
            field = value
            notifyPropertyChanged(BR.animationEnabled)
        }

    @Bindable
    var pagerEnabled: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.pagerEnabled)
        }

    @Bindable
    var enableMarqueeAnimation: Boolean = true
        set(value) {
            field = value
            notifyPropertyChanged(BR.enableMarqueeAnimation)
        }

    @Bindable
    var enableCircularButton: Boolean = true
        set(value) {
            field = value
            notifyPropertyChanged(BR.enableCircularButton)
        }

    @Bindable
    var buttonSelectedColor: Int = R.color.white
        set(value) {
            field = value
            notifyPropertyChanged(BR.buttonSelectedColor)
        }

    @Bindable
    var panelSelectedColor: Int = R.color.white
        set(value) {
            field = value
            notifyPropertyChanged(BR.panelSelectedColor)
        }

    @Bindable
    var panelButtonsColor: Int = R.color.white
        set(value) {
            field = value
            notifyPropertyChanged(BR.panelButtonsColor)
        }
}