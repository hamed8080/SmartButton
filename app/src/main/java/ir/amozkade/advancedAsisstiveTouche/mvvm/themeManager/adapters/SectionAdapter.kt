package ir.amozkade.advancedAsisstiveTouche.mvvm.themeManager.adapters

import android.os.Parcelable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.databinding.RowOfflineThemeBinding
import ir.amozkade.advancedAsisstiveTouche.databinding.RowSectionBinding
import ir.amozkade.advancedAsisstiveTouche.databinding.RowThemePacksBinding
import ir.amozkade.advancedAsisstiveTouche.mvvm.themeManager.models.DisplayType
import ir.amozkade.advancedAsisstiveTouche.mvvm.themeManager.models.Section
import ir.amozkade.advancedAsisstiveTouche.mvvm.themeManager.models.Theme
import ir.amozkade.advancedAsisstiveTouche.mvvm.themeManager.models.ThemePack
import java.util.*

class SectionAdapter<T : Parcelable>(
        private val themePacks: List<ThemePack>, private val sections: List<Section<T>>,
        private val offlineThemesList: List<Theme>,
        private val clickListener: OnItemClickListener<T>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val offlineThemes: Int = 0
    private val themePacksPosition: Int = 1

    interface OnItemClickListener<T> {
        fun onClickItem(item: T)
        fun onOfflineThemeClick(themes: List<Theme>)
        fun onThemePackClick(themePack: ThemePack)
        fun onDefaultThemeClick()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == offlineThemes) {
            val binding = DataBindingUtil.inflate<RowOfflineThemeBinding>(LayoutInflater.from(parent.context), R.layout.row_offline_theme, parent, false)
            return OfflineViewHolder(binding)
        }
        if (viewType == themePacksPosition) {
            val binding = DataBindingUtil.inflate<RowThemePacksBinding>(LayoutInflater.from(parent.context), R.layout.row_theme_packs, parent, false)
            return ThemePacksViewHolder(binding)
        }
        val binding = DataBindingUtil.inflate<RowSectionBinding>(LayoutInflater.from(parent.context), R.layout.row_section, null, false)
        return ViewHolder(binding)
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0) {
            return offlineThemes
        }
        if (position == 1) {
            return themePacksPosition
        }
        return position
    }

    override fun getItemCount(): Int {
        return sections.count() + 2 // one for offline themes and one for theme packs
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (position) {
            0 -> {
                (holder as? OfflineViewHolder)?.let {
                    val buttons = offlineThemesList.filter { it.themeType == "Button" }
                    val backgrounds = offlineThemesList.filter { it.themeType == "Background" }
                    val fonts = offlineThemesList.filter { it.themeType == "Font" }
                    holder.row.btnOfflineButtons.setOnClickListener { clickListener.onOfflineThemeClick(buttons) }
                    holder.row.btnOfflineBackgrounds.setOnClickListener { clickListener.onOfflineThemeClick(backgrounds) }
                    holder.row.btnOfflineIconsPanel.setOnClickListener { clickListener.onOfflineThemeClick(fonts) }
                    holder.row.btnDefault.setOnClickListener { clickListener.onDefaultThemeClick() }
                }

            }
            1 -> {
                (holder as? ThemePacksViewHolder)?.let {
                    holder.row.rcvThemePacks.run {
                        adapter = ThemePackAdapter(themePacks , clickListener)
                        adapter?.notifyDataSetChanged()
                    }
                }
            }
            else -> {
                val section = sections[holder.bindingAdapterPosition - 2]
                (holder as? ViewHolder)?.let {
                    holder.row.txtSection.text = if (Locale.getDefault().language.contains("fa")) section.faTitle else section.title
                    if (section.displayType == DisplayType.List) {
                        @Suppress("IMPLICIT_CAST_TO_ANY")
                        (RowRecyclerViewHorizontalList(section.list, holder.row.container, clickListener))
                    } else {
                        @Suppress("IMPLICIT_CAST_TO_ANY")
                        (RowViewPagerHorizontalList(section.list, holder.row.container, clickListener))
                    }
                }
            }
        }
    }

    class ViewHolder(val row: RowSectionBinding) : RecyclerView.ViewHolder(row.root)
    class OfflineViewHolder(val row: RowOfflineThemeBinding) : RecyclerView.ViewHolder(row.root)
    class ThemePacksViewHolder(val row: RowThemePacksBinding) : RecyclerView.ViewHolder(row.root)
}