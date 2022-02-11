package ir.amozkade.advancedAsisstiveTouche.mvvm.themeManager.models

import com.fasterxml.jackson.annotation.JsonProperty

enum class DisplayType {
    @JsonProperty("0")
    Slider,
    @JsonProperty("1")
    List
}