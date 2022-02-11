package ir.amozkade.advancedAsisstiveTouche.mvvm.editButtonPositions.utils

import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.models.ButtonModelInPanel

sealed class EditPositionResponse{
    data class AllButtons(val buttons: List<ButtonModelInPanel>): EditPositionResponse()
    object Saved:EditPositionResponse()
}
