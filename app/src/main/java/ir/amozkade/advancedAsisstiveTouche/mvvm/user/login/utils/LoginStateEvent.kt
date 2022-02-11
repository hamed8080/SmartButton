package ir.amozkade.advancedAsisstiveTouche.mvvm.user.login.utils


sealed class LoginStateEvent {
    object DoLogin : LoginStateEvent()
    data class GoogleLogin(val token:String?):LoginStateEvent()
}