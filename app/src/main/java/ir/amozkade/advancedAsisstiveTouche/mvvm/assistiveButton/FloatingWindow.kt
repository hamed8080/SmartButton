package ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.PaintDrawable
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import dagger.hilt.android.AndroidEntryPoint
import ir.amozkade.advancedAsisstiveTouche.AppDir
import ir.amozkade.advancedAsisstiveTouche.BuildConfig
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.databinding.ButtonSmartButtonBinding
import ir.amozkade.advancedAsisstiveTouche.databinding.PanelSmartButtonBinding
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.animation.AlertAnimation
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.animation.Animation
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.buttons.HideButton
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.buttons.OpenWindowButton
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.buttons.ScreenshotButton
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.buttons.VolumeDownButton
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.delegates.*
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.models.ButtonModelInPanel
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.models.ButtonPosition
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.models.ButtonSize
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.models.DefaultButtons
import ir.amozkade.advancedAsisstiveTouche.mvvm.editButtonPositions.EditPositionRepository
import ir.amozkade.advancedAsisstiveTouche.mvvm.exception.ExceptionRepository
import ir.amozkade.advancedAsisstiveTouche.mvvm.preferenceActivity.Setting
import ir.amozkade.advancedAsisstiveTouche.mvvm.preferenceActivity.SettingRepository
import ir.mobitrain.applicationcore.helper.CommonHelpers
import ir.mobitrain.applicationcore.helper.Converters.Companion.convertIntToDP
import ir.mobitrain.applicationcore.helper.Converters.Companion.convertIntToSP
import ir.mobitrain.applicationcore.helper.LanguageContext
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import java.io.File
import java.io.InputStream
import javax.inject.Inject
import kotlin.math.ceil


@AndroidEntryPoint
class FloatingWindow : LifecycleService(), FloatingWindowDelegate, ButtonGesture.CustomButtonGestureDelegate, GridButtonAdapter.OnButtonInPanelClickListener {

    var isFromSubWindowItem: Boolean = false
    var isSubWindowOpen = false
    var subWindowView: View? = null

    private var buttonParams: WindowManager.LayoutParams? = null
    private var panelParams: WindowManager.LayoutParams? = null
    private var buttonWindowManager: WindowManager? = null
    private var panelWindowManager: WindowManager? = null
    private var timerDismissJob: Job? = null

    @Inject
    lateinit var settingRepository: SettingRepository
    private lateinit var setting:Setting
    private lateinit var buttonPosition: ButtonPosition
    lateinit var buttonSize: ButtonSize

    @Inject
    lateinit var panelBinding: PanelSmartButtonBinding

    @Inject
    lateinit var buttonBinding: ButtonSmartButtonBinding

    private val cachedButtons = arrayListOf<ButtonModelInPanel>()

    @Inject
    lateinit var listener: ButtonGesture

    @Inject
    lateinit var defaultButtons: DefaultButtons

    @Inject
    lateinit var exceptionRepository: ExceptionRepository

    var isPanelOpen = false
    private var animation: Animation? = null

