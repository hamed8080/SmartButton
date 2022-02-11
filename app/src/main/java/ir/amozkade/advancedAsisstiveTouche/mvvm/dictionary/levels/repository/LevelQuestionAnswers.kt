package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.levels.repository

import android.os.Parcelable
import androidx.room.Relation
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.levels.questionAnswer.QuestionAnswer
import kotlinx.android.parcel.Parcelize

@Parcelize
class LevelQuestionAnswers(
        //LevelId
        val id: Int,
        val level: Int,
        @Relation(parentColumn = "id", entityColumn = "levelId")
        val questionAnswers: List<QuestionAnswer>
) :Parcelable{
}
