package ir.amozkade.advancedAsisstiveTouche.mvvm.user.loginWithSms.utils


sealed class LoginWithSMSResponse{
    object RequestedVerificationCode: LoginWithSMSResponse()
    object VerifiedSMSCode:LoginWithSMSResponse()
    object FailedVerifySMSCode:LoginWithSMSResponse()
}