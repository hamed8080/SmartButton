package ir.amozkade.advancedAsisstiveTouche.mvvm.user.loginWithSms.utils

sealed class LoginWithSMSStateEvent{
    data class RequestVerificationCode(val phoneNumber:String): LoginWithSMSStateEvent()
    data class VerifyCode(val phoneNumber: String,val vrfCode: String) : LoginWithSMSStateEvent()
}
