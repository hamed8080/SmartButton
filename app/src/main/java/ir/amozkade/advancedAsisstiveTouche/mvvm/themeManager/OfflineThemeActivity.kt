package ir.amozkade.advancedAsisstiveTouche.mvvm.themeManager

import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import ir.amozkade.advancedAsisstiveTouche.AppDir
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.databinding.ActivityOfflineThemeBinding
import ir.amozkade.advancedAsisstiveTouche.databinding.RowListItemBinding
import ir.amozkade.advancedAsisstiveTouche.mvvm.BaseActivity
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.FloatingWindow
import ir.amozkade.advancedAsisstiveTouche.mvvm.preferenceActivity.SettingRepository
import ir.amozkade.advancedAsisstiveTouche.mvvm.themeManager.models.Theme
import ir.mobitrain.applicationcore.alertDialog.AlertDialogDelegate
import ir.mobitrain.applicationcore.alertDialog.CustomAlertDialog
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class OfflineThemeActivity : BaseActivity() {

    private lateinit var themes: java.util.ArrayList<Theme>
    private lateinit var mBinding: ActivityOfflineThemeBinding

    @Inject
    @AppDir
    lateinit var appDir:String

    @Inject
    lateinit var settingRepository: SettingRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_offline_theme)
        @Suppress("UNCHECKED_CAST")
        themes = intent.extras!!["themes"] as ArrayList<Theme>
        val type = themes.first().themeType
        mBinding.actionBar.setActionBarTitle(getString(if (type == "Button") R.string.offline_buttons else if (type == "Background") R.string.offline_backgrounds else R.string.offline_icon_fonts))
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        mBinding.rcv.adapter = OfflineThemeAdapter()
        (mBinding.rcv.adapter as OfflineThemeAdapter).notifyDataSetChanged()
    }

    fun openApplyAlertDialog(theme: Theme) {
        CustomAlertDialog.showDialog(this,
                title = theme.title,
                imageUrl = theme.thumbnailImageAddress,
                submitTitle = getString(R.string.apply),
                cancelTextColorId = ContextCompat.getColor(this, R.color.red),
                submitTextColorId = ContextCompat.getColor(this, R.color.primary_color),
                delegate = object : AlertDialogDelegate {
                    override fun alertCompletion(type: AlertDialogDelegate.AlertTapType,extraButton: MaterialButton?) {
                        if (type == AlertDialogDelegate.AlertTapType.Submit) {
                            restartButtonService(theme)
                        }
                    }
                }
        )
    }

    inner class OfflineThemeAdapter : RecyclerView.Adapter<OfflineThemeAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val mBinding = DataBindingUtil.inflate<RowListItemBinding>(LayoutInflater.from(this@OfflineThemeActivity), R.layout.row_list_item, null, false)
            return ViewHolder(mBinding)
        }

        override fun getItemCount(): Int {
            return themes.count()
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val theme = themes[holder.bindingAdapterPosition]
            holder.row.txtTitle.text = theme.title
            val fileName = if(theme.themeType == "Font"){
                theme.thumbnailImageAddress.split("/").last()
            }else{
                theme.url.split("/").last()
            }
            val bitmap = BitmapFactory.decodeFile("$appDir/$fileName")
            val drawable = BitmapDrawable(resources, bitmap)
            holder.row.img.setImageDrawable(drawable)
            holder.row.container.setOnClickListener { openApplyAlertDialog(theme) }
        }

        inner class ViewHolder(val row: RowListItemBinding) : RecyclerView.ViewHolder(row.root)
    }

    private fun restartButtonService(item: Theme) {
        GlobalScope.launch(IO){
            when (item.themeType) {
                "Button" -> {
                    settingRepository.setUserSelectedImageName(item.url.split("/").last())
                }
                "Background" -> {
                    settingRepository.setUserSelectedPanelImageName(item.url.split("/").last())
                }
                "Font" -> {
                    settingRepository.setUserSelectedFontName(item.url.split("/").last())
                }
            }
            withContext(Main){
                FloatingWindow.restartButtonService(this@OfflineThemeActivity,settingRepository)
            }
        }
    }
}