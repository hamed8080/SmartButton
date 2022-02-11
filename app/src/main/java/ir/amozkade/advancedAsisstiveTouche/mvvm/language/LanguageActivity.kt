package ir.amozkade.advancedAsisstiveTouche.mvvm.language

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import dagger.hilt.android.AndroidEntryPoint
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.databinding.ActivityLanguageBinding
import ir.amozkade.advancedAsisstiveTouche.mvvm.BaseActivity
import ir.amozkade.advancedAsisstiveTouche.mvvm.mainAssistiveTouch.MainAssistiveTouchActivity

@AndroidEntryPoint
class LanguageActivity : BaseActivity() {

    private val viewModel: LanguageViewModel by viewModels()
    private var languages: ArrayList<Language> = ArrayList()
    private lateinit var mBinding: ActivityLanguageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_language)
        viewModel.languageLiveData.observe(this) {
            languageTaped()
        }
        languages.add(Language(identifier = "fa", name = "فارسی"))
        languages.add(Language(identifier = "en", name = "english"))
        languages.add(Language(identifier = "de", name = "deutsch"))
        mBinding.rcvLanguage.adapter = LanguageAdapter(languages = languages, vm = viewModel)
        mBinding.rcvLanguage.adapter?.notifyDataSetChanged()
    }

    @Suppress("DEPRECATION")
    private fun languageTaped() {
        val intent = Intent(cto, MainAssistiveTouchActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.action = "LANGUAGE_CHANGED"
        startActivity(intent)
        finish()
    }
}