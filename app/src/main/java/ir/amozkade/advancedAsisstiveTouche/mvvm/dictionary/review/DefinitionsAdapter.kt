package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.review

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.databinding.*

class DefinitionsAdapter(private val definitions: List<String>) : RecyclerView.Adapter<DefinitionsAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = DataBindingUtil.inflate<RowDefinitionBinding>(inflater, R.layout.row_definition, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return definitions.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val definition = definitions[holder.bindingAdapterPosition]
        holder.bind(definition)
    }

    open class ViewHolder(val row: RowDefinitionBinding) : RecyclerView.ViewHolder(row.root) {
        fun bind(definition: String) {
            row.txtDefinition.text = definition
                    .replace("adv\t", "")
                    .replace("u\t", "")
                    .replace("adj\t", "")
                    .replace("v\t", "")
                    .replace("n\t", "")
        }
    }
}
