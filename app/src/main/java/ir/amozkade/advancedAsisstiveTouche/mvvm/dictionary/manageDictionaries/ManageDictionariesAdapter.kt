package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.manageDictionaries

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.databinding.*
import ir.mobitrain.applicationcore.helper.CommonHelpers
import java.util.*

class ManageDictionariesAdapter(private val dictionaries: List<Dictionary> , val delegate: ManageDictionaryDelegate) : RecyclerView.Adapter<ManageDictionariesAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = DataBindingUtil.inflate<RowDictionaryBinding>(inflater, R.layout.row_dictionary, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dictionaries.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dictionary = dictionaries[holder.bindingAdapterPosition]
        holder.bind(dictionary)
        holder.row.btnAddToLeitner.setOnClickListener {
            delegate.tapeOnAddAllToLeitner(dictionary)
        }
        holder.row.btnDelete.setOnClickListener {
            delegate.onDeleteDictionaryTaped(dictionary)
        }
    }

    open class ViewHolder(val row: RowDictionaryBinding) : RecyclerView.ViewHolder(row.root) {
        @SuppressLint("RtlHardcoded")
        fun bind(dictionary: Dictionary) {
            row.model =  dictionary
            if (CommonHelpers.textContainsArabic(dictionary.name)) {
                row.txtName.typeface = ResourcesCompat.getFont(row.root.context, R.font.iransans_bold)
                if (!Locale.getDefault().language.contains("fa")) {
                    row.txtName.gravity = Gravity.RIGHT
                }
            }
        }
    }
}
