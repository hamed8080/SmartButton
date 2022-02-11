package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.manageDictionaries

import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.databinding.ActivityManageDictionariesBinding
import ir.amozkade.advancedAsisstiveTouche.helper.api.DataState
import ir.amozkade.advancedAsisstiveTouche.mvvm.BaseActivity
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.leitners.models.Leitner
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.manageDictionaries.utils.DictionaryResponse
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.manageDictionaries.utils.ManageDictionaryStateEvent
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

@AndroidEntryPoint
class ManageDictionariesActivity : BaseActivity(), ManageDictionaryDelegate {

    private val viewModel: ManageDictionariesViewModel by viewModels()
    private lateinit var mBinding: ActivityManageDictionariesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_manage_dictionaries)
        setupUI()
        setupObservers()
    }

    private fun setupUI() {
        addLoadingsToContainer(mBinding.root as ViewGroup)
        viewModel.setState(ManageDictionaryStateEvent.LoadAllDictionary)
    }

    private fun setupObservers() {
        viewModel.exceptionObserver.observe(this) {
            manageDataState(DataState.Error(it as Exception))
        }

        viewModel.response.observe(this) { dataState ->
            if (dataState is DataState.Success) {
                if (dataState.data is DictionaryResponse.AllDictionary) {
                    setupAdapter(dataState.data.dictionaries)
                }

                if (dataState.data is DictionaryResponse.InsertedAllDictionaryItemsIntoLeitner) {
                    showWarn(
                        getString(R.string.import_to_db),
                        getString(R.string.imported_successfully)
                    )
                }
            }
            manageDataState(dataState)
        }
    }

    private fun setupAdapter(dictionaries: List<Dictionary>) {
        mBinding.rcv.adapter = ManageDictionariesAdapter(dictionaries, this)
        (mBinding.rcv.adapter as ManageDictionariesAdapter).notifyDataSetChanged()
    }

    override fun onDeleteDictionaryTaped(dictionary: Dictionary) {
        viewModel.setState(ManageDictionaryStateEvent.DeleteDictionary(dictionary))
    }

    override fun tapeOnAddAllToLeitner(dictionary: Dictionary) {
        GlobalScope.launch(IO) {
            val categories = viewModel.getAllLeitners()
            withContext(Main) {
                showImportDialog(categories, dictionary)
            }
        }
    }

    private var leitnerSelectedIndex: Int = 0
    private fun showImportDialog(leitners: List<Leitner>, dictionary: Dictionary) {
        MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.import_all_dic_to_leitner))
                .setNeutralButton(getString(R.string.cancel)) { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton(getString(R.string.import_to_db)) { dialog, _ ->
                    val leitnerSelectedIndex = leitnerSelectedIndex
                    if (leitners.count() == 0) {
                        showWarn(getString(R.string.add_new_leitner), getString(R.string.add_new_leitner))
                        return@setPositiveButton
                    }
                    val leitner = leitners[leitnerSelectedIndex]
                    viewModel.setState(ManageDictionaryStateEvent.AddAllDictionaryItemsToLeitner(dictionary, leitner.id))
                    dialog.dismiss()
                }
                .setSingleChoiceItems(leitners.map { it.name }.toTypedArray(), 0) { _, which ->
                    leitnerSelectedIndex = which
                }
                .show()
    }
}
