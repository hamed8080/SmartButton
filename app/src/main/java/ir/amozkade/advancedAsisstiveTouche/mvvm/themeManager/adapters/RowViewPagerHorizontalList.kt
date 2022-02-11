package ir.amozkade.advancedAsisstiveTouche.mvvm.themeManager.adapters

import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.databinding.RowSliderBinding
import ir.amozkade.advancedAsisstiveTouche.databinding.RowSliderHorizontalListBinding
import ir.amozkade.advancedAsisstiveTouche.helper.bindings.ImageViewBinding
import ir.amozkade.advancedAsisstiveTouche.mvvm.themeManager.models.Theme
import ir.mobitrain.applicationcore.helper.Converters

class RowViewPagerHorizontalList<T : Parcelable>(val list: List<T>, val container: ConstraintLayout, private val clickListener: SectionAdapter.OnItemClickListener<T>) {

    private lateinit var mBinding: RowSliderHorizontalListBinding

    init {
        setupAdapter()
    }

    private fun setupAdapter() {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(container.context), R.layout.row_slider_horizontal_list, null, false)
        mBinding.rcv.adapter = SliderHorizontalAdapterInSection(list, container, clickListener)
        (mBinding.rcv.adapter as SliderHorizontalAdapterInSection<*>).notifyDataSetChanged()
        container.addView(mBinding.root)
        if (list.count() >= 3) {
            val margin = Converters.convertIntToDP(24, container.context)
            (mBinding.rcv.layoutManager as LinearLayoutManager).scrollToPositionWithOffset((list.count() / 2), margin.toInt() * 3)
        }
    }

    class SliderHorizontalAdapterInSection<T : Parcelable>(val list: List<T>, val container: ConstraintLayout, private val clickListener: SectionAdapter.OnItemClickListener<T>) : RecyclerView.Adapter<SliderHorizontalAdapterInSection.ViewHolder>() {


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = DataBindingUtil.inflate<RowSliderBinding>(LayoutInflater.from(parent.context), R.layout.row_slider, null, false)
            return ViewHolder(binding)
        }

        override fun getItemCount(): Int {
            return list.count()
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val theme = list[position] as Theme
            holder.row.txtTitle.text = theme.title
            holder.row.root.setOnClickListener { clickListener.onClickItem(list[holder.bindingAdapterPosition]) }
            holder.row.root.layoutParams = ViewGroup.LayoutParams(Converters.convertIntToDP(236, holder.row.root.context).toInt(), Converters.convertIntToDP(272, holder.row.root.context).toInt())
            ImageViewBinding.setImageWithUrl(holder.row.img, theme.thumbnailImageAddress)
        }

        class ViewHolder(val row: RowSliderBinding) : RecyclerView.ViewHolder(row.root)

    }

}