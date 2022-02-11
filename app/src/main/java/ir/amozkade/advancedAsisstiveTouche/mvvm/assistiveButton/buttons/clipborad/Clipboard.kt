package ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.buttons.clipborad

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
@Entity
data class Clipboard constructor(@PrimaryKey(autoGenerate = true) @JsonProperty("id") val id: Int = 0,
                                 @JsonProperty("date") var date: Date? = null,
                                 @JsonProperty("text") var text: String? = null


) : Parcelable {
}