package ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.buttons.clipborad

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.*
import android.os.Build
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.amozkade.advancedAsisstiveTouche.AppDir
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.databinding.ClipboardsLayoutBinding
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.FloatingWindow
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.delegates.FloatingWindowDelegate
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.delegates.AssistiveButtonDelegate
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.delegates.ScalePanelAnimationButton
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.models.ButtonModelInPanel
import ir.amozkade.advancedAsisstiveTouche.mvvm.preferenceActivity.SettingRepository
import ir.mobitrain.applicationcore.helper.CommonHelpers
import ir.mobitrain.applicationcore.helper.Converters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class ClipboardButton @Inject constructor(@ApplicationContext private val context: Context,
                                          private val clipboardDao: ClipboardDao,
                                          private val settingRepository: SettingRepository,
                                          @AppDir private val appDir: String) : ScalePanelAnimationButton(), AssistiveButtonDelegate, ClipboardDelegate {

    private var mBinding: ClipboardsLayoutBinding? = null
    private var clipboardManager: ClipboardManager? = null

    override fun action(delegate: FloatingWindowDelegate, buttonModel: ButtonModelInPanel) {
        this.delegate = delegate
        if (mBinding == null) {
            val themeContextWrapper = ContextThemeWrapper(context, R.style.AppTheme)
            val inflater = themeContextWrapper.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            mBinding = DataBindingUtil.inflate(inflater, R.layout.clipboards_layout, null, false)
            initView()
        }

        mBinding?.edt?.setText("")
        val mBinding = mBinding ?: return
        rootView = mBinding.root
        val displayWidth = context.resources.displayMetrics.widthPixels
        val subWindowHeight = 4 * (context.resources.displayMetrics.heightPixels / 6)
        val subWindowWidth = displayWidth - Converters.convertIntToSP(64, context).toInt()
        mBinding.subWindowView.layoutParams.width = subWindowWidth
        mBinding.subWindowView.layoutParams.height = subWindowHeight
        mBinding.rcv.adapter = ClipboardAdapter(arrayListOf(), mBinding.rcv, settingRepository,this)
        (mBinding.rcv.adapter as ClipboardAdapter).notifyDataSetChanged()
        delegate.addViewToSubWindow(mBinding.root, this)
        fetchClipboardsAsyncAndSetAdapter()
        scaleAnimation(true)
        delegate.closePanelForSubPanel()
        clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }

    private fun addFromEditTextToDataBase() {
        val text = mBinding?.edt?.text.toString()
        addToDatabase(text, context)
    }

    private fun addFromClipboardToDataBase() {
        val isHtml = clipboardManager?.primaryClipDescription?.hasMimeType(ClipDescription.MIMETYPE_TEXT_HTML)
                ?: false
        val isTextPlain = (clipboardManager?.primaryClipDescription?.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
                ?: false)
        if (isHtml || isTextPlain) {
            val text = clipboardManager?.primaryClip?.getItemAt(0)?.text.toString()
            addToDatabase(text, context)
        }
    }

    private fun addToDatabase(text: String, cto: Context) {
        if (text == "") return
        GlobalScope.launch {
            val clipboard = Clipboard(date = Date(), text = text)
            clipboardDao.insert(clipboard)
            addToTopOfAdapter(clipboard)
        }
    }

    private fun addToTopOfAdapter(clipboard: Clipboard) {
        GlobalScope.launch(Dispatchers.Main) {
            (mBinding?.rcv?.adapter as ClipboardAdapter).addToTop(clipboard)
        }
    }

    private fun fetchClipboardsAsyncAndSetAdapter() {
        GlobalScope.launch(IO) {
            val clipboards = clipboardDao.getAll().sortedByDescending { it.date }
            setupAdapter(clipboards = clipboards)
        }
    }

    private fun setupAdapter(clipboards: List<Clipboard>) {
        GlobalScope.launch(Dispatchers.Main) {
            mBinding?.rcv?.let { rcv ->
                rcv.adapter = ClipboardAdapter(ArrayList(clipboards), rcv, settingRepository,this@ClipboardButton)
                (rcv.adapter as ClipboardAdapter).notifyDataSetChanged()
            }
        }
    }

    override fun onSelectClipboard(clipboard: Clipboard) {
        val item = ClipData.Item(clipboard.text)
        val description = ClipDescription("", arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN))
        val clipData = ClipData(description, item)
        clipboardManager?.setPrimaryClip(clipData)
        scaleAnimation(false)
    }

    override fun deleteFromDatabaseAndNotify(clipboard: Clipboard) {
        GlobalScope.launch {
            clipboardDao.delete(clipboard)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
       val setting = settingRepository.getCashedModel()
        val panel = mBinding?.subWindowView ?: return
        val drawable = ContextCompat.getDrawable(context, R.drawable.progress_bar_container) as LayerDrawable

        if (setting.userSelectedPanelImageName != null) {
            val options = BitmapFactory.Options()
            options.inPreferredConfig = Bitmap.Config.ARGB_8888
            val filePath: String = appDir + "/" + setting.userSelectedPanelImageName
            panel.background = CommonHelpers.getRoundedCornerDrawable(context, filePath, Converters.convertIntToDP(24, context))
        } else {
            (drawable.findDrawableByLayerId(R.id.backgroundShape) as GradientDrawable).setColor(setting.panelColorOverlay)
            panel.background = drawable
        }

        mBinding?.txtTitle?.setTextColor(setting.panelButtonsColor)
        mBinding?.txtSubtitle?.setTextColor(setting.panelButtonsColor)
        mBinding?.edt?.setHintTextColor(setting.panelButtonsColor)
        mBinding?.edt?.setTextColor(setting.panelButtonsColor)
        mBinding?.btnAddToClipboard?.setOnClickListener {
            addFromEditTextToDataBase()
            FloatingWindow.floatingWindowService?.isFromSubWindowItem = true
        }
        mBinding?.btnAddFromClipboardToList?.setOnClickListener {
            addFromClipboardToDataBase()
            FloatingWindow.floatingWindowService?.isFromSubWindowItem = true
        }
        mBinding?.root?.setOnClickListener {
            scaleAnimation(false)
        }
        mBinding?.edt?.setOnTouchListener { _, _ ->
            FloatingWindow.floatingWindowService?.isFromSubWindowItem = true
            return@setOnTouchListener false
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            mBinding?.btnAddFromClipboardToList?.visibility = View.VISIBLE
        }
    }
}