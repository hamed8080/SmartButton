package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.leitners.models

import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize

@JsonIgnoreProperties(ignoreUnknown = true)
@Parcelize
@Entity
data class Leitner @JsonCreator constructor(
        @PrimaryKey(autoGenerate = true) @JsonProperty("id") var id: Int,
        @JsonProperty("name") var name: String,
        @JsonProperty("isBackToTopEnable") var isBackToTopEnable: Boolean = true,
        @JsonProperty("showDefinition") var showDefinition: Boolean = true
) : BaseObservable(), Parcelable {

    override fun toString(): String {
        return name
    }
}