package ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.delegates

import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.models.ButtonModelInPanel

interface LongActionDelegate {
    fun actionLong(floatingWindow: FloatingWindowDelegate, buttonModel: ButtonModelInPanel)
}