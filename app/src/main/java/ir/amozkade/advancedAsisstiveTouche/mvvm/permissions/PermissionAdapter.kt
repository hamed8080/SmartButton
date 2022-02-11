package ir.amozkade.advancedAsisstiveTouche.mvvm.permissions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.databinding.RowPermissionBinding
import ir.amozkade.advancedAsisstiveTouche.mvvm.permissions.models.Permission
import java.util.*

class PermissionAdapter(private val permissions: List<Permission>, private val permissionActivity: PermissionActivity) : RecyclerView.Adapter<PermissionAdapter.ViewHolder>() {

    private val isFa = Locale.getDefault().language.contains("fa")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<RowPermissionBinding>(LayoutInflater.from(parent.context), R.layout.row_permission, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return permissions.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val permission = permissions [holder.bindingAdapterPosition]
        holder.bind(permission)
        holder.row.container.setOnClickListener{permissionActivity.showWebView(permission)}
        holder.row.txtTitle.text = if(isFa) permission.titleFA else permission.title
    }

    class ViewHolder(val row: RowPermissionBinding) : RecyclerView.ViewHolder(row.root){
        fun bind(permission: Permission){
            row.permission = permission
        }
    }
}