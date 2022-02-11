package ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton

import android.annotation.SuppressLint
import android.content.Context
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.databinding.RowButtonInPanelBinding
import ir.amozkade.advancedAsisstiveTouche.helper.customviews.CircularTextViewWithBorder
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.delegates.AssistiveButtonDelegate
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.delegates.EnableDelegate
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.models.ButtonModelInPanel
import ir.amozkade.advancedAsisstiveTouche.mvvm.preferenceActivity.SettingRepository
import ir.mobitrain.applicationcore.helper.Converters
import kotlin.math.abs

class GridButtonAdapter(private val buttons: ArrayList<ButtonModelInPanel>,
                        private val settingRepository: SettingRepository,
                        val listener: OnButtonInPanelClickListener) : RecyclerView.Adapter<GridButtonAdapter.ViewHolder>() {

    interface OnButtonInPanelClickListener {
        fun onClickItemInPanel(button: AssistiveButtonDelegate, buttonModel: ButtonModelInPanel)
        fun onLongClickItemInPanel(button: AssistiveButtonDelegate, buttonModel: ButtonModelInPanel)
    }

    //    theme needed if remove theme app crash in view pager mode
    private val contextThemeWrapper = ContextThemeWrapper(listener as FloatingWindow, R.style.AppTheme)
    private val inflater = contextThemeWrapper.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val iconWidth: Int
    private val iconHeight: Int
    private var circularButtons: ArrayList<CircularTextViewWithBorder> = ArrayList()

    init {
        iconWidth = when {
            settingRepository.getCashedModel().isLeftMenu -> {
                Converters.convertIntToDP(44, contextThemeWrapper).toInt()
            }
            settingRepository.getCashedModel().pagerEnable -> {
                val padding = Converters.convertIntToDP(24, contextThemeWrapper).toInt() * 2 // * 2 for left and right padding in FloatingWindow Class
                ((contextThemeWrapper.resources.displayMetrics.widthPixels - Converters.convertIntToDP(24, contextThemeWrapper).toInt()) - padding) / 3
            }
            else -> {
                val padding = Converters.convertIntToDP(24, contextThemeWrapper).toInt() * 2 // * 2 for left and right padding in FloatingWindow Class
                ((contextThemeWrapper.resources.displayMetrics.widthPixels * ((settingRepository.getCashedModel().panelWidthPercent) / 100f)).toInt() - padding) / 3
            }
        }

        iconHeight = if (settingRepository.getCashedModel().isLeftMenu) {
            Converters.convertIntToDP(82, contextThemeWrapper).toInt()
        } else {
            val panelIconSize = Converters.convertIntToDP(settingRepository.getCashedModel().panelButtonsIconSize, contextThemeWrapper).toInt()
            val panelTextSize = Converters.convertIntToSP(settingRepository.getCashedModel().panelButtonsTextSize, contextThemeWrapper).toInt()
            val margin = Converters.convertIntToSP(contextThemeWrapper.resources.getDimension(R.dimen.paddingAllLayout2X).toInt(), contextThemeWrapper).toInt() / 4
            abs(panelIconSize + panelTextSize + margin)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<RowButtonInPanelBinding>(inflater, R.layout.row_button_in_panel, parent, false)
        binding.circularButton.init(settingRepository)
        binding.root.layoutParams.width = iconWidth
        binding.root.layoutParams.height = iconHeight
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return buttons.size
    }

    @SuppressLint("DefaultLocale")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val button = buttons[holder.bindingAdapterPosition]
        holder.bind(button)
        holder.row.circularButton.drawButton(button, showsEmptyActions = false)
        holder.row.root.setOnClickListener {
            button.button?.let {
                listener.onClickItemInPanel(it, buttonModel = button)
            }
        }
        holder.row.circularButton.setOnLongClickListener {
            button.button?.let {
                listener.onLongClickItemInPanel(it, buttonModel = button)
            }
            return@setOnLongClickListener true
        }
        circularButtons.add(holder.row.circularButton)
    }

    fun updateEnableDisableButtonForIndex(index: Int, isEnable: Boolean) {
        val button = buttons[index]
        (button.button as? EnableDelegate)?.isEnable = isEnable
        notifyItemChanged(index)
    }

    open class ViewHolder(val row: RowButtonInPanelBinding) : RecyclerView.ViewHolder(row.root) {
        fun bind(buttonModel: ButtonModelInPanel) {
            row.buttonModel = buttonModel
        }
    }

    fun onDestroyCalled() {
        circularButtons.forEach {
            it.onDestroyCalled()
        }
    }
}