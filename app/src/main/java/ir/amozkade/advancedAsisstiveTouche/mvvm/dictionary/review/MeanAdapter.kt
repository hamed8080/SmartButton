package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.review

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.databinding.RowTranslateBinding
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.translator.TranslateResult
import ir.mobitrain.applicationcore.helper.CommonHelpers


class MeanAdapter(private val means: ArrayList<TranslateResult>, val delegate: ReviewDelegate) : RecyclerView.Adapter<MeanAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = DataBindingUtil.inflate<RowTranslateBinding>(inflater, R.layout.row_translate, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return means.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mean = means[holder.bindingAdapterPosition]
        holder.bind(mean)
        holder.row.clickableView.setOnClickListener {
            delegate.onMeanItemTaped(mean)
        }
    }

    fun add(translateResult: TranslateResult) {
        means.add(translateResult)
        notifyItemInserted(means.size)
    }

    open class ViewHolder(val row: RowTranslateBinding) : RecyclerView.ViewHolder(row.root) {
        @SuppressLint("ClickableViewAccessibility")
        fun bind(translateResult: TranslateResult) {
            val isFa = CommonHelpers.textContainsArabic(translateResult.dbName)
            row.txtDicName.text = translateResult.dbName
            row.txtDicName.typeface = ResourcesCompat.getFont(row.root.context, if (isFa) R.font.iransans_bold else R.font.sf_pro_rounded_bold)
            val html = """
            <html dir="rtl">
             <head>
            <meta name="viewport" content="initial-scale=1.0" />
            <meta http-equiv="Content-Type" content="text/html; charset=utf-8"  />
            <style>body{text-align:right}</style>
            <style>@font-face { font-family: "IranSans" ; src: url("font/iransans.ttf"); }</style>
            <style>*{font-size:12px;font-family:IranSans,'IranSans',tahoma; line-height: 18px;}</style>
            </head>
            <body style="padding:5px;">
            """ + translateResult.result + """
            </body>
            </html>
            """
            row.wbResult.isVerticalScrollBarEnabled = false
            row.wbResult.loadDataWithBaseURL("file:///android_res/", html, "text/html", "UTF-8", "")
            row.wbResult.isEnabled = false
        }
    }
}
