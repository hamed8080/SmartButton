package ir.amozkade.advancedAsisstiveTouche.mvvm.editButtonPositions

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.amozkade.advancedAsisstiveTouche.helper.api.DataState
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.models.ButtonInPreferenceModel
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.models.ButtonModelInPanel
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.models.DefaultButtons
import ir.amozkade.advancedAsisstiveTouche.mvvm.editButtonPositions.utils.EditPositionResponse
import ir.amozkade.advancedAsisstiveTouche.mvvm.preferenceActivity.SettingRepository
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class EditPositionRepository @Inject constructor(@ApplicationContext private val context: Context,
                                                 private val settingRepository: SettingRepository,
                                                 private val defaultButtons: DefaultButtons) {

    companion object{
        fun convertImageUriStringToDrawable(packageName: String? = null, imageUri: String? , context: Context): Drawable? {
            if (packageName != null) {
                val intent = Intent(Intent.ACTION_MAIN, null)
                intent.addCategory(Intent.CATEGORY_LAUNCHER)
                val allApps = context.packageManager.queryIntentActivities(intent, 0)
                val app = allApps.firstOrNull { it.activityInfo.packageName == packageName }
                        ?: return null
                return app.loadIcon(context.packageManager)
            }
            if (imageUri == null) return null
            val inputStream = context.contentResolver.openInputStream(Uri.parse(imageUri))
            return Drawable.createFromStream(inputStream, imageUri)
        }

        fun convertPreferenceToSmartButton(context: Context , settingRepository: SettingRepository): List<ButtonModelInPanel> {
            val prefButtons = settingRepository.getCashedModel().buttons ?: return arrayListOf()
            val prefButtonList = ObjectMapper().readValue<List<ButtonInPreferenceModel>>(prefButtons)
            return prefButtonList.map {
                ButtonInPreferenceModel.convertPreferenceToButtonInPanel(it, context)
            }
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    fun savePositions(buttons: List<ButtonModelInPanel>)= flow<DataState<EditPositionResponse>> {
        val preferenceButtons = buttons.map {
            ButtonInPreferenceModel.convertButtonModelInPanelToPreference(it)
        }
        val buttonsInPreference = ObjectMapper().writeValueAsString(preferenceButtons)
        settingRepository.setButtons(buttonsInPreference)
        emit(DataState.Success(EditPositionResponse.Saved))
    }

    fun init() = flow<DataState<EditPositionResponse>> {

        if (settingRepository.getCashedModel().buttons != null) {
            val buttons = convertPreferenceToSmartButton(context,settingRepository)
            emit(DataState.Success(EditPositionResponse.AllButtons(buttons)))
        } else {
            val list = defaultButtons.getDefaultButtons().filter {
                it.buttonTypeName != ButtonModelInPanel.ButtonTypesName.HIDE_TO_NOTIFICATION && it.buttonTypeName != ButtonModelInPanel.ButtonTypesName.OPEN_WINDOW
            }.take(9)
            emit(DataState.Success(EditPositionResponse.AllButtons(list)))
        }
    }
}