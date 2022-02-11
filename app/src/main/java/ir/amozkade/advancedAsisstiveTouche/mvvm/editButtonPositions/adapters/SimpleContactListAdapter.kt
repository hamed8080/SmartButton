package ir.amozkade.advancedAsisstiveTouche.mvvm.editButtonPositions.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.databinding.RowContactInActionPickerBinding
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.models.ButtonModelInPanel
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.buttons.ContactButton
import ir.amozkade.advancedAsisstiveTouche.mvvm.editButtonPositions.ItemSelectDelegate
import java.lang.ref.WeakReference

class SimpleContactListAdapter(private val contacts: ArrayList<ContactButton.Contact>,
                               val delegate: ItemSelectDelegate,
                               private val bottomSheetFragment: WeakReference<GridEditButtonPositionsAdapter.Companion.BottomSheetFragment>,
                               private val selectedPositionInMenu: Int) : RecyclerView.Adapter<SimpleContactListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = DataBindingUtil.inflate<RowContactInActionPickerBinding>(inflater, R.layout.row_contact_in_action_picker, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = contacts.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contact = contacts[holder.bindingAdapterPosition]
        holder.row.txtTitle.text = contact.name
        if (contact.icon !=null){
            holder.row.imgIcon.setImageDrawable(contact.icon)
        }else{
            holder.row.imgIcon.setImageDrawable(ContextCompat.getDrawable(holder.row.root.context , R.drawable.ic_no_contact_image))
        }
        val modelInPanel = ButtonModelInPanel(contactName =  contact.name, phoneNumber = contact.phoneNumber,  icon = contact.icon, imageUri = contact.imageUri, button = ContactButton(holder.row.root.context), buttonTypeName = ButtonModelInPanel.ButtonTypesName.CONTACT)
        holder.row.root.setOnClickListener { bottomSheetFragment.get()?.dismiss(); delegate.onSelectButtonItem(modelInPanel, selectedPositionInMenu) }
    }

    open class ViewHolder(val row: RowContactInActionPickerBinding) : RecyclerView.ViewHolder(row.root)
}