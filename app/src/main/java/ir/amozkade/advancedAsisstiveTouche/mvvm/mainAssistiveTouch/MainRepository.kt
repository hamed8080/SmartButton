package ir.amozkade.advancedAsisstiveTouche.mvvm.mainAssistiveTouch

import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject

class MainRepository @Inject constructor(private val firebaseAnalytics: FirebaseAnalytics) {


    fun logActivity(activityName: String) {
        firebaseAnalytics.logEvent(activityName , null)
    }

}