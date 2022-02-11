package ir.amozkade.advancedAsisstiveTouche.mvvm.user.forgotPassword.utils

sealed class ForgotPasswordResponse {
    data class SendRequestSuccessFully(val sendRequestSuccessFully: Boolean) : ForgotPasswordResponse()
    data class VerifyResult(val isVerified: Boolean) : ForgotPasswordResponse()
}
