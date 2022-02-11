package ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.models

import android.os.Build
import javax.inject.Inject

class DefaultButtons  @Inject constructor(private val buttonFactory: ButtonFactory){
    fun getDefaultButtons(): ArrayList<ButtonModelInPanel> {
        val list = arrayListOf<ButtonModelInPanel>()
        list.add(ButtonModelInPanel("open_window", unicodeIcon = "\uE000", buttonTypeName = ButtonModelInPanel.ButtonTypesName.OPEN_WINDOW, button = buttonFactory.create(ButtonModelInPanel.ButtonTypesName.OPEN_WINDOW)))
        list.add(ButtonModelInPanel("hide", unicodeIcon = "\uE001", buttonTypeName = ButtonModelInPanel.ButtonTypesName.HIDE_TO_NOTIFICATION, button = buttonFactory.create(ButtonModelInPanel.ButtonTypesName.HIDE_TO_NOTIFICATION)))
        list.add(ButtonModelInPanel("volume_down", unicodeIcon = "\uE002", buttonTypeName = ButtonModelInPanel.ButtonTypesName.VOLUME_DOWN, button = buttonFactory.create(ButtonModelInPanel.ButtonTypesName.VOLUME_DOWN)))
        list.add(ButtonModelInPanel("volume_up", unicodeIcon = "\uE003", buttonTypeName = ButtonModelInPanel.ButtonTypesName.VOLUME_UP, button = buttonFactory.create(ButtonModelInPanel.ButtonTypesName.VOLUME_UP)))
        list.add(ButtonModelInPanel("lock", unicodeIcon = "\uE010", buttonTypeName = ButtonModelInPanel.ButtonTypesName.LOCK, button = buttonFactory.create(ButtonModelInPanel.ButtonTypesName.LOCK)))
        list.add(ButtonModelInPanel("mute", unicodeIcon = "\uE020", buttonTypeName = ButtonModelInPanel.ButtonTypesName.MUTE, button = buttonFactory.create(ButtonModelInPanel.ButtonTypesName.MUTE)))
        list.add(ButtonModelInPanel("wifi", unicodeIcon = "\uE004", buttonTypeName = ButtonModelInPanel.ButtonTypesName.WIFI, button = buttonFactory.create(ButtonModelInPanel.ButtonTypesName.WIFI)))
        list.add(ButtonModelInPanel("torch", unicodeIcon = "\uE011", buttonTypeName = ButtonModelInPanel.ButtonTypesName.TORCH, button = buttonFactory.create(ButtonModelInPanel.ButtonTypesName.TORCH)))
        list.add(ButtonModelInPanel("home", unicodeIcon = "\uE008", buttonTypeName = ButtonModelInPanel.ButtonTypesName.HOME, button = buttonFactory.create(ButtonModelInPanel.ButtonTypesName.HOME)))
        list.add(ButtonModelInPanel("multitasking", unicodeIcon = "\uE007", buttonTypeName = ButtonModelInPanel.ButtonTypesName.MULTITASKING, button = buttonFactory.create(ButtonModelInPanel.ButtonTypesName.MULTITASKING)))
        list.add(ButtonModelInPanel("bluetooth", unicodeIcon = "\uE005", buttonTypeName = ButtonModelInPanel.ButtonTypesName.BLUETOOTH, button = buttonFactory.create(ButtonModelInPanel.ButtonTypesName.BLUETOOTH)))
        list.add(ButtonModelInPanel("brightness", unicodeIcon = "\uE013", buttonTypeName = ButtonModelInPanel.ButtonTypesName.BRIGHTNESS, button = buttonFactory.create(ButtonModelInPanel.ButtonTypesName.BRIGHTNESS)))
        list.add(ButtonModelInPanel("screenshot", unicodeIcon = "\uE014", buttonTypeName = ButtonModelInPanel.ButtonTypesName.SCREENSHOT, button = buttonFactory.create(ButtonModelInPanel.ButtonTypesName.SCREENSHOT), requiredApi = Build.VERSION_CODES.LOLLIPOP))
        list.add(ButtonModelInPanel("lock_rotate", unicodeIcon = "\uE006", buttonTypeName = ButtonModelInPanel.ButtonTypesName.ROTATE, button = buttonFactory.create(ButtonModelInPanel.ButtonTypesName.ROTATE)))
        list.add(ButtonModelInPanel("back", unicodeIcon = "\uE009", buttonTypeName = ButtonModelInPanel.ButtonTypesName.BACK, button = buttonFactory.create(ButtonModelInPanel.ButtonTypesName.BACK)))
        list.add(ButtonModelInPanel("volume", unicodeIcon = "\uE012", buttonTypeName = ButtonModelInPanel.ButtonTypesName.VOLUME, button = buttonFactory.create(ButtonModelInPanel.ButtonTypesName.VOLUME)))
        list.add(ButtonModelInPanel("sound_setting", unicodeIcon = "\uE015", buttonTypeName = ButtonModelInPanel.ButtonTypesName.SOUND, button = buttonFactory.create(ButtonModelInPanel.ButtonTypesName.SOUND)))
        list.add(ButtonModelInPanel("operator_setting", unicodeIcon = "\uE016", buttonTypeName = ButtonModelInPanel.ButtonTypesName.OPERATOR, button = buttonFactory.create(ButtonModelInPanel.ButtonTypesName.OPERATOR)))
        list.add(ButtonModelInPanel("gps_setting", unicodeIcon = "\uE017", buttonTypeName = ButtonModelInPanel.ButtonTypesName.GPS_SETTING, button = buttonFactory.create(ButtonModelInPanel.ButtonTypesName.GPS_SETTING)))
        list.add(ButtonModelInPanel("power", unicodeIcon = "\uE018", buttonTypeName = ButtonModelInPanel.ButtonTypesName.POWER, button = buttonFactory.create(ButtonModelInPanel.ButtonTypesName.POWER), requiredApi = Build.VERSION_CODES.LOLLIPOP))
        list.add(ButtonModelInPanel("apps", unicodeIcon = "\uE019", buttonTypeName = ButtonModelInPanel.ButtonTypesName.APP_DRAWER, button = buttonFactory.create(ButtonModelInPanel.ButtonTypesName.APP_DRAWER)))
        list.add(ButtonModelInPanel("notification", unicodeIcon = "\uE021", buttonTypeName = ButtonModelInPanel.ButtonTypesName.NOTIFICATION, button =buttonFactory.create(ButtonModelInPanel.ButtonTypesName.NOTIFICATION)))
        list.add(ButtonModelInPanel("reboot", unicodeIcon = "\uE022", buttonTypeName = ButtonModelInPanel.ButtonTypesName.REBOOT, button = buttonFactory.create(ButtonModelInPanel.ButtonTypesName.REBOOT), needRoot = true))
        list.add(ButtonModelInPanel("clipboard", unicodeIcon = "\uE023", buttonTypeName = ButtonModelInPanel.ButtonTypesName.CLIPBOARD, button = buttonFactory.create(ButtonModelInPanel.ButtonTypesName.CLIPBOARD)))
        list.add(ButtonModelInPanel("translator", unicodeIcon = "\uE024", buttonTypeName = ButtonModelInPanel.ButtonTypesName.TRANSLATE, button = buttonFactory.create(ButtonModelInPanel.ButtonTypesName.TRANSLATE)))
        list.add(ButtonModelInPanel("screen_recording", unicodeIcon = "\uE025", buttonTypeName = ButtonModelInPanel.ButtonTypesName.SCREEN_RECORDING, button = buttonFactory.create(ButtonModelInPanel.ButtonTypesName.SCREEN_RECORDING), requiredApi = Build.VERSION_CODES.LOLLIPOP))
        return list
    }
}