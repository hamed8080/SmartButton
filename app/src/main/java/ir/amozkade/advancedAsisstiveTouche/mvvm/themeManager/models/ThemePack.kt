package ir.amozkade.advancedAsisstiveTouche.mvvm.themeManager.models

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize


@JsonIgnoreProperties(ignoreUnknown = true)
@Parcelize
class ThemePack @JsonCreator constructor(
        @JsonProperty("title") val title: String?,
        @JsonProperty("titleFa") val titleFa: String?,
        @JsonProperty("thumbnailUrl") val thumbnailUrl: String?,
        @JsonProperty("buttonUrl") val buttonUrl: String?,
        @JsonProperty("backgroundUrl") val backgroundUrl: String?,
        @JsonProperty("fontUrl") val fontUrl: String?,
        @JsonProperty("isFree") val isFree: Boolean?,
        @JsonProperty("buttonOverlayColor") val buttonOverlayColor: String?,
        @JsonProperty("backgroundOverlayColor") val backgroundOverlayColor: String?,
        @JsonProperty("panelButtonsColor") val panelButtonsColor: String?
) : Parcelable