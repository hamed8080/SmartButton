package ir.amozkade.advancedAsisstiveTouche.mvvm.mainAssistiveTouch

import ir.amozkade.advancedAsisstiveTouche.AppDir
import ir.amozkade.advancedAsisstiveTouche.helper.api.DataState
import ir.amozkade.advancedAsisstiveTouche.helper.downloader.DownloadResponse
import ir.amozkade.advancedAsisstiveTouche.helper.downloader.Downloader
import ir.amozkade.advancedAsisstiveTouche.mvvm.mainAssistiveTouch.di.GoogleDriveModule
import ir.amozkade.advancedAsisstiveTouche.mvvm.mainAssistiveTouch.di.GoogleDriveRetrofit
import ir.amozkade.advancedAsisstiveTouche.mvvm.mainAssistiveTouch.models.DriveFile
import ir.amozkade.advancedAsisstiveTouche.mvvm.mainAssistiveTouch.models.GoogleDriveFilesResult
import ir.amozkade.advancedAsisstiveTouche.mvvm.preferenceActivity.SettingRepository
import ir.amozkade.advancedAsisstiveTouche.mvvm.user.di.ProfileRetrofit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.File
import java.lang.Exception
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.ceil

class GoogleDriveRepository @Inject constructor(
        private val downloader: Downloader,
        private val profileRetrofit: ProfileRetrofit,
        private val googleDriveRetrofit: GoogleDriveRetrofit,
        private val settingRepository: SettingRepository,
        @AppDir private val appDir: String
) {

    companion object {
        const val OK = 200
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun getAccessTokenFromServer(code: String?) {
        code?.let {
            val googleAccessTokenResponse = profileRetrofit.exchangeCode(code)
            googleAccessTokenResponse.refresh_token?.let { refreshToken ->
                settingRepository.setGoogleApiRefreshTokenKey(refreshToken)
            }
            settingRepository.setGoogleAccessToken(googleAccessTokenResponse.access_token)
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun search(fileNameWithExtension: String): GoogleDriveFilesResult {
        return googleDriveRetrofit.searchFile("name='${fileNameWithExtension}'" , getAuthorizationForHeader())
    }

    private fun getAuthorizationForHeader(): String? {
        settingRepository.getCashedModel().googleAccessTokenResponseKey?.let {
            return "Bearer $it"
        }
        return null
    }

    suspend fun uploadFile(file: File, mimeType: String? = null): Flow<GoogleDriveUploadState> = flow {
        emit(GoogleDriveUploadState.Uploading(progress = 0, fileSize = file.length().toInt(), callBackData = null))
        try {
            deleteBackupFileIfExist(file)
        }catch (e:HttpException){
            if(e.code() == 401){
                getAccessTokenWithRefreshToken().collect()
            }
        }
        if(getAuthorizationForHeader() == null){
            emit(GoogleDriveUploadState.SignOutFromGoogleSignInToGetNewToken)
            return@flow
        }
        getAuthorizationForHeader()?.let {
            val driveUploader = GoogleDriveUploader(it, file, mimeType , settingRepository)
            driveUploader.uploadFile().collect { uploadState ->
                emit(uploadState)
            }
        }
    }

    private suspend fun deleteBackupFileIfExist(file: File) {
        val findedFiles = search(file.name)
        findedFiles.files.forEach {
            googleDriveRetrofit.deleteFile(it.id, getAuthorizationForHeader())
        }
    }

    suspend fun getAccessTokenWithRefreshToken(): Flow<Boolean> = flow {
        if(settingRepository.getCashedModel().googleApiRefreshTokenKey == null ){
            emit(false)
            return@flow
        }
        settingRepository.getCashedModel().googleApiRefreshTokenKey?.let {
            try {
                val googleAccessTokenResponse = profileRetrofit.getAccessTokenWithRefreshToken(it)
                settingRepository.setGoogleAccessToken(googleAccessTokenResponse.access_token)
                emit(true)
            }catch (e:Exception){
                settingRepository.setGoogleAccessToken(null)
                emit(false)
                return@flow
            }
        }
    }

    suspend fun downloadFileWithName(fileName: String): Flow<GoogleDriveDownloadState> = flow {
        emit(GoogleDriveDownloadState.Downloading(progress = 0, fileSize = 0, callBackData = null))
        search(fileName).files.lastOrNull()?.let {driveFile->
            val url = "${GoogleDriveModule.baseUrl}files/${driveFile.id}?alt=media"
            val fileDetail = getFileDetail(driveFile.id) // get file size
            val saveFilePath = "${appDir}/${fileName}"
            downloader.download(url, saveFilePath, getAuthorizationForHeader(), null).collect { dataState ->
                if (dataState is DataState.Success && dataState.data is DownloadResponse.Downloading) {
                    //download progress in google drive cant be calculated so must be calculated here
                    val downloadedBytes = abs(dataState.data.progress / 100)
                    val progress = ceil(100 * downloadedBytes / (fileDetail.size?.toDouble() ?: 0.toDouble()) ).toInt()
                    emit(GoogleDriveDownloadState.Downloading(progress = progress , fileSize = fileDetail.size?.toInt() ?: 0 , callBackData = null ))
                }else if(dataState is DataState.Success && dataState.data is DownloadResponse.Finished){
                    emit(GoogleDriveDownloadState.Finished)
                }
            }
        }
    }

    private suspend fun getFileDetail(fileId:String):DriveFile{
        return googleDriveRetrofit.getFileDetail(fileId , "size,name,id,kind,mimeType" , getAuthorizationForHeader())
    }

}