package ir.amozkade.advancedAsisstiveTouche.mvvm.user

import android.content.Context
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.Credentials
import com.google.android.gms.auth.api.credentials.CredentialsOptions
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.amozkade.advancedAsisstiveTouche.helper.api.DataState
import ir.amozkade.advancedAsisstiveTouche.mvvm.preferenceActivity.SettingRepository
import ir.amozkade.advancedAsisstiveTouche.mvvm.user.di.ProfileRetrofit
import ir.amozkade.advancedAsisstiveTouche.mvvm.user.forgotPassword.VerifyNewPasswordReq
import ir.amozkade.advancedAsisstiveTouche.mvvm.user.forgotPassword.utils.ForgotPasswordResponse
import ir.amozkade.advancedAsisstiveTouche.mvvm.user.login.models.*
import ir.amozkade.advancedAsisstiveTouche.mvvm.user.login.utils.LoginResponse
import ir.amozkade.advancedAsisstiveTouche.mvvm.user.loginWithSms.utils.LoginWithSMSResponse
import ir.amozkade.advancedAsisstiveTouche.mvvm.user.profile.ProfileRepository
import ir.amozkade.advancedAsisstiveTouche.mvvm.user.profile.editProfile.EditUserDetailReq
import ir.amozkade.advancedAsisstiveTouche.mvvm.user.profile.editProfile.utils.EditProfileResponse
import ir.amozkade.advancedAsisstiveTouche.mvvm.user.profile.resetPassword.PasswordResetReq
import ir.amozkade.advancedAsisstiveTouche.mvvm.user.profile.resetPassword.utils.ResetPasswordResponse
import ir.amozkade.advancedAsisstiveTouche.mvvm.user.register.utils.RegisterResponse
import ir.mobitrain.applicationcore.api.JWT
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class UserRepository @Inject constructor(
        private val retrofit: ProfileRetrofit,
        @ApplicationContext private val context: Context,
        private val settingRepository: SettingRepository,
        private val profileRepository: ProfileRepository
) {

    suspend fun doLogin(email: String, password: String): Flow<DataState<LoginResponse>> = flow {
        val user = User(email = email, passwordHash = password)
        emit(DataState.Loading)
        val profileWithJwt = retrofit.login(user)
        saveProfileAndJwt(profileWithJwt)
        registerTokenToServer()
    }

    suspend fun getTokenFromGoogle(accessToken: String?) {
        accessToken?.let {
            val validateTokenReq = ValidateTokenReq(tokenProvider = "G", accessToken = accessToken)
            val profileWithJwt = retrofit.validateToken(validateTokenReq)
            saveProfileAndJwt(profileWithJwt)
            registerTokenToServer()
        }
    }

    suspend fun registerTokenToServer() {
        if (settingRepository.getCashedModel().newFirebaseTokenSynced) return
        if (JWT.instance.computedJWT == null) return
        settingRepository.getCashedModel().firebaseToken?.let { token ->
            retrofit.setDeviceToken(token, JWT.instance.getPayload<Payload>()?.userId, 1)
            settingRepository.setNewFirebaseTokenSync(true)
        }
    }

    private fun saveProfileAndJwt(profileWithJwt: ProfileWithJwt?) {
        profileWithJwt?.let {
            JWT.instance.setJWT(it.jwt.replace("\"", ""))
            GlobalScope.launch (IO){
                profileRepository.saveProfile(profileWithJwt.profile)
            }
        }
    }

    suspend fun doRegister(email: String, password: String): Flow<DataState<RegisterResponse>> = flow {
        emit(DataState.Loading)
        val user = User(email = email, userName = email, passwordHash = password)
        val profileWithJwt = retrofit.register(user)
        saveProfileAndJwt(profileWithJwt)
        emit(DataState.Success(RegisterResponse.Registered))
    }

    suspend fun requestVerificationCode(phoneNumber: String): Flow<DataState<LoginWithSMSResponse>> = flow {
        emit(DataState.Loading)
        retrofit.requestVerificationCode(phoneNumber)
        emit(DataState.Success(LoginWithSMSResponse.RequestedVerificationCode))
    }

    suspend fun verifyWithServer(phoneNumber: String, verificationCode: String): Flow<DataState<LoginWithSMSResponse>> = flow {
        emit(DataState.Loading)
        retrofit.verifyWithServer(phoneNumber, verificationCode)?.let {
            saveProfileAndJwt(it)
            registerTokenToServer()
            emit(DataState.Success(LoginWithSMSResponse.VerifiedSMSCode))
            return@flow
        }
        emit(DataState.Success(LoginWithSMSResponse.FailedVerifySMSCode))
    }

    fun resetPassword(resetPasswordReq: PasswordResetReq) = flow<DataState<ResetPasswordResponse>> {
        emit(DataState.BlockLoading)
        val profileWithJwt = retrofit.resetPassword(resetPasswordReq)
        profileWithJwt?.let {
            saveProfileAndJwt(profileWithJwt)
            val credential = Credential
                    .Builder(profileRepository.getProfile()?.email ?: "")
                    .setPassword(resetPasswordReq.newPassword)
                    .build()
            val options = CredentialsOptions.Builder()
                    .forceEnableSaveDialog()
                    .build()
            val mCredentialsClient = Credentials.getClient(context, options)
            mCredentialsClient.save(credential)
            emit(DataState.Success(ResetPasswordResponse.SuccessReset))
        }
    }

    suspend fun doEditProfile(editUserDetailReq: EditUserDetailReq) = flow<DataState<EditProfileResponse>> {
        emit(DataState.BlockLoading)
        val profileWithJwt = retrofit.editProfile(editUserDetailReq)
        profileWithJwt?.let {
            saveProfileAndJwt(it)
            emit(DataState.Success(EditProfileResponse.SuccessEdited))
        }
    }

    suspend fun uploadProfileImage(profileBase64StrImage: String) = flow<DataState<EditProfileResponse>> {
        val profileWithJwt = retrofit.uploadProfileImage(profileBase64StrImage, JWT.instance.getPayload<Payload>()?.userId)
        profileWithJwt?.let {
            saveProfileAndJwt(it)
            emit(DataState.Success(EditProfileResponse.SuccessUploadedImage))
        }
    }

    suspend fun requestResetCode(email: String) = flow<DataState<ForgotPasswordResponse>> {
        emit(DataState.BlockLoading)
        val sendRequestSuccessFully = retrofit.requestCode(email)
        emit(DataState.Success(ForgotPasswordResponse.SendRequestSuccessFully(sendRequestSuccessFully)))
    }

    suspend fun resetPassword(email: String, newPassword: String, code: Int) = flow<DataState<ForgotPasswordResponse>>{
        emit(DataState.BlockLoading)
        val isVerified = retrofit.resetPassword(VerifyNewPasswordReq(email = email, newPassword = newPassword, code = code))
        emit(DataState.Success(ForgotPasswordResponse.VerifyResult(isVerified)))
    }
}