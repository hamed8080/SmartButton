package ir.amozkade.advancedAsisstiveTouche.mvvm.editButtonPositions.adapters

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.databinding.*
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.helperActivities.AppDrawerActivity
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.buttons.ContactButton
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.buttons.NoActionButton
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.models.ButtonModelInPanel
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.models.DefaultButtons
import ir.amozkade.advancedAsisstiveTouche.mvvm.editButtonPositions.ItemSelectDelegate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import ir.amozkade.advancedAsisstiveTouche.R.dimen.paddingAllLayout2X
import ir.amozkade.advancedAsisstiveTouche.mvvm.preferenceActivity.SettingRepository

class GridEditButtonPositionsAdapter(
        val buttons: ArrayList<ButtonModelInPanel>,
        private val defaultButtons: DefaultButtons,
        private val settingRepository: SettingRepository,
        val context: Context

) : RecyclerView.Adapter<GridEditButtonPositionsAdapter.ViewHolder>(), ItemSelectDelegate {

    private val iconWidth: Int = (context.resources.displayMetrics.widthPixels - (context.resources.getDimension(paddingAllLayout2X) * 2)).toInt() / 3
    private val iconHeight: Int = ir.mobitrain.applicationcore.helper.Converters.convertIntToDP(96, context).toInt()


    fun getEditedPositionOfButtons(): ArrayList<ButtonModelInPanel> {
        return buttons
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = DataBindingUtil.inflate<RowButtonInPanelBinding>(inflater, R.layout.row_button_in_panel, parent, false)
        binding.circularButton.init(settingRepository)
        binding.root.layoutParams.width = iconWidth
        binding.root.layoutParams.height = iconHeight
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return buttons.size
    }

    @SuppressLint("DefaultLocale")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val button = buttons[holder.bindingAdapterPosition]
        holder.bind(button)
        holder.row.circularButton.drawButton(button, true)
        holder.row.root.setOnClickListener {
            button.button?.let {
                openChoseActionAlert(context, defaultButtons, this, holder.bindingAdapterPosition,settingRepository)
            }
        }
    }

    override fun onSelectButtonItem(button: ButtonModelInPanel, selectedPositionInMenu: Int) {
        buttons[selectedPositionInMenu] = button
        notifyItemChanged(selectedPositionInMenu)
    }

    override fun onDeleteButton(selectedPositionInMenu: Int) {
        buttons.removeAt(selectedPositionInMenu)
        notifyItemRemoved(selectedPositionInMenu)
    }

    companion object {

        fun openChoseActionAlert(context: Context, defaultButtons: DefaultButtons, delegate: ItemSelectDelegate, position: Int , settingRepository: SettingRepository,showDelete: Boolean = true) {
            val pair = getDialog(R.layout.alert_action_select_in_edit_position, context)
            val binding = pair.second as AlertActionSelectInEditPositionBinding
            val bottomSheetFragment = WeakReference(BottomSheetFragment(binding))
            binding.btnEditShortcuts.setOnClickListener { openPickShortcutDialog(context, defaultButtons, delegate, position,settingRepository); bottomSheetFragment.get()?.dismiss() }
            binding.btnAddApp.setOnClickListener { openPickAppDialog(context, delegate, position); bottomSheetFragment.get()?.dismiss() }
            binding.btnAddContact.setOnClickListener { openPickContactDialog(context, delegate, position);bottomSheetFragment.get()?.dismiss() }
            binding.btnNoAction.setOnClickListener { bottomSheetFragment.get()?.dismiss(); delegate.onSelectButtonItem(ButtonModelInPanel(buttonTypeName = ButtonModelInPanel.ButtonTypesName.NO_ACTION, button = NoActionButton(), unicodeIcon = "\uF140"), position) }
            if (!showDelete) {
                binding.btnDelete.visibility = View.GONE
            }
            binding.btnDelete.setOnClickListener { bottomSheetFragment.get()?.dismiss();delegate.onDeleteButton(position) }
            bottomSheetFragment.get()?.show((context as AppCompatActivity).supportFragmentManager, bottomSheetFragment.get()?.tag)
            pair.first.setView(binding.root)
        }

        private fun openPickShortcutDialog(context: Context, defaultButtons: DefaultButtons, delegate: ItemSelectDelegate, position: Int , settingRepository: SettingRepository) {
            val pair = getDialog(R.layout.pick_shortcut, context)
            val binding = pair.second as PickShortcutBinding
            val bottomSheetFragment = WeakReference(BottomSheetFragment(binding))
            bottomSheetFragment.get()?.show((context as AppCompatActivity).supportFragmentManager, bottomSheetFragment.get()?.tag)
            binding.rcv.adapter = SimpleShortcutListAdapter(defaultButtons.getDefaultButtons(), bottomSheetFragment, delegate, position, settingRepository ,  context)
            (binding.rcv.adapter as SimpleShortcutListAdapter).notifyDataSetChanged()
        }

        private fun openPickAppDialog(context: Context, delegate: ItemSelectDelegate, position: Int) {
            val pair = getDialog(R.layout.pick_app, context)
            val binding = pair.second as PickAppBinding
            val bottomSheetFragment = WeakReference(BottomSheetFragment(binding))
            WeakReference((context as AppCompatActivity).supportFragmentManager).get()?.let {
                bottomSheetFragment.get()?.show(it, bottomSheetFragment.get()?.tag)
                GlobalScope.launch {
                    val apps = AppDrawerActivity.getAllAppsWithIcon(context)
                    GlobalScope.launch(Dispatchers.Main) {
                        binding.progressBar.visibility = View.GONE
                        binding.rcv.adapter = SimpleAppListAdapter(apps, delegate, bottomSheetFragment, position)
                        (binding.rcv.adapter as SimpleAppListAdapter).notifyDataSetChanged()
                    }
                }
            }
        }

        class BottomSheetFragment(private val binding: ViewDataBinding) : BottomSheetDialogFragment() {
            override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
                return binding.root
            }
        }

        private fun getDialog(layoutId: Int, context: Context): Pair<AlertDialog.Builder, ViewDataBinding> {
            val builder = AlertDialog.Builder(context)
            val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val binding = DataBindingUtil.inflate<ViewDataBinding>(inflater, layoutId, null, false)
            return Pair(builder, binding)
        }

        @Suppress("BlockingMethodInNonBlockingContext")
        @SuppressLint("Recycle")
        private fun openPickContactDialog(context: Context, delegate: ItemSelectDelegate, position: Int) {

            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        context as AppCompatActivity,
                        arrayOf(Manifest.permission.READ_CONTACTS),
                        300
                )
                return
            }
            val pair = getDialog(R.layout.pick_contact, context)
            val binding = pair.second as PickContactBinding

            val bottomSheetFragment = WeakReference(BottomSheetFragment(binding))
            bottomSheetFragment.get()?.show((context as AppCompatActivity).supportFragmentManager, bottomSheetFragment.get()?.tag)
            val cursor: Cursor = context.contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null)!!
            GlobalScope.launch {
                val contacts = arrayListOf<ContactButton.Contact>()
                while (cursor.moveToNext()) {
                    val name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                    val phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    val imageUri = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI))
                    val id = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID))
                    var icon: Drawable? = null
                    if (imageUri != null) {
                        val inputStream = context.contentResolver.openInputStream(Uri.parse(imageUri))
                        icon = Drawable.createFromStream(inputStream, imageUri)
                    }
                    contacts.add(ContactButton.Contact(name, phoneNumber, imageUri, id, icon))
                }
                cursor.close()
                GlobalScope.launch(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE
                    binding.rcv.adapter = SimpleContactListAdapter(contacts, delegate, bottomSheetFragment, position)
                    (binding.rcv.adapter as SimpleContactListAdapter).notifyDataSetChanged()
                    binding.root.invalidate()
                    binding.root.requestLayout()
                }
            }

        }

    }

    fun addNewBlankItem() {
        val newInsertedPosition = buttons.lastIndex + 1
        buttons.add(ButtonModelInPanel(button = NoActionButton(), unicodeIcon = "\uF140", buttonTypeName = ButtonModelInPanel.ButtonTypesName.NO_ACTION))
        notifyItemInserted(newInsertedPosition)
    }

    open class ViewHolder(val row: RowButtonInPanelBinding) : RecyclerView.ViewHolder(row.root) {
        fun bind(buttonModel: ButtonModelInPanel) {
            row.buttonModel = buttonModel
        }
    }
}
