package ir.amozkade.advancedAsisstiveTouche.mvvm.language

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.databinding.RowLanguageBinding

class LanguageAdapter(private val languages: List<Language> ,val vm:LanguageViewModel) : RecyclerView.Adapter<LanguageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val row = DataBindingUtil.inflate<RowLanguageBinding>(inflater, R.layout.row_language, parent, false)
        return ViewHolder(row)
    }

    override fun getItemCount(): Int = languages.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(languages[holder.bindingAdapterPosition] ,vm)
    }

    class ViewHolder(private val rowLanguageBinding: RowLanguageBinding) : RecyclerView.ViewHolder(rowLanguageBinding.root) {

        fun bind(language:Language , vm:LanguageViewModel){
            rowLanguageBinding.language = language
            rowLanguageBinding.txtLanguage.text = language.name
            rowLanguageBinding.vm = vm
        }
    }

}