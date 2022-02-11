package ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.delegates

import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.buttons.BrightnessButton
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.buttons.VolumeDownButton
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.buttons.VolumeUpButton
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.buttons.clipborad.ClipboardButton
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.buttons.translate.TranslateButton
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.models.ButtonModelInPanel

interface AssistiveButtonDelegate{

    fun action(delegate: FloatingWindowDelegate , buttonModel: ButtonModelInPanel)
    fun isOverlayPanelType(): Boolean {
        return this is VolumeUpButton || this is VolumeDownButton || this is BrightnessButton || this is ClipboardButton || this is TranslateButton
    }
}