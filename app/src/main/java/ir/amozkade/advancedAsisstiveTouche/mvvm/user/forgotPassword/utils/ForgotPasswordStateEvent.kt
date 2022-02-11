package ir.amozkade.advancedAsisstiveTouche.mvvm.user.forgotPassword.utils

sealed class ForgotPasswordStateEvent{
    data class RequestReset(val email:String):ForgotPasswordStateEvent()
    data class VerifyResetPassword(val email: String , val newPassword:String ,val code:Int):ForgotPasswordStateEvent()
}
