package ir.amozkade.advancedAsisstiveTouche.mvvm.themeManager

import ir.amozkade.advancedAsisstiveTouche.AppDir
import ir.amozkade.advancedAsisstiveTouche.helper.api.DataState
import ir.amozkade.advancedAsisstiveTouche.mvvm.preferenceActivity.SettingRepository
import ir.amozkade.advancedAsisstiveTouche.mvvm.themeManager.di.ThemeDao
import ir.amozkade.advancedAsisstiveTouche.mvvm.themeManager.di.ThemeManagerRetrofit
import ir.amozkade.advancedAsisstiveTouche.mvvm.themeManager.models.Theme
import ir.amozkade.advancedAsisstiveTouche.mvvm.themeManager.models.ThemePack
import ir.amozkade.advancedAsisstiveTouche.mvvm.themeManager.utils.ThemeManagerResponse
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import javax.inject.Inject

class ThemeManagerRepository @Inject constructor(private val themeDao: ThemeDao,
                                                 @AppDir private val appDir: String,
                                                 private val settingRepository: SettingRepository,
                                                 private val retrofit: ThemeManagerRetrofit) {

    suspend fun getAllThemes(): Flow<DataState<ThemeManagerResponse>> = flow {
        emit(DataState.Loading)
        val themes = retrofit.getAll()
        val themePacks = retrofit.getAllThemePacks()
        emit(DataState.Success(ThemeManagerResponse.Themes(themePacks, themes)))
    }

    suspend fun downloadTheme(theme: Theme): Flow<DataState<ThemeManagerResponse>> = flow {

        val body = retrofit.download(theme.url).body()
        body?.byteStream()?.let {
            saveToDisk(it, theme.url.split("/").last())
            emit(DataState.Success(ThemeManagerResponse.SucceededDownloadTheme(theme)))
            saveToPreference(theme)
            themeDao.insert(theme)
        }
    }

    private fun saveToDisk(inp: InputStream, fileName:String) {
        try {
            val file = File(appDir, fileName)
            file.createNewFile()
            FileOutputStream(file).use { output ->
                inp.copyTo(output)
                output.flush()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        } finally {
            inp.close()
        }
    }

    private fun saveToPreference(item: Theme) {
        GlobalScope.launch(IO){
            when (item.themeType) {
                "Button" -> {
                    settingRepository.setUserSelectedImageName(item.url.split("/").last())
                }
                "Background" -> {
                    settingRepository.setUserSelectedPanelImageName( item.url.split("/").last())
                }
                "Font" -> {
                    settingRepository.setUserSelectedFontName( item.url.split("/").last())
                }
            }
        }
    }

    suspend fun downloadPack(themePack: ThemePack) = flow<DataState<ThemeManagerResponse>> {
        emit(DataState.BlockLoading)

//        settingRepository.setUserSelectedImageName(null)
//        settingRepository.setUserSelectedPanelImageName(null)
//        settingRepository.setUserSelectedFontName(null)

        themePack.backgroundUrl?.let { backgroundUrl ->
            val backgroundBody = retrofit.download(backgroundUrl).body()
            backgroundBody?.byteStream()?.let {
                saveToDisk(it, backgroundUrl.split("/").last())
                settingRepository.setUserSelectedPanelImageName(backgroundUrl.split("/").last())
            }
        }

        themePack.buttonUrl?.let { buttonUrl->
            val buttonBody = retrofit.download(buttonUrl).body()
            buttonBody?.byteStream()?.let {
                saveToDisk(it, buttonUrl.split("/").last())
                settingRepository.setUserSelectedImageName(buttonUrl.split("/").last())
            }
        }

        themePack.fontUrl?.let { fontUrl->
            val fontBody = retrofit.download(fontUrl).body()
            fontBody?.byteStream()?.let {
                saveToDisk(it, fontUrl.split("/").last())
                settingRepository.setUserSelectedFontName(fontUrl.split("/").last())
            }
        }

        themePack.buttonOverlayColor?.let {
            settingRepository.setButtonColorOverlay(java.lang.Long.parseLong(it.replace("#" , ""), 16).toInt())
        }
        themePack.backgroundOverlayColor?.let {
            settingRepository.setPanelColorOverlay(java.lang.Long.parseLong(it.replace("#" , ""), 16).toInt())
        }
        themePack.panelButtonsColor?.let {
            settingRepository.setPanelButtonsColor(java.lang.Long.parseLong(it.replace("#" , ""), 16).toInt())
        }
        emit(DataState.Success(ThemeManagerResponse.SucceededDownloadThemePack(themePack)))
    }
}