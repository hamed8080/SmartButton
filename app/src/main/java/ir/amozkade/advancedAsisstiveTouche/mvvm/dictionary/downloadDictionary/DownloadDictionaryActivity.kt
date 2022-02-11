package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.downloadDictionary

import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import dagger.hilt.android.AndroidEntryPoint
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.databinding.ActivityDownloadDictionaryBinding
import ir.amozkade.advancedAsisstiveTouche.helper.api.DataState
import ir.amozkade.advancedAsisstiveTouche.helper.downloader.DownloadResponse
import ir.amozkade.advancedAsisstiveTouche.mvvm.BaseActivity
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.downloadDictionary.utils.DownloadDictionaryResponse
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.downloadDictionary.utils.DownloadDictionaryStateEvent
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.manageDictionaries.Dictionary
import java.lang.Exception

@AndroidEntryPoint
class DownloadDictionaryActivity : BaseActivity(), DownloadDictionaryAdapter.DownloadDictionaryAdapterListener {

    private val viewModel by viewModels<DownloadDictionaryViewModel>()
    private lateinit var mBinding: ActivityDownloadDictionaryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUI()
        setupObservers()
        viewModel.setState(DownloadDictionaryStateEvent.GetAllDictionaryList)
    }

    private fun setupUI() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_download_dictionary)
        addLoadingsToContainer(mBinding.root as ViewGroup)
        mBinding.vm = viewModel
    }

    private fun setupObservers() {
        viewModel.exceptionObserver.observe(this) {
            manageDataState(DataState.Error(it as Exception))
        }

        viewModel.response.observe(this) { dataState ->
            if (dataState is DataState.Success) {
                if (dataState.data is DownloadDictionaryResponse.DictionaryList) {
                    setupAdapter(dataState.data.dictionaries)
                }
            }
            manageDataState(dataState)
        }

        viewModel.downloadResponse.observe(this) { dataState ->
            if (dataState is DataState.Success) {
                when (dataState.data) {
                    is DownloadResponse.Finished -> {
                        viewModel.setState(DownloadDictionaryStateEvent.UnCompress(dataState.data.callBackData as Dictionary))
                    }
                    is DownloadResponse.Downloading -> {
                        val status = DownloadDictionaryAdapter.DownloadDictionaryStatus(
                            dataState.data.callBackData as Dictionary,
                            dataState.data.progress
                        )
                        (mBinding.rcv.adapter as DownloadDictionaryAdapter).updateProgress(status)
                    }
                    is DownloadResponse.Uncompressed -> {
                        val status = DownloadDictionaryAdapter.DownloadDictionaryStatus(
                            dataState.data.dictionary,
                            100,
                            true
                        )
                        (mBinding.rcv.adapter as DownloadDictionaryAdapter).updateProgress(status)
                    }
                }
            }
            manageDataState(dataState)
        }
    }

    private fun setupAdapter(dictionaries: List<Dictionary>) {
        mBinding.rcv.run {
            adapter = DownloadDictionaryAdapter(dictionaries, this@DownloadDictionaryActivity)
            adapter?.notifyDataSetChanged()
        }
    }

    override fun onDownloadDictionaryTaped(downloadDictionaryStatus: DownloadDictionaryAdapter.DownloadDictionaryStatus) {
        viewModel.setState(DownloadDictionaryStateEvent.Download(downloadDictionaryStatus))
    }
}
