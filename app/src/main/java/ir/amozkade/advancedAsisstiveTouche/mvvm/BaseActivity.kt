package ir.amozkade.advancedAsisstiveTouche.mvvm

import android.content.Context
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.github.ybq.android.spinkit.SpinKitView
import com.github.ybq.android.spinkit.style.Wave
import com.google.android.material.button.MaterialButton
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.helper.ExceptionImageFactory
import ir.amozkade.advancedAsisstiveTouche.helper.InAppException
import ir.amozkade.advancedAsisstiveTouche.helper.RedirectToFactory
import ir.amozkade.advancedAsisstiveTouche.helper.api.DataState
import ir.mobitrain.applicationcore.alertDialog.AlertDialogDelegate
import ir.mobitrain.applicationcore.alertDialog.CustomAlertDialog
import ir.mobitrain.applicationcore.helper.CommonHelpers
import ir.mobitrain.applicationcore.helper.LanguageContext
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.InterruptedIOException
import java.net.ConnectException
import java.net.SocketTimeoutException

interface BaseActivityDelegate  {
    var signedIn: MutableLiveData<Boolean>
    var showWarningAlert: Boolean
    fun showLoading()
    fun showLoadingBlockUi()
    fun showSignAlert()
    fun showWarn(title: String, subtitle: String, imageId: Int? = null)
    fun showFinishDialog(title: String?, message: String?, imageId: Int?)
    fun hideLoading()
}

open class BaseActivity : AppCompatActivity(), BaseActivityDelegate {

    @Suppress("UNUSED_PARAMETER")
    var cto: AppCompatActivity?
        get() = this
        set(value) {}

    private fun dynamicError(message: String?) {
        showWarn(title = "", subtitle = message ?: "")
    }

    private fun failedConnect() {
        showWarn(getString(R.string.failed_connect_title), getString(R.string.failed_connect), imageId = R.drawable.ic_server_error)
    }

    private fun timeout() {
        showWarn(getString(R.string.timeout_title), getString(R.string.timeout), imageId = R.drawable.ic_server_error)
    }

    private fun unauthorizedException() {
        invalidToken()
        showWarn(getString(R.string.unauthorized_title), getString(R.string.unauthorized_subtitle), imageId = R.drawable.img_unauthorized)
    }

    private fun onBusinessException(message: String?, code: String?) {
        showWarn("", message ?: "", imageId = ExceptionImageFactory.getImageIdentifier(code))
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onRedirectTo(to: String, message: String?) {
        RedirectToFactory.getIntent(to, cto)?.let {
            startActivity(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent.hasExtra("Message")) {
            onBusinessException(CommonHelpers.getLocalizedMessage(intent?.getStringExtra("Message")
                    ?: "", this), intent.getStringExtra("Message"))
        }
    }


    override var signedIn: MutableLiveData<Boolean> = MutableLiveData()
    override var showWarningAlert: Boolean = true

    private var loading: SpinKitView? = null
    private var blockContainer: LinearLayout? = null

    public override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LanguageContext.updateBaseContextLocale(base, this))
    }

    override fun showLoading() {
        loading?.visibility = View.VISIBLE
    }

    override fun showLoadingBlockUi() {
        GlobalScope.launch(Main) {
            loading?.bringToFront()
            blockContainer?.visibility = View.VISIBLE
            loading?.visibility = View.VISIBLE
        }
    }

    override fun hideLoading() {
        blockContainer?.visibility = View.GONE
        loading?.visibility = View.GONE
    }

    override fun showWarn(title: String, subtitle: String, imageId: Int?) {
        hideLoading()
        if (showWarningAlert)
            CustomAlertDialog.showDialog(this, title, subtitle, imageId = imageId)
    }

    override fun showFinishDialog(title: String?, message: String?, imageId: Int?) {
        hideLoading()
        CustomAlertDialog.showDialog(this,
                title = title,
                message = message,
                imageId = imageId,
                delegate = object : AlertDialogDelegate {
                    override fun alertCompletion(type: AlertDialogDelegate.AlertTapType, extraButton: MaterialButton?) {
                        if (type == AlertDialogDelegate.AlertTapType.Cancel) {
                            finish()
                        }
                    }
                })
    }

    override fun showSignAlert() {
        showWarn(getString(R.string.FirstLoginToApp), "", imageId = R.drawable.img_login)
    }

    private fun invalidToken() {

    }

    fun addLoadingsToContainer(viewGroup: ViewGroup) {
        addLoadingView(viewGroup)
        addBlockLoadingView(viewGroup)
    }

    private fun addLoadingView(masterView: ViewGroup) {
        loading = SpinKitView(this)
        val params = ConstraintLayout.LayoutParams(128, 128)
        params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
        params.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID
        params.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID
        loading?.setColor(ContextCompat.getColor(this, R.color.primary_dark_color))
        loading?.setIndeterminateDrawable(Wave())
        loading?.layoutParams = params
        loading?.visibility = View.GONE
        masterView.addView(loading)
    }

    private fun addBlockLoadingView(masterView: ViewGroup) {
        blockContainer = LinearLayout(this)
        blockContainer?.layoutParams = ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        blockContainer?.isClickable = true
        blockContainer?.isFocusable = true
        blockContainer?.visibility = View.GONE
        blockContainer?.setBackgroundColor(ContextCompat.getColor(this, R.color.black_with_transparency))
        masterView.addView(blockContainer)
    }

    private fun manageException(exception: Exception) {
        when (exception) {
            is SocketTimeoutException -> timeout()
            is InterruptedIOException -> timeout()
            is ConnectException -> failedConnect()
            is InAppException -> showWarn(exception.title, exception.message, exception.imageId)
            is HttpException -> {
                when (exception.code()) {
                    401 -> {
                        invalidToken()
                        unauthorizedException()
                    }
                    400 -> {
                        exception.response()?.headers()?.get("DynamicMessage")?.let {
                            dynamicError(String(Base64.decode(it, Base64.DEFAULT)))
                        }
                        exception.response()?.headers()?.get("Message")?.let {
                            val code = String(Base64.decode(it, Base64.DEFAULT))
                            val image = ExceptionImageFactory.getImageIdentifier(code)
                            showWarn("", CommonHelpers.getLocalizedMessage(code, this) ?: "", image)
                        }
                    }
                    301 -> {
                        exception.response()?.headers()?.get("To")?.let { base64ToString ->
                            val to = String(Base64.decode(base64ToString, Base64.DEFAULT))
                            val message = if (exception.response()?.headers()?.get("Message") != null) {
                                String(Base64.decode(exception.response()?.headers()?.get("Message"), Base64.DEFAULT))
                            } else {
                                null
                            }
                            onRedirectTo(to, message)
                        }
                    }
                    in 500..510 -> failedConnect()
                }
            }
        }
    }

    fun manageDataState(dataState: DataState<Any?>) {
        when (dataState) {
            is DataState.Success -> hideLoading()
            is DataState.Error -> {
                hideLoading()
                manageException(dataState.exception)
            }
            is DataState.Loading -> showLoading()
            is DataState.BlockLoading -> showLoadingBlockUi()
        }
    }
}