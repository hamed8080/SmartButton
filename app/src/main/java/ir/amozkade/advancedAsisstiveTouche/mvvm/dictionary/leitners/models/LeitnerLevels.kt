package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.leitners.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LeitnerLevels constructor(val id: Int,
                                     val name: String,
                                     val levelId: Int,
                                     val level: Int,
                                     var time: Int,
                                     val questionCountInLevel: Int,
                                     val questionReviewableCountInLevel: Int
) : Parcelable {
    val reviewableCount: Int
        get() {
//        ignore level 1 and user can open review
            return if (level == 1) questionCountInLevel else questionReviewableCountInLevel
        }

}