package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.review.models

import android.content.Context
import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import ir.amozkade.advancedAsisstiveTouche.R
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true)
data class WordDetail @JsonCreator constructor(@JsonProperty("numSyllables") var numSyllables: Int?,
                                               @JsonProperty("defs") var definitions: List<String>?,
                                               @JsonProperty("tags") var tags: List<String>?
) : Parcelable {

    fun getIpaFromTag(): String? {
        return tags?.firstOrNull { it.contains("ipa_pron:") }?.replace("ipa_pron:", "")
    }

    fun getPartOfSpeech(context: Context): String? {
        var partOfSpeech: String? = null
        tags?.filter { !it.contains("ipa_pron:") && !it.contains("pron:") }?.forEach {
            if (partOfSpeech == null) {
                partOfSpeech = ""
            }
            partOfSpeech += "${convertSpeechCharToString(it, context)} - "
        }
        partOfSpeech?.lastIndexOf("-")?.let {
            return partOfSpeech?.replaceRange(it, it + 1, "")
        }
        return partOfSpeech
    }

    private fun convertSpeechCharToString(char: String, context: Context): String {
        return when (char) {
            "n" -> context.getString(R.string.noun)
            "v" -> context.getString(R.string.verb)
            "adj" -> context.getString(R.string.adjective)
            "adv" -> context.getString(R.string.adverb)
            "u" -> context.getString(R.string.unknown)
            else -> context.getString(R.string.unknown)
        }
    }
}