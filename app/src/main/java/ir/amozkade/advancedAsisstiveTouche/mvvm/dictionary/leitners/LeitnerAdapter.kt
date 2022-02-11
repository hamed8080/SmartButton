package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.leitners

import android.annotation.SuppressLint
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.databinding.RowLeitnerBinding
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.leitners.models.Leitner

class LeitnerAdapter(var leitners: ArrayList<Leitner>, private val leitnerListener:LeitnerListener) : RecyclerView.Adapter<LeitnerAdapter.ViewHolder>() {

    interface LeitnerListener {
        fun onClickLeitner(leitner: Leitner)
        fun onLongPressOnLeitner(leitner: Leitner)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = parent.context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val row: RowLeitnerBinding = DataBindingUtil.inflate(inflater, R.layout.row_leitner, parent, false)
        return ViewHolder(row)
    }

    override fun getItemCount(): Int = leitners.count()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = leitners[position]
        holder.bind(product, leitnerListener)
    }

    fun deleteLeitner(leitner: Leitner) {
        val index = leitners.indexOf(leitner)
        leitners.remove(leitner)
        notifyItemRemoved(index)
    }

    inner class ViewHolder(row: RowLeitnerBinding) : RecyclerView.ViewHolder(row.root) {
        private val rowBinding = row

        @SuppressLint("ClickableViewAccessibility")
        fun bind(leitner: Leitner, leitnerDelegate: LeitnerListener) {
            rowBinding.leitner = leitner
            rowBinding.root.setOnClickListener {
                leitnerDelegate.onClickLeitner(leitner)
            }
            rowBinding.root.setOnLongClickListener {
                leitnerDelegate.onLongPressOnLeitner(leitner)
                true
            }
        }
    }
}