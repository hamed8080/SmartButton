package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.leitners.models

import androidx.room.Relation
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.levels.questionAnswer.QuestionAnswer

data class LeitnerQuestions(
        val id: Int,
        val name: String,
        @Relation(parentColumn = "id", entityColumn = "leitnerId")
        val question: List<QuestionAnswer>
)