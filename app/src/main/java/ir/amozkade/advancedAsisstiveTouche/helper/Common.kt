package ir.amozkade.advancedAsisstiveTouche.helper

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import androidx.room.Room
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.amozkade.advancedAsisstiveTouche.AppDatabase
import ir.amozkade.advancedAsisstiveTouche.helper.api.DataState
import ir.amozkade.advancedAsisstiveTouche.mvvm.mainAssistiveTouch.utils.MainResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.io.*
import java.lang.Exception
import java.util.regex.Pattern
import javax.inject.Inject

class Common @Inject constructor(@ApplicationContext private val context: Context, private val database: AppDatabase) {

    companion object {
        const val TEMP_AUTOMATIC_BACKUP_FILE_NAME = "temp_smart_button_automatic_backup.db"
        const val AUTOMATIC_BACKUP_FILE_NAME = "smart_button_automatic_backup.db"

        fun isContainPersian(value:String):Boolean{
            return Regex("^[\\u0600-\\u06FF\\uFB8A\\u067E\\u0686\\u06AF\\u200C\\u200F ]+\$").matches(value)
        }
    }

    private fun writeInputToOutputStream(inputStream: InputStream, outputStream: OutputStream) {
        val buffer = ByteArray(1024)
        var len: Int
        var downloadedSize = 0
        while (inputStream.read(buffer).also { len = it } >= 0) { //this must >=0 because input stream ended with index 0
            outputStream.write(buffer, 0, len)
            downloadedSize += len
        }
        outputStream.flush()
        outputStream.close()
    }

    suspend fun exportDatabaseToPath(documentFile: DocumentFile): Flow<DataState<MainResponse>> = flow {

        val existFile = documentFile.findFile(AppDatabase.DATABASE_NAME)?.exists()
        if (existFile == true) {
            documentFile.findFile(AppDatabase.DATABASE_NAME)?.delete()
        }
        val dbFileDocument = documentFile.createFile(".db", AppDatabase.DATABASE_NAME)
                ?: return@flow
        val otp = dbFileOutputStream(dbFileDocument.uri) ?: return@flow
        emit(DataState.BlockLoading)


        val dbPath = database.openHelper.writableDatabase.path
        database.close()
        val currentDBFile = File(dbPath)
        if (currentDBFile.exists()) {
            val inp = fileInputStream(currentDBFile)
            writeInputToOutputStream(inp, otp)
        }
        emit(DataState.Success(MainResponse.ExportSucceeded(dbFileDocument.uri)))
    }

    suspend fun importDatabaseToPath(documentFile: DocumentFile): Flow<DataState<MainResponse>> = flow {
        emit(DataState.BlockLoading)
        val inp = dbFileInputStream(documentFile.uri) ?: return@flow
        val roomPath = database.openHelper.writableDatabase.path
        val otp = fileOutputStream(roomPath)
        database.close()
        writeInputToOutputStream(inp, otp)
        Room.databaseBuilder(
                        context,
                        AppDatabase::class.java,
                        AppDatabase.DATABASE_NAME)
                .addMigrations(*AppDatabase.migrations)
                .build()
        withContext(IO) {
            emit(DataState.Success(MainResponse.Imported))
        }
    }

    private fun dbFileOutputStream(uri: Uri): OutputStream? {
        return context.contentResolver.openOutputStream(uri)
    }

    private fun dbFileInputStream(uri: Uri): InputStream? {
        return context.contentResolver.openInputStream(uri)
    }

    fun fileOutputStream(path: String): FileOutputStream {
        return FileOutputStream(path)
    }

    private fun fileInputStream(file: File): FileInputStream {
        return FileInputStream(file)
    }

    fun backupExportDatabaseToPath() {
        val database = Room
                .databaseBuilder(context,AppDatabase::class.java,AppDatabase.DATABASE_NAME)
                .addMigrations(*AppDatabase.migrations)
                .build()
        val dir = context.getExternalFilesDir(null)?.absolutePath + "/"
        val pathOfFile = dir + AUTOMATIC_BACKUP_FILE_NAME
        val backupFile = File(pathOfFile)
        val backupTempFile = File(dir + TEMP_AUTOMATIC_BACKUP_FILE_NAME)
        try {
            if (backupFile.exists()) {
                backupFile.renameTo(backupTempFile)
            }else{
                backupFile.createNewFile()
            }

            val uri = Uri.fromFile(backupFile)
            val otp = dbFileOutputStream(uri) ?: return


            val dbPath = database.openHelper.writableDatabase.path
            database.close()
            val currentDBFile = File(dbPath)
            if (currentDBFile.exists()) {
                val inp = fileInputStream(currentDBFile)
                writeInputToOutputStream(inp, otp)
            }
        }
        catch (e:Exception){
            e.printStackTrace()
        }
        finally {
            if (backupTempFile.exists()) {
                backupTempFile.renameTo(backupFile)
            }
        }
    }

    fun getFileFromExportedDatabase():File?{
        val dbPath = database.openHelper.writableDatabase.path
        val dbFile =  File(dbPath)
        return if(dbFile.exists()){
            dbFile
        }else{
            null
        }
    }
}