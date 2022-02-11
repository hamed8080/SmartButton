package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.downloadDictionary

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.databinding.*
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.manageDictionaries.Dictionary

class DownloadDictionaryAdapter(
        private val dictionaries: List<Dictionary>,
        private val downloadDictionaryListener: DownloadDictionaryAdapterListener
) : RecyclerView.Adapter<DownloadDictionaryAdapter.ViewHolder>() {

    data class DownloadDictionaryStatus(val dictionary:Dictionary, var downloadPercent: Int = 0, var isFinished: Boolean = false)

    private var downloadDictionaryStatusArray = arrayListOf<DownloadDictionaryStatus>()

    interface DownloadDictionaryAdapterListener {
        fun onDownloadDictionaryTaped(downloadDictionaryStatus: DownloadDictionaryStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = DataBindingUtil.inflate<RowDownloadDictionaryBinding>(inflater, R.layout.row_download_dictionary, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dictionaries.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dictionary = dictionaries[holder.bindingAdapterPosition]
        holder.bind(dictionary)
        holder.row.btnDownload.setOnClickListener {
            val downloadStatus = DownloadDictionaryStatus(dictionary= dictionary)
            downloadDictionaryStatusArray.add(downloadStatus)
            downloadDictionaryListener.onDownloadDictionaryTaped(downloadStatus)
        }
        getStatusForDictionary(dictionary.id)?.let {status->
            holder.row.run {
                txtStatus.visibility = if (status.isFinished) View.GONE else View.VISIBLE
                downloadProgress.visibility = if (status.isFinished) View.GONE else View.VISIBLE
                downloadProgress.progress = status.downloadPercent
                txtStatus.text = String.format(txtStatus.context.getString(R.string.downloading_dictionary_status), status.downloadPercent)
                if(status.downloadPercent == 100){
                    txtStatus.text = txtStatus.context.getString(R.string.uncompressing)
                }
            }
        }
    }

    fun updateProgress(downloadDictionaryStatus: DownloadDictionaryStatus) {
        downloadDictionaryStatusArray.firstOrNull{ it.dictionary.id == downloadDictionaryStatus.dictionary.id }?.let {
            val index = downloadDictionaryStatusArray.indexOf(it)
            it.downloadPercent = downloadDictionaryStatus.downloadPercent
            it.isFinished = downloadDictionaryStatus.isFinished
            notifyItemChanged(index)
        }
    }

    private fun getStatusForDictionary(dictionaryId: Int): DownloadDictionaryStatus? {
        return downloadDictionaryStatusArray.firstOrNull { it.dictionary.id == dictionaryId }
    }

    open class ViewHolder(val row: RowDownloadDictionaryBinding) : RecyclerView.ViewHolder(row.root) {
        fun bind(dictionary: Dictionary) {
            row.model = dictionary
        }
    }
}
