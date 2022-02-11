package ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.models

import android.content.Context
import android.graphics.drawable.Drawable
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.buttons.AppButton
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.buttons.ContactButton
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.buttons.NoActionButton
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.delegates.AssistiveButtonDelegate


class ButtonModelInPanel(
        val titleId: String? = null,
        val contactName: String? = null,
        val unicodeIcon: String? = null,
        val needRoot: Boolean = false,
        val buttonTypeName: ButtonTypesName? = null,
        val button: AssistiveButtonDelegate? = null,
        val icon: Drawable? = null,
        val appName: String? = null,
        val packageName: String? = null,
        val imageUri: String? = null,
        val phoneNumber: String? = null,
        val orderPositionInPanel: Int? = null,
        val requiredApi: Int? = null
) {

    fun getTitleBaseOnButtonType(context: Context): String {
        val title = when (button) {
            is AppButton -> {
                appName
            }
            is ContactButton -> {
                contactName
            }
            is NoActionButton -> {
                ""
            }
            else -> {
                //this mean button is shortcut type
                val titleId = titleId ?: return ""
                getLocalizedStringForVariableName(titleId, context)
            }
        }
        return title ?: ""
    }

    override fun toString(): String {
        val className = button?.let { it::class.java.simpleName } ?: "null"
        return "title=${titleId},unicodeIcon=${unicodeIcon},needRoot=${needRoot},buttonTypeName=${buttonTypeName?.name},button=${className},icon=${icon},appName=${appName},packageName=${packageName},imageUri=${imageUri},phoneNumber=${phoneNumber},orderPositionInPanel=${orderPositionInPanel}"
    }

    enum class ButtonTypesName {
        HIDE_TO_NOTIFICATION,
        OPEN_WINDOW,
        VOLUME_UP,
        VOLUME_DOWN,
        WIFI,
        BLUETOOTH,
        ROTATE,
        MULTITASKING,
        HOME,
        BACK,
        LOCK,
        TORCH,
        VOLUME,
        BRIGHTNESS,
        SCREENSHOT,
        MUTE,
        SOUND,
        OPERATOR,
        GPS_SETTING,
        POWER,
        APP_DRAWER,
        NOTIFICATION,
        CONTACT,
        NO_ACTION,
        APP,
        REBOOT,
        CLIPBOARD,
        TRANSLATE,
        SCREEN_RECORDING
    }

    companion object {

        //must use string name instead of resource id because id generated and in each build changed so text goes incorrect
        fun getLocalizedStringForVariableName(stringName: String, context: Context): String {
            return context.resources.getString(context.resources.getIdentifier(stringName, "string", context.packageName))
        }
    }
}