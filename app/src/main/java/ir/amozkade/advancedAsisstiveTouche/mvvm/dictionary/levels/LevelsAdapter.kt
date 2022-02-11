package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.levels

import android.annotation.SuppressLint
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.databinding.RowLevelBinding
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.leitners.models.LeitnerLevels


class LevelsAdapter(private var levels: ArrayList<LeitnerLevels>, private val onLevelListener: OnLevelListener) : RecyclerView.Adapter<LevelsAdapter.ViewHolder>() {

    interface OnLevelListener {
        fun onLevelTaped(level: LeitnerLevels)
        fun onLongPressOnLevel(level: LeitnerLevels)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = parent.context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val row: RowLevelBinding = DataBindingUtil.inflate(inflater, R.layout.row_level, parent, false)
        return ViewHolder(row)
    }

    override fun getItemCount(): Int = levels.count()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val level = levels[position]
        holder.bind(level, onLevelListener)
        if (level.level == 1) {
            holder.row.topCircle.visibility = View.GONE
            holder.row.bottomCircle.setTitle("${level.level}")
        } else {
            holder.row.topCircle.setTitle("${level.level}")
            if (level.level == levels.count()) {
                holder.row.bottomCircle.visibility = View.GONE
                holder.row.bottomLine.visibility = View.GONE
            }else{
                holder.row.bottomCircle.setTitle("${level.level}")
                holder.row.topCircle.visibility = View.VISIBLE
                holder.row.bottomCircle.visibility = View.VISIBLE
                holder.row.bottomLine.visibility = View.VISIBLE
            }
        }
    }

    fun updateLevelTime(day: Int, level: LeitnerLevels) {
        val index = levels.indexOf(level)
        levels[index].time = day
    }

    inner class ViewHolder(val row: RowLevelBinding) : RecyclerView.ViewHolder(row.root) {
        private val rowBinding = row

        @SuppressLint("ClickableViewAccessibility")
        fun bind(level: LeitnerLevels, onLevelListener: OnLevelListener) {
            rowBinding.level = level
            rowBinding.listener = onLevelListener
            rowBinding.cv.setOnLongClickListener {
                onLevelListener.onLongPressOnLevel(level)
                true
            }
        }
    }
}