package ir.amozkade.advancedAsisstiveTouche.mvvm.editButtonPositions.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.databinding.RowShortcutInActionPickerBinding
import ir.amozkade.advancedAsisstiveTouche.mvvm.BaseActivityDelegate
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.models.AndroidCodeNames
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.models.ButtonModelInPanel
import ir.amozkade.advancedAsisstiveTouche.mvvm.editButtonPositions.ItemSelectDelegate
import ir.amozkade.advancedAsisstiveTouche.mvvm.preferenceActivity.SettingRepository
import java.lang.ref.WeakReference
import java.util.*
import kotlin.collections.ArrayList

class SimpleShortcutListAdapter(
        private val buttons: ArrayList<ButtonModelInPanel>,
        private val bottomSheetFragment: WeakReference<GridEditButtonPositionsAdapter.Companion.BottomSheetFragment>,
        val delegate: ItemSelectDelegate,
        private val selectedPositionInMenu: Int,
        private val settingRepository: SettingRepository,
        private val context: Context
) : RecyclerView.Adapter<SimpleShortcutListAdapter.ViewHolder>() {

    private val typeface = Typeface.createFromAsset(context.resources.assets, "fonts/sfSymbol.ttf")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = DataBindingUtil.inflate<RowShortcutInActionPickerBinding>(inflater, R.layout.row_shortcut_in_action_picker, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = buttons.size

    @SuppressLint("DefaultLocale", "SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val button = buttons[holder.bindingAdapterPosition]

        holder.row.txtIcon.typeface = typeface
        holder.row.txtIcon.text = button.unicodeIcon
        button.titleId?.let {
            holder.row.txtTitle.text =
                ButtonModelInPanel.getLocalizedStringForVariableName(button.titleId, context)
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        }
        holder.row.txtRequiredApi.text = if (button.requiredApi != null) "${AndroidCodeNames.convertApiIntToAndroidVersionNumber(button.requiredApi)}+" else ""
        holder.row.txtRoot.text = if (button.needRoot) context.getString(R.string.root_needed) else ""
        holder.row.btnShortcutAction.setOnClickListener {
            bottomSheetFragment.get()?.dismiss()
            if (button.requiredApi != null) {
                if (Build.VERSION.SDK_INT >= button.requiredApi) {
                    delegate.onSelectButtonItem(button, selectedPositionInMenu)
                } else {
                    (context as BaseActivityDelegate).showWarn(context.getString(R.string.sorry), context.getString(R.string.not_compatible), R.drawable.img_not_compatible)
                }
            } else {
                delegate.onSelectButtonItem(button, selectedPositionInMenu)
            }
        }
    }

    open class ViewHolder(val row: RowShortcutInActionPickerBinding) : RecyclerView.ViewHolder(row.root)
}