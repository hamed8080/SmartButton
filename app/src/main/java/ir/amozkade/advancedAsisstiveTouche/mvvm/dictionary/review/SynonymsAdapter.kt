package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.review

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.databinding.*
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.review.models.Synonym

class SynonymsAdapter(private val synonyms: List<Synonym>) : RecyclerView.Adapter<SynonymsAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = DataBindingUtil.inflate<RowSynonymBinding>(inflater, R.layout.row_synonym, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return synonyms.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val synonym = synonyms[holder.bindingAdapterPosition]
        holder.bind(synonym.synonym)
    }

    open class ViewHolder(val row: RowSynonymBinding) : RecyclerView.ViewHolder(row.root) {
        fun bind(synonym: String?) {
            row.txtSynonym.text = synonym
        }
    }
}
