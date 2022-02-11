package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.schedule


import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.databinding.RowSectionBinding
import ir.amozkade.advancedAsisstiveTouche.mvvm.BaseActivityDelegate
import java.lang.ref.WeakReference

class ScheduleAdapter(@Suppress("UNUSED_PARAMETER") baseActivityDelegate: BaseActivityDelegate, val delegate: ScheduleDelegate,
                      private val cto: WeakReference<LifecycleOwner>) : RecyclerView.Adapter<ScheduleAdapter.ViewHolder>() {




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = parent.context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val row: RowSectionBinding = DataBindingUtil.inflate(inflater, R.layout.row_section, parent, false)
        return ViewHolder(row)
    }

    override fun getItemCount(): Int = 5

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }

    inner class ViewHolder(row: RowSectionBinding) : RecyclerView.ViewHolder(row.root) {
        private val mBinding = row
        val inflater: LayoutInflater = mBinding.root.context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }
}