    @Inject
    @AppDir
    lateinit var appDir: String

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LanguageContext.getContextForService(base))
    }

    override fun onCreate() {
        super.onCreate()
       floatingWindowService = this
    }

    private fun initView() {
        try {
            buttonPosition = setting.getButtonPosition()
            buttonSize = setting.getButtonSize()
            panelBinding.pageIndicator.init(settingRepository)
            buttonWindowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
            panelWindowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
            buttonParams = WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    getWindowParamType(),
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSPARENT
            )

            panelParams = WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    getWindowParamType(),
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSPARENT
            )
            initButtonParams()
            initButtonImageIcon()
            initSpeedTextView()
            setPositionAndSizeButton()
            initPanelLayoutParams()
            initAdapter()
            buttonBinding.button.run {
                y = 0f
                x = 0f
            }
            buttonParams?.run {
                width = buttonSize.width
                height = buttonSize.height
                gravity = Gravity.TOP or Gravity.START
            }

            buttonWindowManager?.addView(buttonBinding.root, buttonParams)
            showButton()
            removeUnusedView()

            val buttonBinding = buttonBinding
            val panelBinding = panelBinding
            val buttonWindowManager = buttonWindowManager ?: return
            val panelWindowManager = panelWindowManager ?: return
            val buttonParams = buttonParams ?: return
            val panelParams = panelParams ?: return
            animation = Animation(buttonBinding, panelBinding, buttonWindowManager, panelWindowManager, buttonParams, panelParams, this, setting.animationEnabled, setting.pagerEnable, setting.isLeftMenu, settingRepository)
            animation?.update(buttonParams.x.toFloat(), buttonParams.y.toFloat())

            listener.setMinOpacityIfEnabled()

        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                Toast.makeText(this, "exception in floating window${e.message}", Toast.LENGTH_LONG).show()
            }
            exceptionRepository.saveException(e)
        }
    }

    private fun setPositionAndSizeButton() {
        buttonBinding.button.layoutParams = ConstraintLayout.LayoutParams(buttonSize.width, buttonSize.height)
        buttonParams?.run {
            x = buttonPosition.x
            y = buttonPosition.y
        }
    }

    private fun initButtonImageIcon() {
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.ARGB_8888
        if (setting.userSelectedImageName == "default") {
            val drawable = ContextCompat.getDrawable(this, R.drawable.logo)
            drawable?.colorFilter = PorterDuffColorFilter(setting.buttonColorOverlay, PorterDuff.Mode.MULTIPLY)
            buttonBinding.imageView.setImageDrawable(drawable)
        } else {
            val filePath: String = appDir + "/" + setting.userSelectedImageName
            val contentUri: Uri = FileProvider.getUriForFile(this, "$packageName.provider", File(filePath))
            lifecycleScope.launch(IO) {
                delay(300)
                val inp: InputStream? = contentResolver.openInputStream(contentUri)
                val bitmap = BitmapFactory.decodeStream(inp)
                val drawable = BitmapDrawable(resources, bitmap)
                drawable.colorFilter = PorterDuffColorFilter(setting.buttonColorOverlay, PorterDuff.Mode.MULTIPLY)
                withContext(Main) {
                    buttonBinding.imageView.setImageDrawable(drawable)
                }
            }
        }
    }

    private fun initSpeedTextView() {
        buttonBinding.txtSpeed.run {
            text = ""
            textSize = setting.speedTextSize.toFloat()
        }
    }

    private fun removeUnusedView() {
        (panelBinding.root as ConstraintLayout).run {
            if (setting.pagerEnable) {
                removeView(panelBinding.rcvButton)
            } else {
                removeView(panelBinding.vp)
                removeView(panelBinding.pageIndicator)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initButtonParams() {
        buttonBinding.txtSpeed.textSize = convertIntToSP(setting.speedTextSize, this)
        setButtonMovingTouchListener()
    }

    private fun setButtonMovingTouchListener() {
        val buttonParams = buttonParams ?: return
        listener.run {
            setDelegate(this@FloatingWindow)
            setButtonParams(buttonParams)
            //in api 30 and above crash
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R){
                setDisplay(display)
            }
            setIsAutomaticEdgeEnabled(setting.isAutomaticEdgeEnabled)
            setIsAutoAlphaEnabled(setting.autoAlphaButtonEnable)
        }
        buttonBinding.root.setOnTouchListener(listener)
    }

    @Suppress("DEPRECATION")
    private fun hideButtonAndGoesToNotificationCenter() {
        closeButton()
        startForegroundService(true, 300, "Hide", "FROM_NOTIFICATION", "OK")
        val v = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
        } else {
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(200, 10))
        }
    }

    override fun speedChange(speed: String) {
        runBlocking(Main){
            buttonBinding.txtSpeed.text = speed
        }
    }

    override fun closePanel() {
        //prevent accessibility blink
        if (!isPanelOpen) {
            return
        }
        isPanelOpen = false
        animation?.startAnimation(false)
    }

    override fun closePanelForScreenshot() {
        panelBinding.run {
            vp.alpha = 0f
            rcvButton.alpha = 0f
        }
    }

    override fun openPanel() {
        isPanelOpen = true
        if (setting.pagerEnable) panelBinding.vp.visibility = View.VISIBLE else panelBinding.rcvButton.visibility = View.VISIBLE
        animation?.startAnimation(true)
    }

    private fun initPanelLayoutParams() {
        val panel = panel
        panel.alpha = setting.panelAlpha
        if (setting.isLeftMenu) {
            (panel as RecyclerView).layoutManager = GridLayoutManager(this, 1)
            val padding = convertIntToDP(5, this).toInt()
            val paddingTopAndBottom = convertIntToDP(2, this).toInt()
            panel.setPadding(padding, paddingTopAndBottom, padding, paddingTopAndBottom)
            panel.layoutParams.height = resources.displayMetrics.heightPixels - convertIntToDP(48, this).toInt()
            panel.layoutParams.width = convertIntToDP(48, this).toInt() + convertIntToDP(5, this).toInt()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                panel.clipToOutline = true
            }
            panel.clipChildren = true
        } else {
            val padding = convertIntToDP(24, this).toInt()
            panel.setPadding(padding, 0, padding, 0)
        }

        when {
            setting.userSelectedPanelImageName != null -> {
                val options = BitmapFactory.Options()
                options.inPreferredConfig = Bitmap.Config.ARGB_8888
                val filePath: String = appDir + "/" + setting.userSelectedPanelImageName
                lifecycleScope.launch(IO) {
                    delay(300)
                    withContext(Main) {
                        panel.background = CommonHelpers.getRoundedCornerDrawable(this@FloatingWindow, filePath, convertIntToDP(24, this@FloatingWindow))
                    }
                }
            }
            setting.isLeftMenu -> {
                PaintDrawable(setting.panelColorOverlay).apply {
                    setCornerRadius(panel.layoutParams.width / 2f)
                    panel.background = this
                }
            }
            else -> {
                val drawable = ContextCompat.getDrawable(this, R.drawable.progress_bar_container) as LayerDrawable
                (drawable.findDrawableByLayerId(R.id.backgroundShape) as GradientDrawable).setColor(setting.panelColorOverlay)
                panel.background = drawable
            }
        }
    }

    private fun initAdapter() {
        if (setting.pagerEnable) {
            initPagerAdapter()
        } else {
            initGridAdapter()
        }
        setPanelBackgroundTapListener()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initGridAdapter() {
        panelBinding.rcvButton.run {
            adapter = GridButtonAdapter(cachedButtons, settingRepository, this@FloatingWindow)
            adapter?.notifyDataSetChanged()
            setHasFixedSize(true)
        }
    }

    private fun initPagerAdapter() {
        panelBinding.run {
            vp.adapter = PagerButtonAdapter(cachedButtons, this@FloatingWindow, settingRepository, this@FloatingWindow)
            pageIndicator.count = ceil(cachedButtons.count().toFloat() / 9f).toInt()
            pageIndicator.circleWidth = 20f
            vp.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) = Unit
                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) = Unit
                override fun onPageSelected(position: Int) {
                    pageIndicator.selectedIndex = position
                }
            })
            vp.adapter?.notifyDataSetChanged()
        }
    }

    private fun setPanelBackgroundTapListener() {
        panelBinding.root.setOnClickListener {
            isPanelOpen = false
            animation?.startAnimation(false)
        }
    }

    companion object {

        var isServiceStarted = MutableLiveData<Boolean>()
        var persistIntent : Intent? = null
        var floatingWindowService: FloatingWindow? = null

        fun restartButtonService(cto: Context, settingRepository: SettingRepository) {
            if (floatingWindowService != null && settingRepository.getCashedModel().startedByUser) {
                cto.stopService(persistIntent)
                val intent = Intent(cto, FloatingWindow::class.java)
                intent.removeExtra("FROM_NOTIFICATION")
                intent.putExtra("USER_TAP_ON_START", "OK")
                persistIntent = intent
                cto.startService(intent)
            }
        }

        fun getWindowParamType(): Int {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_PHONE
            }
        }
    }

    override fun showButton() {
        buttonBinding.button.visibility = View.VISIBLE
    }

    override fun closeButton() {
        saveButtonPosition()
        buttonBinding.button.visibility = View.GONE
    }

    @SuppressLint("DefaultLocale")
    override fun showWarn(title: String, subtitle: String, imageId: Int?, imagePath: String?) {
        val wm = buttonWindowManager ?: return
        val alertAnimation = AlertAnimation(this, wm)
        alertAnimation.start(title, subtitle, imageId, imagePath)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return try {
            setting = settingRepository.getCashedModel()
            if (intent?.getStringExtra("USER_TAP_ON_START") != null || intent?.getStringExtra("BOOT_COMPLETED") != null) {
                startNormalService()
            }

            if (intent?.hasExtra("StartIntent") == true) {
                panelParams?.flags = 0
                buttonWindowManager?.addView(buttonBinding.root, buttonParams)
                startForegroundService(false, 500, "StartAppToForeground", null, null)
                showButton()
            }else if (intent?.hasExtra("StopIntent") == true){
                hideButtonAndGoesToNotificationCenter()
            }
            START_STICKY
        } catch (e: Exception) {
            exceptionRepository.saveException(e)
            super.onStartCommand(intent, flags, startId)
        }
    }

    private fun startNormalService() {
        startForegroundService(false, 500, "StartAppToForeground", null, null)
        initCachedButtons()
        initView()
        CurrentSpeedMeter.sharedInstance().startShowingCurrentSpeedIfEnabled(this, setting.showSpeedEnabled)
    }

    private fun initCachedButtons() {
        if (setting.buttons != null) {
            cachedButtons.addAll(EditPositionRepository.convertPreferenceToSmartButton(this, settingRepository))
        } else {
            for (button in defaultButtons.getDefaultButtons().filter {
                it.buttonTypeName != ButtonModelInPanel.ButtonTypesName.HIDE_TO_NOTIFICATION && it.buttonTypeName != ButtonModelInPanel.ButtonTypesName.OPEN_WINDOW
            }.take(9)) {
                cachedButtons.add(button)
            }
        }
        cachedButtons.forEach {
            (it.button as? EnableDelegate)?.initIsEnable(this)
        }
    }


    private fun saveButtonPosition() {
        //save to memory to show button can calculate correct position
        buttonParams?.let {
            val position = ButtonPosition(it.x, it.y)
            lifecycleScope.launch(IO) {
                settingRepository.saveButtonPosition(position)
            }
            buttonPosition.x = it.x
            buttonPosition.y = it.y
        }
    }

    private fun startForegroundService(hideToStatusBar: Boolean, id: Int, channelName: String, extraIntentKey: String?, extraIntentValue: String?) {
        stopForeground(true)

        val intent = Intent(this, FloatingWindow::class.java)
        intent.putExtra( if (hideToStatusBar) "StartIntent" else "StopIntent",true)
        intent.putExtra(extraIntentKey, extraIntentValue)

        val pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val builder: NotificationCompat.Builder
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = NotificationCompat.Builder(this, createNotificationChannel(channelName))
        } else {
            @Suppress("DEPRECATION")
            builder = NotificationCompat.Builder(this)
        }
        builder
                .addAction(R.drawable.notification_small, if (hideToStatusBar) "Start" else "Pause", pendingIntent)
                .setAutoCancel(false)
                .setOngoing(true)
                .setSmallIcon(R.drawable.notification_small)
                .setContentTitle(getString(R.string.app_name))
        if (hideToStatusBar) {
            safeRemoveRootBinding()
        }
        val notif = builder.build()
        startForeground(id, notif)
        persistIntent = intent
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelName: String): String {
        val channelId = "my_service"
        val chan = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_NONE)
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }

    override fun updateLayoutParamsOnGesture(lastButtonX: Float, lastButtonY: Float) {
        animation?.update(lastButtonX, lastButtonY)
        buttonBinding.root.let {
            if (ViewCompat.isAttachedToWindow(it)) {
                buttonWindowManager?.updateViewLayout(buttonBinding.root, buttonParams)
            }
        }
    }

    override fun longPress() {
        val holdAction = setting.getHoldAction(this)
        if (holdAction == null || holdAction.button is HideButton) {
            hideButtonAndGoesToNotificationCenter()
        } else {
            onClickItemInPanel(holdAction.button!!, holdAction)
        }
    }

    override fun doubleTap() {
        setting.getDoubleTapAction(this)?.let {
            val button = it.button ?: return
            onClickItemInPanel(button, it)
        }
    }

    override fun singleTap() {
        val tapAction = setting.getSingleTapAction(this)
        if ( tapAction == null || tapAction.button is OpenWindowButton) {
            openPanel()
        } else {
            onClickItemInPanel(tapAction.button!!, tapAction)
        }
    }

    override fun updateButtonOpacity(alpha: Float) {
        buttonBinding.button.alpha = alpha
    }

    override fun onClickItemInPanel(button: AssistiveButtonDelegate, buttonModel: ButtonModelInPanel) {
        try {
            if (!button.isOverlayPanelType() && button !is ScreenshotButton) {
                closePanel()
                showButton()
            }
            button.action(this, buttonModel)
        } catch (e: Exception) {
            exceptionRepository.saveException(e)
        }
    }

    override fun onLongClickItemInPanel(button: AssistiveButtonDelegate, buttonModel: ButtonModelInPanel) {
        try {
            closePanel()
            showButton()
            (button as? LongActionDelegate)?.actionLong(this, buttonModel)
        } catch (e: Exception) {
            exceptionRepository.saveException(e)
        }
    }

    override fun onEnableButtonChange(button: ButtonModelInPanel, isEnable: Boolean) {
        cachedButtons.indexOfFirst { it.buttonTypeName == button.buttonTypeName }.let { index ->
            if (setting.pagerEnable) {
                (panelBinding.vp.adapter as PagerButtonAdapter).updateEnableDisableButtonForIndex(index, button, isEnable)
            } else {
                (panelBinding.rcvButton.adapter as GridButtonAdapter).updateEnableDisableButtonForIndex(index, isEnable)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun showVolumeView(currentVolume: Int, maxVolumeLevel: Int, button: AssistiveButtonDelegate) {
        //VolumeDownButton is companion object so not important volume up or down that handle inside companion object
        VolumeDownButton.showVolumeView(currentVolume, maxVolumeLevel, this, button)
    }

    private val panel: View
        get() {
            return if (setting.pagerEnable) panelBinding.vp else panelBinding.rcvButton
        }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        setButtonMovingTouchListener()
        listener.configurationChanged(newConfig)
        animation?.updateLayoutWidthHeightOnOrientationChange(newConfig)
    }

    override fun closePanelForSubPanel() {
        panel.visibility = View.GONE
        panelBinding.pageIndicator.visibility = View.GONE
    }

    private var subWindow: WindowManager? = null
    private fun createSubWindow() {
        subWindow = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    override fun addViewToSubWindow(view: View, button: AssistiveButtonDelegate) {
        if (subWindowView == null) {
            createSubWindow()
        }
        subWindowView = view
        isSubWindowOpen = true
        cancelDismissJob()
        val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                getWindowParamType(),
                0,
                PixelFormat.TRANSPARENT
        )
        subWindow?.addView(view, params)
        if (button is AutoDismissActionDelegate) {
            //clipboard can't be dismiss
            startDismissJob()
        }
    }

    override fun removeViewSubWindow(view: View) {
        isSubWindowOpen = false
        if (ViewCompat.isAttachedToWindow(view)) {
            subWindow?.removeViewImmediate(view)
        }
    }

    override fun startDismissJob() {
        timerDismissJob = CoroutineScope(Main).launch {
            delay(2000)
            subWindowView?.let {
                removeViewSubWindow(it)
            }
        }
    }

    override fun cancelDismissJob() {
        timerDismissJob?.cancel()
    }

    private fun safeRemoveRootBinding() {
        buttonBinding.let {
            if (ViewCompat.isAttachedToWindow(it.root)) {
                buttonWindowManager?.removeView(it.root)
            }
        }
        panelBinding.root.let {
            if (ViewCompat.isAttachedToWindow(it)) {
                panelWindowManager?.removeViewImmediate(it)
            }
        }
    }

    //****************** Close or destroy service ******************
    override fun onDestroy() {
        super.onDestroy()
        (panelBinding.rcvButton.adapter as? GridButtonAdapter)?.onDestroyCalled()
        saveButtonPosition()
        stopSelf()
        closeButton()
        safeRemoveRootBinding()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        saveButtonPosition()
        if (!ViewCompat.isAttachedToWindow(buttonBinding.root)) {
            startNormalService()
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        if (persistIntent!= null) {
            stopService(persistIntent)
            startService(persistIntent)
        }
    }

    override fun stopService() {
        stopSelf()
    }
    //****************** Close or destroy service ******************
}