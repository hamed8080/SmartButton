package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.manageDictionaries

import ir.amozkade.advancedAsisstiveTouche.mvvm.BaseActivityDelegate
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.levels.questionAnswer.QuestionAnswer

interface ManageDictionaryDelegate : BaseActivityDelegate {
    fun onDeleteDictionaryTaped(dictionary: Dictionary)
    fun tapeOnAddAllToLeitner(dictionary: Dictionary)
}