package ir.amozkade.advancedAsisstiveTouche.mvvm.mainAssistiveTouch.di

import ir.amozkade.advancedAsisstiveTouche.mvvm.mainAssistiveTouch.models.DriveFile
import ir.amozkade.advancedAsisstiveTouche.mvvm.mainAssistiveTouch.models.GoogleDriveFilesResult
import retrofit2.Response
import retrofit2.http.*

interface GoogleDriveRetrofit {
    @GET("files")
    suspend fun searchFile(@Query("q") fileNameWithExtension: String, @Header("Authorization") authorization: String?): GoogleDriveFilesResult

    // retrofit has issue with 204 response code so need to use this Response<Unit>
    @DELETE("files/{fileId}")
    suspend fun deleteFile(@Path("fileId") fileId: String, @Header("Authorization") authorization: String?): Response<Unit>

    @GET("files/{fileId}")
    suspend fun getFileDetail(@Path("fileId")  fileId: String , @Query("fields") fields:String, @Header("Authorization") authorization: String?): DriveFile

}