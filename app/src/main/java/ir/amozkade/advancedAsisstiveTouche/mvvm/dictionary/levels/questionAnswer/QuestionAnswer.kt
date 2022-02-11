package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.levels.questionAnswer

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.fasterxml.jackson.annotation.JsonProperty
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.leitners.models.Leitner
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
@Entity(indices = [Index("leitnerId")] ,
        primaryKeys = [
            "question", "leitnerId"
        ],
        foreignKeys =
        [ForeignKey(
                entity = Leitner::class,
                parentColumns = ["id"],
                childColumns = ["leitnerId"],
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.CASCADE
        )]
)
class QuestionAnswer constructor(@JsonProperty("question") var question: String,
                                 @JsonProperty("answer") var answer: String?,
                                 @JsonProperty("passedTime") var passedTime: Date? = null,
                                 @JsonProperty("leitnerId") var leitnerId: Int,
                                 var levelId: Int,
                                 var completed:Boolean = false,
                                 var favorite:Boolean = false,
                                 var favoriteDate:Date? = null
) : Parcelable {


    companion object {
//        fun getDictFromJsonFile(cto: Context, application: Application) {
//            val stream = cto.resources.openRawResource(R.raw.five_hundred_and_four);
//            val questionAnswers = arrayListOf<QuestionAnswer>()
//            val tree = ObjectMapper().readTree(stream)
//            tree.forEach {
//                val mapper = ObjectMapper()
//                val row: Map<String, String> = mapper.convertValue(it, object : TypeReference<Map<String, String>>() {})
//                val en = row.keys.first()
//                val fa = row.values.first()
////                questionAnswers.add(QuestionAnswer(question = en, answer = fa, passedTime = null, levelId = 1 , leitner =   ))
//            }
//            QuestionAnswerRepository(application = application).insert(questionAnswers)
//        }
    }
}


