package ir.amozkade.advancedAsisstiveTouche.mvvm.themeManager.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.databinding.RowListItemBinding
import ir.amozkade.advancedAsisstiveTouche.helper.bindings.ImageViewBinding
import ir.amozkade.advancedAsisstiveTouche.mvvm.themeManager.models.ThemePack
import ir.mobitrain.applicationcore.helper.Converters

class ThemePackAdapter(private val themePacks: List<ThemePack>, private val listener: SectionAdapter.OnItemClickListener<*>)
    : RecyclerView.Adapter<ThemePackAdapter.ThemePackViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThemePackViewHolder {
        val binding = DataBindingUtil.inflate<RowListItemBinding>(LayoutInflater.from(parent.context), R.layout.row_list_item, parent, false)
        return ThemePackViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ThemePackViewHolder, position: Int) {
        val themePack = themePacks[position]
        holder.bind(themePack)
    }

    override fun getItemCount(): Int = themePacks.size


    inner class ThemePackViewHolder(val row: RowListItemBinding) : RecyclerView.ViewHolder(row.root) {

        fun bind(themePack: ThemePack) {
            row.txtTitle.text = themePack.title
            row.container.setOnClickListener { listener.onThemePackClick(themePack) }
            ImageViewBinding.setImageWithUrl(row.img, themePack.thumbnailUrl)
            row.root.layoutParams = ViewGroup.LayoutParams(Converters.convertIntToDP(172, row.root.context).toInt(), Converters.convertIntToDP(226, row.root.context).toInt())
        }
    }

}