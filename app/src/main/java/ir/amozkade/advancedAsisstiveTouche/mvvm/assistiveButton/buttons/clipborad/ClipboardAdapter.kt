package ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.buttons.clipborad

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.databinding.RowClipboardBinding
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.FloatingWindow
import ir.amozkade.advancedAsisstiveTouche.mvvm.preferenceActivity.SettingRepository
import ir.mobitrain.applicationcore.helper.CommonHelpers
import java.util.*
import kotlin.collections.ArrayList

interface ClipboardDelegate{
    fun onSelectClipboard(clipboard: Clipboard)
    fun deleteFromDatabaseAndNotify(clipboard: Clipboard)
}
class ClipboardAdapter(private val clipboards: ArrayList<Clipboard>,
                       private val recyclerView: RecyclerView,
                       private val settingRepository: SettingRepository,
                       private val clipboardDelegate: ClipboardDelegate) : RecyclerView.Adapter<ClipboardAdapter.ViewHolder>() {

    private val panelTextColor = settingRepository.getCashedModel().panelButtonsColor
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClipboardAdapter.ViewHolder {
        val inflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val row: RowClipboardBinding = DataBindingUtil.inflate(inflater, R.layout.row_clipboard, parent, false)
        return ViewHolder(row)
    }

    override fun getItemCount(): Int = clipboards.count()

    override fun onBindViewHolder(holder: ClipboardAdapter.ViewHolder, position: Int) {
        val clipboard = clipboards[holder.bindingAdapterPosition]
        val context = holder.row.root.context
        holder.row.txt.text = clipboard.text
        holder.row.btnDelete.setOnClickListener {
            deleteFromDatabaseAndNotify(clipboard)
            FloatingWindow.floatingWindowService?.isFromSubWindowItem = true
        }
        holder.row.root.setOnClickListener {
            clipboardDelegate.onSelectClipboard(clipboard)
        }
        val isFa = Locale.getDefault().language.contains("fa") || CommonHelpers.textContainsArabic(clipboard.text ?: "")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            holder.row.txt.textDirection = if(isFa) TextView.TEXT_DIRECTION_RTL else TextView.TEXT_DIRECTION_LTR
            holder.row.txt.typeface = ResourcesCompat.getFont(context , if(isFa) R.font.iransans_bold else R.font.sf_pro_rounded_bold)
        }
        holder.row.txt.setTextColor(panelTextColor)
        holder.row.btnDelete.setTextColor(panelTextColor)
    }

    private fun deleteFromDatabaseAndNotify(clipboard: Clipboard) {
        val index = clipboards.indexOf(clipboard)
        clipboards.remove(clipboard)
        notifyItemRemoved(index)
        clipboardDelegate.deleteFromDatabaseAndNotify(clipboard)
    }

    fun addToTop(clipboard: Clipboard) {
        if(clipboards.count()>0){
            clipboards.add(0 , clipboard)
        }else{
            clipboards.add(clipboard)
        }
        notifyItemInserted(0)
        recyclerView.scrollToPosition(0)
    }

    inner class ViewHolder(val row: RowClipboardBinding) : RecyclerView.ViewHolder(row.root)
}