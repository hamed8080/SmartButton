package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.manageDictionaries

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import ir.mobitrain.applicationcore.helper.CommonHelpers
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
data class Dictionary @JsonCreator constructor(
        @PrimaryKey @JsonProperty("id") var id: Int,
        @JsonProperty("name") var name: String,
        @JsonProperty("fileName") var fileName: String,
        @JsonProperty("fileSize") var fileSize: Int,
        @JsonProperty("entryCount") var entryCount: Long

) : Parcelable {
    val fileSizeString: String
        get() {
            return CommonHelpers.getFileSizeStringWithType(fileSize)
        }
    val dbNameWithoutZipExtension:String
    get() {
        return  fileName.replace(".zip" , "")
    }
}