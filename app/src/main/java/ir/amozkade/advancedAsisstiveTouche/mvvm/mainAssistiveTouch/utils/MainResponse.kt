package ir.amozkade.advancedAsisstiveTouche.mvvm.mainAssistiveTouch.utils

import android.net.Uri
import com.google.android.material.button.MaterialButton

sealed class MainResponse {
    data class ShowReviewAlert(val btn: MaterialButton) : MainResponse()
    data class ExportSucceeded(val exportPath: Uri) : MainResponse()
    object Imported : MainResponse()
    object SignOutGoogle:MainResponse()
}
