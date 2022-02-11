package ir.amozkade.advancedAsisstiveTouche.mvvm.notification

import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.databinding.ActivityNotificationBinding
import ir.amozkade.advancedAsisstiveTouche.helper.api.DataState
import ir.amozkade.advancedAsisstiveTouche.mvvm.BaseActivity
import ir.amozkade.advancedAsisstiveTouche.mvvm.notification.utils.NotificationResponse
import ir.amozkade.advancedAsisstiveTouche.mvvm.notification.utils.NotificationStateEvent
import ir.mobitrain.applicationcore.alertDialog.AlertDialogDelegate
import ir.mobitrain.applicationcore.alertDialog.CustomAlertDialog
import java.lang.Exception

@AndroidEntryPoint
class NotificationActivity : BaseActivity() {

    private lateinit var mBinding: ActivityNotificationBinding
    private val viewModel: NotificationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUI()
        setupObservers()
        viewModel.setState(NotificationStateEvent.GetAll)
    }

    private fun setupUI() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_notification)
        mBinding.actionBar.btnDeleteSetOnClickListener { openDeleteAllNotifications() }
        mBinding.vm = viewModel
        viewModel.getModel().listEmpty = true
    }

    private fun setupObservers() {
        viewModel.exceptionObserver.observe(this) {
            manageDataState(DataState.Error(it as Exception))
        }
        viewModel.response.observe(this) { dataState ->
            if (dataState is DataState.Success) {
                if (dataState.data is NotificationResponse.AllNotifications) {
                    setupRecyclerView(dataState.data.notifications)
                }
            }
            manageDataState(dataState)
        }
    }

    private fun setupRecyclerView(notifications: ArrayList<Notification>) {
        viewModel.getModel().listEmpty = notifications.isEmpty()
        val sortedNotifications = notifications.sortedByDescending { it.dateSend }
        mBinding.rcv.adapter = NotificationAdapter(ArrayList(sortedNotifications), this)
        (mBinding.rcv.adapter as? NotificationAdapter)?.notifyDataSetChanged()
    }

    private fun clearAdapter() {
        (mBinding.rcv.adapter as? NotificationAdapter)?.clear()
        viewModel.getModel().listEmpty = true
    }

    private fun openDeleteAllNotifications() {
        CustomAlertDialog.showDialog(
                this,
                title = getString(R.string.delete_all_notifications_title),
                submitTextColorId = ContextCompat.getColor(this, R.color.red),
                cancelTextColorId = ContextCompat.getColor(this, R.color.primary_color),
                submitTitle = getString(R.string.delete),
                message = getString(R.string.delete_all_notifs_subtitle),
                imageId = R.drawable.img_delete,
                delegate = object : AlertDialogDelegate {
                    override fun alertCompletion(type: AlertDialogDelegate.AlertTapType, extraButton: MaterialButton?) {
                        if (type == AlertDialogDelegate.AlertTapType.Submit) {
                            clearAdapter()
                            viewModel.setState(NotificationStateEvent.ViewedAll)
                        }
                    }
                })
    }
}