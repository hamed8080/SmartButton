package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.levels.models

import android.content.Context
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import ir.amozkade.advancedAsisstiveTouche.R
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
class Level constructor(@PrimaryKey(autoGenerate = true) val id: Int, val level: Int, var time: Int = 2, val leitnerId: Int) : Parcelable {

    companion object {
        @JvmStatic
        fun getLocalizedLevelName(cto: Context, levelValue: Int): String {
            return String.format(cto.getString(R.string.level_name), levelValue.toString())
        }
    }

}

