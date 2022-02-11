package ir.amozkade.advancedAsisstiveTouche.mvvm.editButtonPositions.utils

import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.models.ButtonModelInPanel

sealed class EditPositionStateEvent {
    object DeleteAll:EditPositionStateEvent()
    object Init:EditPositionStateEvent()
    data class SavePositions(val buttons:List<ButtonModelInPanel>):EditPositionStateEvent()
}