package ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.buttons.translate

import android.annotation.SuppressLint
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.amozkade.advancedAsisstiveTouche.AppDir
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.databinding.TranslateLayoutBinding
import ir.amozkade.advancedAsisstiveTouche.helper.CustomSpinnerAdapter
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.leitners.di.LeitnerDao
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.leitners.models.Leitner
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.FloatingWindow
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.delegates.AssistiveButtonDelegate
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.delegates.FloatingWindowDelegate
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

class TranslateButton @Inject constructor(private val leitnerDao: LeitnerDao ,
                                          @ApplicationContext private val context:Context,
                                          @AppDir private  val appDir:String,
                                          private val settingRepository: SettingRepository,
                                          private val viewModel: TranslateViewModel ): ScalePanelAnimationButton(), AssistiveButtonDelegate {

    private var mBinding: TranslateLayoutBinding? = null

    private var leitnerSelectedId: Int = 0

    override fun action(delegate: FloatingWindowDelegate, buttonModel: ButtonModelInPanel) {
        this.delegate = delegate
        if (mBinding == null) {
            val themeContextWrapper = ContextThemeWrapper(context, R.style.AppTheme)
            val inflater = themeContextWrapper.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            mBinding = DataBindingUtil.inflate(inflater, R.layout.translate_layout, null, false)
            mBinding?.vm = viewModel
            initView()
        }
        val mBinding = mBinding ?: return
        rootView = mBinding.root
        delegate.addViewToSubWindow(mBinding.root, this)
        scaleAnimation(true)
        delegate.closePanelForSubPanel()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        val mBinding = mBinding ?: return
        val setting = settingRepository.getCashedModel()
        val panel = mBinding.subWindowView
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

        mBinding.addToLeitnerLayout.visibility = View.VISIBLE
        mBinding.txtTitle.setTextColor(setting.panelButtonsColor)
        mBinding.edt.setHintTextColor(setting.panelButtonsColor)
        mBinding.edt.setTextColor(setting.panelButtonsColor)
        mBinding.txtFromTitle.setTextColor(setting.panelButtonsColor)
        mBinding.txtToTitle.setTextColor(setting.panelButtonsColor)
        mBinding.targetText.setTextColor(setting.panelButtonsColor)
        val states = arrayOf(intArrayOf(android.R.attr.state_enabled), intArrayOf(-android.R.attr.state_enabled), intArrayOf(-android.R.attr.state_checked), intArrayOf(android.R.attr.state_pressed))
        val color = setting.panelButtonsColor
        val colors = intArrayOf(color, color, color, color)
        mBinding.textInputLayout.hintTextColor = ColorStateList(states, colors)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBinding.sourceLangSelector.backgroundTintList = ColorStateList(states, colors)
            mBinding.targetLangSelector.backgroundTintList = ColorStateList(states, colors)
        }
        val en = viewModel.availableLanguages.first { it.code == "en" }
        val sourcePref = viewModel.availableLanguages.firstOrNull { it.code == setting.sourceLang }
        val backColor = if (setting.userSelectedPanelImageName != null) Color.TRANSPARENT else setting.panelColorOverlay
        val adapter = CustomSpinnerAdapter(viewModel.availableLanguages, setting.panelButtonsColor, backColor, "en")
        mBinding.sourceLangSelector.adapter = adapter
        mBinding.sourceLangSelector.setSelection(viewModel.availableLanguages.indexOf(sourcePref
                ?: en))
        mBinding.edt.setOnTouchListener { _, _ ->
            FloatingWindow.floatingWindowService?.isFromSubWindowItem = true
            return@setOnTouchListener false
        }
        mBinding.sourceLangSelector.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
            ) {
                val selectedItem = adapter.getItem(position)
                FloatingWindow.floatingWindowService?.isFromSubWindowItem = true
                viewModel.sourceLang.value = selectedItem
                GlobalScope.launch(IO) {
                    settingRepository.setSourceLang(selectedItem.code)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                FloatingWindow.floatingWindowService?.isFromSubWindowItem = true
                setTextTranslateResult("")
            }
        }

        mBinding.root.setOnClickListener {
            scaleAnimation(false)
            delegate.showButton()
        }

        mBinding.subWindowView.setOnClickListener {
            FloatingWindow.floatingWindowService?.isFromSubWindowItem = true
        }

        // TargetLangSelector
        mBinding.targetLangSelector.adapter = adapter
        val fa = viewModel.availableLanguages.first { it.code == "fa" }
        val targetPref = viewModel.availableLanguages.firstOrNull { it.code == setting.destLang }
        mBinding.targetLangSelector.setSelection(viewModel.availableLanguages.indexOf(targetPref
                ?: fa))
        mBinding.targetLangSelector.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
            ) {
                val selectedItem = adapter.getItem(position)
                FloatingWindow.floatingWindowService?.isFromSubWindowItem = true
                viewModel.targetLang.value = selectedItem
                GlobalScope.launch(IO){
                    settingRepository.setDestLang(selectedItem.code)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                FloatingWindow.floatingWindowService?.isFromSubWindowItem = true
                setTextTranslateResult("")
            }
        }

        mBinding.edt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                viewModel.sourceText.postValue(s.toString())
            }
        })


        mBinding.btnAddToLeitner.setOnClickListener {
            if (mBinding.edt.text.isNullOrEmpty()) return@setOnClickListener
            viewModel.addToLeitner(mBinding.edt.text.toString(), leitnerSelectedId)
        }

        GlobalScope.launch(IO) {
            val leitners = leitnerDao.getAll()
            GlobalScope.launch(Dispatchers.Main) {
                mBinding.leitnerSelector.adapter = CustomSpinnerAdapter(leitners, setting.panelButtonsColor, backColor, "")
            }
        }
        mBinding.leitnerSelector.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
            ) {
                FloatingWindow.floatingWindowService?.isFromSubWindowItem = true
                val leitnerAdapter = mBinding.leitnerSelector.adapter as? CustomSpinnerAdapter<*>
                val selectedItem = leitnerAdapter?.getItem(position)
                leitnerSelectedId = (selectedItem as Leitner).id
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                FloatingWindow.floatingWindowService?.isFromSubWindowItem = true
            }
        }


        mBinding.buttonSwitchLang.setOnClickListener {
            FloatingWindow.floatingWindowService?.isFromSubWindowItem = true
            val sourceLangPosition = mBinding.sourceLangSelector.selectedItemPosition
            mBinding.sourceLangSelector.setSelection(mBinding.targetLangSelector.selectedItemPosition)
            mBinding.targetLangSelector.setSelection(sourceLangPosition)
        }

        mBinding.btnFromClipboard.setOnClickListener {
            FloatingWindow.floatingWindowService?.isFromSubWindowItem = true
            val clipManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val isHtml = clipManager.primaryClipDescription?.hasMimeType(ClipDescription.MIMETYPE_TEXT_HTML)
                    ?: false
            val isTextPlain = (clipManager.primaryClipDescription?.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
                    ?: false)
            if (isHtml || isTextPlain) {
                val text = clipManager.primaryClip?.getItemAt(0)?.text.toString()
                mBinding.edt.setText(text)
            }
        }

        viewModel.translatedText.observe((context as FloatingWindow)) { resultOrError ->
            resultOrError?.let {
                if (it.error != null) {
                    setTextTranslateResult(resultOrError.error?.localizedMessage ?: "")
                } else {
                    setTextTranslateResult(resultOrError.result ?: "")
                }
            }
        }

        viewModel.downloadModelText.observe(context) {
            setTextTranslateResult(it)
        }
    }

    private fun setTextTranslateResult(text: String) {
        mBinding?.targetText?.text = text
        val isFa = Locale.getDefault().language.contains("fa") || CommonHelpers.textContainsArabic(text)
        mBinding?.targetText?.typeface = ResourcesCompat.getFont(context, if (isFa) R.font.iransans_bold else R.font.sf_pro_rounded_bold)
    }

    fun translateFromOtherApp(text: String) {
        mBinding?.edt?.setText(text)
    }

}