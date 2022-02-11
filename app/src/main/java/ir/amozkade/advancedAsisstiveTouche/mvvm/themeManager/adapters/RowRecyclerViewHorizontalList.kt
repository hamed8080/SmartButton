package ir.amozkade.advancedAsisstiveTouche.mvvm.themeManager.adapters

import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.databinding.RowListItemBinding
import ir.amozkade.advancedAsisstiveTouche.databinding.RowRecyclerViewHorizontalListBinding
import ir.amozkade.advancedAsisstiveTouche.helper.bindings.ImageViewBinding
import ir.amozkade.advancedAsisstiveTouche.mvvm.themeManager.models.Theme
import ir.mobitrain.applicationcore.helper.Converters

class RowRecyclerViewHorizontalList<T : Parcelable>(val list: List<T>, val container: ConstraintLayout, private val clickListener: SectionAdapter.OnItemClickListener<T>) {

    private lateinit var mBinding: RowRecyclerViewHorizontalListBinding

    init {
        setupAdapter()
    }

    private fun setupAdapter() {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(container.context), R.layout.row_recycler_view_horizontal_list, null, false)
        mBinding.rcv.adapter = RecyclerViewListAdapterInSection(list, clickListener)
        (mBinding.rcv.adapter as RecyclerViewListAdapterInSection<*>).notifyDataSetChanged()
        container.addView(mBinding.root)
    }

    class RecyclerViewListAdapterInSection<T : Parcelable>(val list: List<T>, private val clickListener: SectionAdapter.OnItemClickListener<T>) : RecyclerView.Adapter<RecyclerViewListAdapterInSection.ViewHolder>() {


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = DataBindingUtil.inflate<RowListItemBinding>(LayoutInflater.from(parent.context), R.layout.row_list_item, null, false)
            return ViewHolder(binding)
        }

        override fun getItemCount(): Int = list.count()

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val theme = list[holder.bindingAdapterPosition] as Theme
            holder.row.txtTitle.text = theme.title
            holder.row.container.setOnClickListener { clickListener.onClickItem(list[holder.bindingAdapterPosition]) }
            ImageViewBinding.setImageWithUrl(holder.row.img, theme.thumbnailImageAddress)
            holder.row.root.layoutParams = ViewGroup.LayoutParams(Converters.convertIntToDP(172, holder.row.root.context).toInt(), Converters.convertIntToDP(226, holder.row.root.context).toInt())
        }

        class ViewHolder(val row: RowListItemBinding) : RecyclerView.ViewHolder(row.root)
    }

}