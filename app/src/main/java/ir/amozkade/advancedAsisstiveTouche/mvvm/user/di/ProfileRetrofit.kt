package ir.amozkade.advancedAsisstiveTouche.mvvm.user.di

import ir.amozkade.advancedAsisstiveTouche.mvvm.user.forgotPassword.VerifyNewPasswordReq
import ir.amozkade.advancedAsisstiveTouche.mvvm.user.login.models.GoogleAccessTokenResponse
import ir.amozkade.advancedAsisstiveTouche.mvvm.user.login.models.ProfileWithJwt
import ir.amozkade.advancedAsisstiveTouche.mvvm.user.login.models.User
import ir.amozkade.advancedAsisstiveTouche.mvvm.user.login.models.ValidateTokenReq
import ir.amozkade.advancedAsisstiveTouche.mvvm.user.profile.editProfile.EditUserDetailReq
import ir.amozkade.advancedAsisstiveTouche.mvvm.user.profile.resetPassword.PasswordResetReq
import retrofit2.http.*

interface ProfileRetrofit {
    @POST("Authentication/Login")
    suspend fun login(@Body user: User): ProfileWithJwt

    @POST("FCM/RegisterFirebaseToken")
    suspend fun setDeviceToken(@Query("token") token: String, @Query("userId") userId: String?, @Query("devicetype") devicetype: Int): Void

    @POST("Authentication/ValidateTokenProvider")
    suspend fun validateToken(@Body validateTokenReq: ValidateTokenReq): ProfileWithJwt

    @POST("Authentication/Register")
    suspend fun register(@Body user: User): ProfileWithJwt

    @PUT("Authentication/RVC/{pn}")
    suspend fun requestVerificationCode(@Path("pn") phoneNumber: String)

    @GET("Authentication/VRF")
    suspend fun verifyWithServer(@Query("pn") phoneNumber: String, @Query("code") code: String): ProfileWithJwt?

    @POST("Authentication/ResetPassword")
    suspend fun resetPassword(@Body reset: PasswordResetReq): ProfileWithJwt?

    @POST("Authentication/EditUserDetail")
    suspend fun editProfile(@Body editUserDetailReq: EditUserDetailReq): ProfileWithJwt?

    @POST("Authentication/UploadProfileImage")
    @FormUrlEncoded
    suspend fun uploadProfileImage(@Field("base64Image") base64Image: String, @Query("userId") userId: String?): ProfileWithJwt?

    @GET("Authentication/ForgotPasswordRequest")
    suspend fun requestCode(@Query("email") email: String): Boolean

    @POST("Authentication/ResetPasswordWithCode")
    suspend fun resetPassword(@Body req: VerifyNewPasswordReq): Boolean

    @GET("Authentication/ExchangeCodeToGetAccessToken")
    suspend fun exchangeCode(@Query("code") code: String): GoogleAccessTokenResponse

    @GET("Authentication/GetAccessTokenWithRefreshToken")
    suspend fun getAccessTokenWithRefreshToken(@Query("refreshToken") refreshToken: String): GoogleAccessTokenResponse

}