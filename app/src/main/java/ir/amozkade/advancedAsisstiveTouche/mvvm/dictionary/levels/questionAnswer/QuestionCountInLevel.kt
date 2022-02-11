package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.levels.questionAnswer

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class QuestionCountInLevel constructor(val id: Int,
                                       val name: String,
                                       val levelId: Int,
                                       val questionAnswers: List<QuestionAnswer>) : Parcelable {
}