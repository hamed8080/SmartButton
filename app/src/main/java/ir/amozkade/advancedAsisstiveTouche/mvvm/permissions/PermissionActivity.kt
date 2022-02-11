package ir.amozkade.advancedAsisstiveTouche.mvvm.permissions

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import dagger.hilt.android.AndroidEntryPoint
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.databinding.ActivityPermissoinBinding
import ir.amozkade.advancedAsisstiveTouche.helper.api.DataState
import ir.amozkade.advancedAsisstiveTouche.mvvm.BaseActivity
import ir.amozkade.advancedAsisstiveTouche.mvvm.permissions.models.Permission
import ir.amozkade.advancedAsisstiveTouche.mvvm.permissions.utils.PermissionResponse
import ir.amozkade.advancedAsisstiveTouche.mvvm.permissions.utils.PermissionStateEvent
import java.util.*


@AndroidEntryPoint
class PermissionActivity : BaseActivity() {

    private lateinit var mBinding: ActivityPermissoinBinding
    private val viewModel: PermissionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupUI()
        setupObservers()
    }

    private fun setupUI() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_permissoin)
        addLoadingsToContainer(mBinding.root as ConstraintLayout)
        mBinding.actionBar.btnBackSetOnClickListener {
            if (mBinding.wb.visibility == View.VISIBLE) {
                mBinding.wb.visibility = View.GONE
            } else {
                finish()
            }
        }
        mBinding.vm = viewModel
        viewModel.setState(PermissionStateEvent.GetAllPermissions)
    }

    private fun setupObservers() {
        viewModel.exceptionObserver.observe(this) {
            manageDataState(DataState.Error(it as Exception))
        }
        viewModel.response.observe(this) { dataState ->
            if (dataState is DataState.Success) {
                if (dataState.data is PermissionResponse.Permissions) {
                    setupAdapter(dataState.data.permissions)
                }
            }
            manageDataState(dataState)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && mBinding.wb.visibility == View.VISIBLE) {
            mBinding.wb.visibility = View.GONE
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun setupAdapter(permissions: List<Permission>) {
        hideLoading()
        mBinding.rcv.adapter = PermissionAdapter(permissions, this)
        (mBinding.rcv.adapter as PermissionAdapter).notifyDataSetChanged()
    }

    fun showWebView(permission: Permission) {

        val isFa = Locale.getDefault().language.contains("fa")
        val fontName = if (isFa) "iransans.ttf" else "sf_pro_rounded_medium.ttf"
        val textAlign = if (isFa) "right" else "left"
        val html = """
            <html dir="rtl">
            <head>
            <meta name="viewport" content="initial-scale=1.0" />
            <meta http-equiv="Content-Type" content="text/html; charset=utf-8"  />
            <style>@font-face { font-family: "IranSans" ; src: url("font/${fontName}"); }</style>
            <style>body{text-align:${textAlign}}</style>
            <style>*{font-size:14px;font-family:IranSans,'IranSans',tahoma; line-height: 37px;}</style>
            </head>
            <body style="padding:5px;">
            """ + if (Locale.getDefault().language.contains("fa")) permission.textFA else permission.text + """
            </body>
            </html>
            """
        mBinding.wb.visibility = View.VISIBLE
        mBinding.wb.loadDataWithBaseURL("file:///android_res/", html, "text/html", "UTF-8", null)
    }
}