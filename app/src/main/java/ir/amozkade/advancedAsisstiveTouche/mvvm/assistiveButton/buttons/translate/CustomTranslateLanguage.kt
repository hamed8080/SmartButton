package ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.buttons.translate

import java.util.*

class CustomTranslateLanguage(val code: String): Comparable<CustomTranslateLanguage> {
    private val displayName: String
        get() = Locale(code).displayName

    override fun equals(other: Any?): Boolean {
        if (other === this) {
            return true
        }

        if (other !is CustomTranslateLanguage) {
            return false
        }

        val otherLang = other as CustomTranslateLanguage?
        return otherLang!!.code == code
    }

    override fun toString(): String {
        return "$code - $displayName"
    }

    override fun compareTo(other: CustomTranslateLanguage): Int {
        return this.displayName.compareTo(other.displayName)
    }

    override fun hashCode(): Int {
        return code.hashCode()
    }
}