package ir.amozkade.advancedAsisstiveTouche

import android.app.Application
import android.os.Build
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.models.ButtonFactory
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.models.DefaultButtons
import ir.amozkade.advancedAsisstiveTouche.mvvm.exception.ExceptionHandler
import ir.amozkade.advancedAsisstiveTouche.mvvm.preferenceActivity.Setting
import ir.amozkade.advancedAsisstiveTouche.mvvm.preferenceActivity.SettingRepository
import ir.mobitrain.applicationcore.api.JWT
import ir.mobitrain.applicationcore.helper.Preference.setInstance
import ir.mobitrain.applicationcore.helper.encryption.Crypt
import javax.inject.Inject

@HiltAndroidApp
open class BaseApp : Application(), Configuration.Provider {

    @Inject
    lateinit var defaultButtons: DefaultButtons

    @Inject
    lateinit var buttonFactory: ButtonFactory

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var exceptionHandler: ExceptionHandler

    @Inject
    lateinit var settingRepository: SettingRepository

    override fun onCreate() {
        super.onCreate()
        setInstance(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Crypt.init(this, JWT.JWT_SIGNATURE_ALIAS)
        }
        ButtonFactory.initButtonFactory(buttonFactory)
        Setting.appLaunchInit(defaultButtons , settingRepository)
        exceptionHandler.startSyncExceptionWithServer()
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
                .setWorkerFactory(workerFactory)
                .build()
    }
}
