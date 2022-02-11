package ir.amozkade.advancedAsisstiveTouche.mvvm.editButtonPositions.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.databinding.RowAppInActionPickerBinding
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.models.ButtonModelInPanel
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.buttons.AppButton
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.buttons.AppDrawerButton
import ir.amozkade.advancedAsisstiveTouche.mvvm.editButtonPositions.ItemSelectDelegate
import java.lang.ref.WeakReference

class SimpleAppListAdapter(private val apps: ArrayList<AppDrawerButton.AppInfo>,
                           val delegate: ItemSelectDelegate,
                           private val bottomSheetFragment: WeakReference<GridEditButtonPositionsAdapter.Companion.BottomSheetFragment>,
                           private val selectedPositionInMenu: Int)
    : RecyclerView.Adapter<SimpleAppListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = DataBindingUtil.inflate<RowAppInActionPickerBinding>(inflater, R.layout.row_app_in_action_picker, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = apps.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val app = apps[holder.bindingAdapterPosition]
        holder.row.imgIcon.setImageDrawable(app.drawable)
        holder.row.txtTitle.text = app.appName
        val modelInPanel = ButtonModelInPanel(appName = app.appName, packageName = app.packageName, icon = app.drawable, button = AppButton(holder.row.root.context), buttonTypeName = ButtonModelInPanel.ButtonTypesName.APP)
        holder.row.root.setOnClickListener { bottomSheetFragment.get()?.dismiss(); delegate.onSelectButtonItem(modelInPanel, selectedPositionInMenu) }
    }

    open class ViewHolder(val row: RowAppInActionPickerBinding) : RecyclerView.ViewHolder(row.root)
}