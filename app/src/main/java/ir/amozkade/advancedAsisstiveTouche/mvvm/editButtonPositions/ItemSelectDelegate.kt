package ir.amozkade.advancedAsisstiveTouche.mvvm.editButtonPositions

import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.models.ButtonModelInPanel

interface ItemSelectDelegate {
    fun onSelectButtonItem(button: ButtonModelInPanel, selectedPositionInMenu:Int)
    fun onDeleteButton( selectedPositionInMenu:Int)
}