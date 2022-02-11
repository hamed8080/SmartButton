package ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.models

import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.buttons.*
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.buttons.clipborad.ClipboardButton
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.buttons.screenRecorder.ScreenRecordingButton
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.buttons.translate.TranslateButton
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.delegates.AssistiveButtonDelegate
import javax.inject.Inject


class ButtonFactory @Inject constructor() {

    @Inject
    lateinit var clipboardButton: ClipboardButton

    @Inject
    lateinit var translateButton: TranslateButton

    @Inject
    lateinit var homeButton: HomeButton

    @Inject
    lateinit var volumeButton: VolumeButton

    @Inject
    lateinit var backButton: BackButton

    @Inject
    lateinit var bluetoothButton: BluetoothButton

    @Inject
    lateinit var hideButton: HideButton

    @Inject
    lateinit var openWindowButton: OpenWindowButton

    @Inject
    lateinit var wifiButton: WifiButton

    @Inject
    lateinit var volumeUpButton: VolumeUpButton

    @Inject
    lateinit var volumeDownButton: VolumeDownButton

    @Inject
    lateinit var lockButton: LockButton

    @Inject
    lateinit var torchButton: TorchButton

    @Inject
    lateinit var multitaskingButton: MultitaskingButton

    @Inject
    lateinit var rotateButton: RotateButton

    @Inject
    lateinit var screenshotButton: ScreenshotButton

    @Inject
    lateinit var appButton: AppButton

    @Inject
    lateinit var rebootButton: RebootButton

    @Inject
    lateinit var contactButton: ContactButton

    @Inject
    lateinit var noActionButton: NoActionButton

    @Inject
    lateinit var appDrawerButton: AppDrawerButton

    @Inject
    lateinit var muteButton: MuteButton

    @Inject
    lateinit var brightnessButton: BrightnessButton

    @Inject
    lateinit var soundSettingButton: SoundSettingButton

    @Inject
    lateinit var operatorButton: OperatorButton

    @Inject
    lateinit var gpsButton: GpsButton

    @Inject
    lateinit var powerButton: PowerButton

    @Inject
    lateinit var notificationButton: NotificationButton

    @Inject
    lateinit var screenRecordingButton: ScreenRecordingButton

    fun create(action: ButtonModelInPanel.ButtonTypesName): AssistiveButtonDelegate {

        return when (action) {
            ButtonModelInPanel.ButtonTypesName.OPEN_WINDOW -> openWindowButton
            ButtonModelInPanel.ButtonTypesName.HIDE_TO_NOTIFICATION -> hideButton
            ButtonModelInPanel.ButtonTypesName.VOLUME_UP -> volumeUpButton
            ButtonModelInPanel.ButtonTypesName.VOLUME_DOWN -> volumeDownButton
            ButtonModelInPanel.ButtonTypesName.WIFI -> wifiButton
            ButtonModelInPanel.ButtonTypesName.BLUETOOTH -> bluetoothButton
            ButtonModelInPanel.ButtonTypesName.HOME -> homeButton
            ButtonModelInPanel.ButtonTypesName.BACK -> backButton
            ButtonModelInPanel.ButtonTypesName.LOCK -> lockButton
            ButtonModelInPanel.ButtonTypesName.VOLUME -> volumeButton
            ButtonModelInPanel.ButtonTypesName.TORCH -> torchButton
            ButtonModelInPanel.ButtonTypesName.MULTITASKING -> multitaskingButton
            ButtonModelInPanel.ButtonTypesName.ROTATE -> rotateButton
            ButtonModelInPanel.ButtonTypesName.SCREENSHOT -> screenshotButton
            ButtonModelInPanel.ButtonTypesName.MUTE -> muteButton
            ButtonModelInPanel.ButtonTypesName.BRIGHTNESS -> brightnessButton
            ButtonModelInPanel.ButtonTypesName.SOUND -> soundSettingButton
            ButtonModelInPanel.ButtonTypesName.OPERATOR -> operatorButton
            ButtonModelInPanel.ButtonTypesName.GPS_SETTING -> gpsButton
            ButtonModelInPanel.ButtonTypesName.POWER -> powerButton
            ButtonModelInPanel.ButtonTypesName.APP_DRAWER -> appDrawerButton
            ButtonModelInPanel.ButtonTypesName.NO_ACTION -> noActionButton
            ButtonModelInPanel.ButtonTypesName.CONTACT -> contactButton
            ButtonModelInPanel.ButtonTypesName.APP -> appButton
            ButtonModelInPanel.ButtonTypesName.NOTIFICATION -> notificationButton
            ButtonModelInPanel.ButtonTypesName.REBOOT -> rebootButton
            ButtonModelInPanel.ButtonTypesName.CLIPBOARD -> clipboardButton
            ButtonModelInPanel.ButtonTypesName.TRANSLATE -> translateButton
            ButtonModelInPanel.ButtonTypesName.SCREEN_RECORDING -> screenRecordingButton
        }
    }

    companion object{
        lateinit var buttonFactory: ButtonFactory
        fun initButtonFactory(buttonFactory: ButtonFactory) {
            this.buttonFactory = buttonFactory
        }
    }

}