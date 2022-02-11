package ir.amozkade.advancedAsisstiveTouche.helper.bindings

import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import ir.amozkade.advancedAsisstiveTouche.R
import ir.mobitrain.applicationcore.helper.Converters
import ir.mobitrain.applicationcore.helper.Preference.getPreferenceLanguage
import java.text.DateFormat
import java.util.*

@BindingAdapter("setTextView")
fun setTextView(textView: TextView, text: String?) {
    text?.let { textView.text = it }
}

@BindingAdapter("setShortDateText")
fun setShortDateText(textView: TextView, date: Date?) {
    val newDate = date ?: return
    if (getPreferenceLanguage()?.contains("fa") == true) {
        textView.text = Converters.convertToShamsi(newDate)
    } else {
        val dateFormattedString = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT, Locale.getDefault()).format(newDate)
        textView.text = dateFormattedString
    }
}

@BindingAdapter("placeholderOfSpannable","countOfSpannable","spannableColor")
fun setSpannableWithVariableColorText(textView: TextView, placeholderOfSpannable:String= "",  countOfSpannable: Int , spannableColor:Int ) {
    val context = textView.context
    val spn = SpannableString("$placeholderOfSpannable $countOfSpannable")
    spn.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.white_darker_3X)), 0, placeholderOfSpannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    spn.setSpan(ForegroundColorSpan(spannableColor), placeholderOfSpannable.length + 1,placeholderOfSpannable.length  + 1  + countOfSpannable.toString().count(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    textView.text = spn
}

