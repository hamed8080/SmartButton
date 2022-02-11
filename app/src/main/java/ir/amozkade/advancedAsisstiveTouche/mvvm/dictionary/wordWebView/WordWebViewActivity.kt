package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.wordWebView

import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import dagger.hilt.android.AndroidEntryPoint
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.databinding.ActivityWordWebViewBinding
import ir.amozkade.advancedAsisstiveTouche.mvvm.BaseActivity
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.levels.questionAnswer.QuestionAnswer
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.levels.questionAnswer.QuestionAnswerDao
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.levels.repository.LevelDao
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.translator.TranslateResult
import ir.mobitrain.applicationcore.helper.CommonHelpers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class WordWebViewActivity : BaseActivity() {

    @Inject
    lateinit var tts: TextToSpeech

    private lateinit var mBinding: ActivityWordWebViewBinding

    @Inject
    lateinit var levelDao: LevelDao

    @Inject
    lateinit var questionAnswerDao: QuestionAnswerDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_word_web_view)
        mBinding.actionBar.setActionBarTitle(intent.getStringExtra("Question") ?: "")
        val translateResult = intent.getParcelableExtra<TranslateResult>("TranslateResult")
                ?: return
        val isFa = CommonHelpers.textContainsArabic(translateResult.dbName)
        mBinding.btnSpeakWord.setOnClickListener {
            intent.getStringExtra("Question")?.let {
                speakText(it)
            }
        }

        mBinding.btnAddToLeitner.setOnClickListener {
            addToLeitner()
        }
        mBinding.txtDicName.text = translateResult.dbName
        mBinding.txtDicName.typeface = ResourcesCompat.getFont(this, if (isFa) R.font.iransans_bold else R.font.sf_pro_rounded_bold)
        val html = """
            <html dir="rtl">
            <head>
            <meta name="viewport" content="initial-scale=1.0" />
            <meta http-equiv="Content-Type" content="text/html; charset=utf-8"  />
            <style>body{text-align:right}</style>
            <style>@font-face { font-family: "IranSans" ; src: url("font/iransans.ttf"); }</style>
            <style>*{font-size:14px;font-family:IranSans,'IranSans',tahoma; line-height: 37px;}</style>
            </head>
            <body style="padding:5px;background-color:#F7F7F7">
            """ + translateResult.result + """
            </body>
            </html>
            """
        mBinding.wbResult.loadDataWithBaseURL("file:///android_res/", html, "text/html", "UTF-8", "")
    }

    private fun addToLeitner() {
        GlobalScope.launch(IO) {
            val question = intent.getStringExtra("Question") ?: return@launch
            val leitnerId = intent.getIntExtra("leitnerId", 0)
            if (leitnerId == 0) return@launch
            val levelId = levelDao.getFirstLevelIdInLeitner(leitnerId)
            val questionAnswer = QuestionAnswer(question, null, null, leitnerId, levelId)
            questionAnswerDao.insert(questionAnswer)
        }
    }

    private fun speakText(text: String) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        } else {
            @Suppress("DEPRECATION")
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null)
        }
    }
}