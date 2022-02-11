package ir.amozkade.advancedAsisstiveTouche.helper

import android.content.Context
import android.content.Intent
import ir.amozkade.advancedAsisstiveTouche.mvvm.user.profile.ProfileActivity

class RedirectToFactory {
    companion object {
        fun getIntent(to: String? = null ,cto:Context?): Intent? {
            return when (to) {
                null -> null
                "Profile" ->{
                    return Intent(cto , ProfileActivity::class.java)
                }
                else -> null
            }
        }
    }
}