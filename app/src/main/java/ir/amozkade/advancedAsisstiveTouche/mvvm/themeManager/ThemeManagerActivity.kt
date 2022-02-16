package ir.amozkade.advancedAsisstiveTouche.mvvm.themeManager

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.databinding.ActivityThemeManagerBinding
import ir.amozkade.advancedAsisstiveTouche.helper.api.DataState
import ir.amozkade.advancedAsisstiveTouche.mvvm.BaseActivity
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.FloatingWindow
import ir.amozkade.advancedAsisstiveTouche.mvvm.preferenceActivity.SettingRepository
import ir.amozkade.advancedAsisstiveTouche.mvvm.themeManager.adapters.SectionAdapter
import ir.amozkade.advancedAsisstiveTouche.mvvm.themeManager.di.ThemeDao
import ir.amozkade.advancedAsisstiveTouche.mvvm.themeManager.models.Section
import ir.amozkade.advancedAsisstiveTouche.mvvm.themeManager.models.Theme
import ir.amozkade.advancedAsisstiveTouche.mvvm.themeManager.models.ThemePack
import ir.amozkade.advancedAsisstiveTouche.mvvm.themeManager.utils.ThemeManagerResponse
import ir.amozkade.advancedAsisstiveTouche.mvvm.themeManager.utils.ThemeManagerStateEvent
import ir.mobitrain.applicationcore.alertDialog.AlertDialogDelegate
import ir.mobitrain.applicationcore.alertDialog.CustomAlertDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ThemeManagerActivity : BaseActivity(), SectionAdapter.OnItemClickListener<Theme> {

    @Inject
    lateinit var themeDao: ThemeDao
    private var offlineThemes: List<Theme> = listOf()
    private val argsScrollLState = "recyclerState"

    @Inject
    lateinit var settingRepository: SettingRepository

    private lateinit var mBinding: ActivityThemeManagerBinding
    private val viewModel: ThemeManagerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUI()
        setupObservers()
        if (savedInstanceState == null){
            showLoadingBlockUi()
            viewModel.setState(ThemeManagerStateEvent.GetAllThemes)
        }else{
            val adapterState: Parcelable? = savedInstanceState.getParcelable(argsScrollLState)
            mBinding.rcv.layoutManager?.onRestoreInstanceState(adapterState)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (mBinding.rcv.adapter != null){
            outState.putParcelable(argsScrollLState, mBinding.rcv.layoutManager?.onSaveInstanceState())
        }
    }

    private fun setupUI() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_theme_manager)
        addLoadingsToContainer(mBinding.container)
        mBinding.vm = viewModel
    }

    private fun setupObservers() {
        viewModel.exceptionObserver.observe(this) {
            manageDataState(DataState.Error(it as Exception))
        }
        viewModel.response.observe(this) { dataState ->
            if (dataState is DataState.Success) {
                if (dataState.data is ThemeManagerResponse.Themes) {
                    updateAdapter(dataState.data.themePacks, dataState.data.themes)
                }
                if (dataState.data is ThemeManagerResponse.SucceededDownloadTheme) {
                    succeededDownloadTheme()
                }
                if (dataState.data is ThemeManagerResponse.SucceededDownloadThemePack) {
                    FloatingWindow.restartButtonService(this, settingRepository)
                }
            }
            manageDataState(dataState)
        }
        CoroutineScope(IO).launch {
            offlineThemes = themeDao.getAllTheme()
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateAdapter(themePacks: List<ThemePack>, themes: List<Section<Theme>>) {
        hideLoading()
        mBinding.rcv.adapter = SectionAdapter(themePacks, themes, offlineThemes, this)
        (mBinding.rcv.adapter as SectionAdapter<*>).notifyDataSetChanged()
    }

    override fun onClickItem(item: Theme) {
        CustomAlertDialog.showDialog(this,
                title = item.title,
                imageUrl = item.thumbnailImageAddress,
                submitTitle = getString(R.string.apply),
                cancelTextColorId = ContextCompat.getColor(this, R.color.red),
                submitTextColorId = ContextCompat.getColor(this, R.color.primary_color),
                delegate = object : AlertDialogDelegate {
                    override fun alertCompletion(type: AlertDialogDelegate.AlertTapType, extraButton: MaterialButton?) {
                        if (type == AlertDialogDelegate.AlertTapType.Submit) {
                            viewModel.setState(ThemeManagerStateEvent.DownloadTheme(item))
//                            if (item.themeType == "Font") {
//                                viewModel.downloadImageThumbnail(item.thumbnailImageAddress, item.thumbnailImageAddress.split("/").last())
//                            }
                        }
                    }
                }
        )
    }

    override fun onOfflineThemeClick(themes: List<Theme>) {
        if (themes.isEmpty()) {
            return
        }
        val intent = Intent(this, OfflineThemeActivity::class.java)
        intent.putParcelableArrayListExtra("themes", ArrayList(themes))
        startActivity(intent)
    }

    override fun onDefaultThemeClick() {
        CustomAlertDialog.showDialog(this,
                title = getString(R.string.default_theme),
                message = getString(R.string.default_theme_subtitle),
                submitTitle = getString(R.string.apply),
                cancelTextColorId = ContextCompat.getColor(this, R.color.red),
                submitTextColorId = ContextCompat.getColor(this, R.color.primary_color),
                delegate = object : AlertDialogDelegate {
                    override fun alertCompletion(type: AlertDialogDelegate.AlertTapType, extraButton: MaterialButton?) {
                        if (type == AlertDialogDelegate.AlertTapType.Submit) {
                            viewModel.setDefaultTheme(this@ThemeManagerActivity)
                        }
                    }
                }
        )
    }

    private fun succeededDownloadTheme() {
        mBinding.rcv.adapter?.notifyItemChanged(0)
        FloatingWindow.restartButtonService(this,settingRepository)
    }

    override fun onThemePackClick(themePack: ThemePack) {
        CustomAlertDialog.showDialog(this,
                title = themePack.title,
                imageUrl = themePack.thumbnailUrl,
                submitTitle = getString(R.string.apply),
                cancelTextColorId = ContextCompat.getColor(this, R.color.red),
                submitTextColorId = ContextCompat.getColor(this, R.color.primary_color),
                delegate = object : AlertDialogDelegate {
                    override fun alertCompletion(type: AlertDialogDelegate.AlertTapType, extraButton: MaterialButton?) {
                        if (type == AlertDialogDelegate.AlertTapType.Submit) {
                            viewModel.setState(ThemeManagerStateEvent.DownloadThemePack(themePack))
                        }
                    }
                }
        )
    }
}