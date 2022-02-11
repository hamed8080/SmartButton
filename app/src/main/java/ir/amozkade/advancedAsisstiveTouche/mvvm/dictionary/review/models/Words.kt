package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.review.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(indices = [Index("word",name = "ix_word_w")])
@Parcelize
class Words(@PrimaryKey val id:Int , val word:String? , val mean:ByteArray? ):Parcelable