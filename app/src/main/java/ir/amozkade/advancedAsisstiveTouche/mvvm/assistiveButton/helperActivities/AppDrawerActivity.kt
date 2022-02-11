package ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.helperActivities

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.databinding.AppDrawerRecyclerBinding
import ir.amozkade.advancedAsisstiveTouche.databinding.RowAppInDrawerBinding
import ir.amozkade.advancedAsisstiveTouche.mvvm.BaseActivity
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.buttons.AppDrawerButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AppDrawerActivity : BaseActivity() {
    private lateinit var mBinding: AppDrawerRecyclerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.app_drawer_recycler)
        GlobalScope.launch {
            val apps = getAllAppsWithIcon(mBinding.root.context)
            GlobalScope.launch(Dispatchers.Main){
                mBinding.rcvAppDrawer.adapter = AppDrawerAdapter(apps)
                (mBinding.rcvAppDrawer.adapter as AppDrawerAdapter).notifyDataSetChanged()
            }
        }
    }

    class AppDrawerAdapter(private val packages: ArrayList<AppDrawerButton.AppInfo>) : RecyclerView.Adapter<AppDrawerAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val inflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val binding = DataBindingUtil.inflate<RowAppInDrawerBinding>(inflater, R.layout.row_app_in_drawer, parent, false)
            return ViewHolder(binding)
        }

        override fun getItemCount(): Int {
            return packages.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val appInfo = packages[position]
            holder.row.root.setOnClickListener {
                val pm: PackageManager = holder.row.root.context.packageManager
                val intent = pm.getLaunchIntentForPackage(appInfo.packageName)
                intent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                holder.row.root.context.startActivity(intent)
            }
            holder.row.imgApp.setImageDrawable(appInfo.drawable)
            holder.row.txtAppName.text = appInfo.appName
        }

        open class ViewHolder(val row: RowAppInDrawerBinding) : RecyclerView.ViewHolder(row.root)
    }

    companion object {
        fun getAllAppsWithIcon(context: Context): ArrayList<AppDrawerButton.AppInfo> {

            val appInfoWithIcons = arrayListOf<AppDrawerButton.AppInfo>()
            val intent = Intent(Intent.ACTION_MAIN, null)
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            val allApps = context.packageManager.queryIntentActivities(intent, 0)
            val allAppsInfo = context.packageManager.getInstalledApplications(PackageManager.GET_META_DATA).map {
                val appName = context.packageManager.getApplicationLabel(it)
                AppDrawerButton.AppInfo(it.packageName, appName.toString(), null)
            }
            for (app in allApps) {
                val appName = app.loadLabel(context.packageManager)
                val drawable: Drawable = app.loadIcon(context.packageManager)
                val packageInfo = allAppsInfo.firstOrNull { it.appName == appName }
                packageInfo?.let {
                    appInfoWithIcons.add(AppDrawerButton.AppInfo(packageInfo.packageName, appName.toString(), drawable))
                }
            }
            return appInfoWithIcons

        }
    }
}