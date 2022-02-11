package ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.delegates

import android.content.Context

interface EnableDelegate {
    fun initIsEnable(context: Context)
    var isEnable:Boolean
}