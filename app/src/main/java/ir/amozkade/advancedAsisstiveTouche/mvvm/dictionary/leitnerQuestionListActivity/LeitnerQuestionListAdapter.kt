package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.leitnerQuestionListActivity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.graphics.Typeface
import android.os.Build
import android.view.*
import android.widget.PopupWindow
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.qtalk.recyclerviewfastscroller.RecyclerViewFastScroller
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.databinding.QuestionAnswerPopupMenuBinding
import ir.amozkade.advancedAsisstiveTouche.databinding.RowLeitnerQuestionBinding
import ir.amozkade.advancedAsisstiveTouche.helper.Common
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.levels.models.Level
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.levels.questionAnswer.QuestionAnswer
import ir.mobitrain.applicationcore.helper.Converters.Companion.addDaysToDateTime
import ir.mobitrain.applicationcore.helper.Converters.Companion.convertDateToProperString
import ir.mobitrain.applicationcore.helper.Converters.Companion.convertIntToDP
import ir.mobitrain.applicationcore.helper.Converters.Companion.getDifferenceDays
import java.util.*


class LeitnerQuestionListAdapter(
    private var questionAnswers: ArrayList<QuestionAnswer>,
    private val levels: List<Level>,
    private val onQuestionListener: OnQuestionListener
) : RecyclerView.Adapter<LeitnerQuestionListAdapter.ViewHolder>(),
    RecyclerViewFastScroller.OnPopupTextUpdate {

    var iranSansFont: Typeface? = null
    var sfProFont: Typeface? = null

    init {
        val context = onQuestionListener as Context
        iranSansFont = ResourcesCompat.getFont(context, R.font.iransans_bold)
        sfProFont = ResourcesCompat.getFont(context, R.font.sf_pro_rounded_bold)
    }

    interface OnQuestionListener {
        fun onQuestionDeleteTaped(questionAnswer: QuestionAnswer)
        fun onQuestionEditTaped(questionAnswer: QuestionAnswer)
        fun onBackToListTaped(questionAnswer: QuestionAnswer)
        fun onMoveToLeitner(questionAnswer: QuestionAnswer)
        fun onFavTaped(questionAnswer: QuestionAnswer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = parent.context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val row: RowLeitnerQuestionBinding =
            DataBindingUtil.inflate(inflater, R.layout.row_leitner_question, parent, false)
        return ViewHolder(row)
    }

    override fun getItemCount(): Int = questionAnswers.count()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val questionAnswer = questionAnswers[position]
        holder.bind(questionAnswer)
    }

    fun update(questionAnswer: QuestionAnswer) {
        questionAnswers.firstOrNull { it.question == questionAnswer.question && it.leitnerId == questionAnswer.leitnerId }
            ?.let { listQuestionAnswer ->
                val index = questionAnswers.indexOf(listQuestionAnswer)
                listQuestionAnswer.levelId = questionAnswer.levelId
                listQuestionAnswer.leitnerId = questionAnswer.leitnerId
                listQuestionAnswer.completed = questionAnswer.completed
                listQuestionAnswer.passedTime = questionAnswer.passedTime
                listQuestionAnswer.answer = questionAnswer.answer
                notifyItemChanged(index)
            }
    }

    fun remove(questionAnswer: QuestionAnswer) {
        val index = questionAnswers.indexOfFirst { questionAnswer.question == it.question }
        notifyItemRemoved(index)
    }

    inner class ViewHolder(val row: RowLeitnerQuestionBinding) : RecyclerView.ViewHolder(row.root) {
        private val rowBinding = row

        @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
        fun bind(questionAnswer: QuestionAnswer) {
            val context = row.root.context
            rowBinding.txtQuestion.text = questionAnswer.question
            levels.firstOrNull { it.id == questionAnswer.levelId }?.let { level ->
                rowBinding.txtLevel.text =
                    String.format(context.getString(R.string.level_name), level.level)
                val totalDays = level.time
                if (level.level != 1 && questionAnswer.passedTime != null) {
                    val lastDate = addDaysToDateTime(questionAnswer.passedTime!!, totalDays)
                    val days = getDifferenceDays(lastDate, Date())
                    val remainDays =
                        String.format(context.getString(R.string.remaining_day), days.toString())
                    rowBinding.txtPassedTime.text =
                        "${convertDateToProperString(questionAnswer.passedTime)} - $remainDays"
                } else {
                    rowBinding.txtPassedTime.text = ""
                }
            }
            val regex = Regex("[-()*&^%\$#@!~]")
            val isPersianAnswer = Common.isContainPersian(
                questionAnswer.answer?.replace(regex, "") ?: "A"
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                rowBinding.txtAnswer.typeface = if (isPersianAnswer) iranSansFont else sfProFont
            }
            rowBinding.txtCompleted.visibility =
                if (questionAnswer.completed) View.VISIBLE else View.GONE
            rowBinding.txtAnswer.text = questionAnswer.answer ?: ""
            rowBinding.btnFav.setOnClickListener {
                val icon = if (questionAnswer.favorite) R.drawable.ic_baseline_star_outline_24 else R.drawable.ic_baseline_star_24
                rowBinding.btnFav.setIconResource(icon)
                onQuestionListener.onFavTaped(questionAnswer)
            }
            rowBinding.btnFav.setIconResource(if (questionAnswer.favorite) R.drawable.ic_baseline_star_24 else R.drawable.ic_baseline_star_outline_24)
            rowBinding.menuButton.setOnClickListener {
                openPopupMenu(questionAnswer, it, rowBinding)
            }
        }

    }

    private fun openPopupMenu(
        questionAnswer: QuestionAnswer,
        anchorView: View,
        rowBinding: RowLeitnerQuestionBinding
    ) {
        val context = anchorView.context
        val inflater = context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupMenuBinding = DataBindingUtil.inflate<QuestionAnswerPopupMenuBinding>(
            inflater,
            R.layout.question_answer_popup_menu,
            null,
            false
        )
        val popupWindow = PopupWindow(
            popupMenuBinding.root,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        popupMenuBinding.btnDelete.setOnClickListener {
            val index = questionAnswers.indexOf(questionAnswer)
            questionAnswers.remove(questionAnswer)
            notifyItemRemoved(index)
            onQuestionListener.onQuestionDeleteTaped(questionAnswer)
            popupWindow.dismiss()
        }
        popupMenuBinding.btnEdit.setOnClickListener {
            onQuestionListener.onQuestionEditTaped(questionAnswer)
            popupWindow.dismiss()
        }

        popupMenuBinding.btnMoveToAnotherLeitner.setOnClickListener {
            onQuestionListener.onMoveToLeitner(questionAnswer)
            popupWindow.dismiss()
        }

        if (questionAnswer.completed) {
            popupMenuBinding.btnBackToList.visibility = View.VISIBLE
            popupMenuBinding.btnBackToList.setOnClickListener {
                onQuestionListener.onBackToListTaped(questionAnswer)
                popupWindow.dismiss()
            }
        }
        popupWindow.isOutsideTouchable = true
        popupWindow.isFocusable = true
        popupWindow.isTouchable = true
        popupWindow.setBackgroundDrawable(null)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            popupMenuBinding.container.outlineProvider = ViewOutlineProvider.BACKGROUND
            popupMenuBinding.container.clipToOutline = true
            popupWindow.elevation = convertIntToDP(16, context)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            popupWindow.showAsDropDown(
                anchorView,
                0,
                calculatePopupPosition(rowBinding, questionAnswer)
            )
        } else {
            popupWindow.showAsDropDown(anchorView, anchorView.width, anchorView.height)
        }
    }

    override fun onChange(position: Int): CharSequence {
        return questionAnswers[position].question
    }

    private fun calculatePopupPosition(
        rowBinding: RowLeitnerQuestionBinding,
        questionAnswer: QuestionAnswer
    ): Int {
        val context = rowBinding.root.context
        //First we get the position of the menu icon in the screen
        val values = IntArray(2)
        rowBinding.menuButton.getLocationInWindow(values)
        val positionOfIcon = values[1]

        //Get the height of 2/3rd of the height of the screen
        val displayMetrics = context.resources.displayMetrics
        val height = displayMetrics.heightPixels * 2 / 3
        val popupHeight =
            if (questionAnswer.completed) convertIntToDP(48, context) * 5 else convertIntToDP(
                48,
                context
            ) * 4
        return if (positionOfIcon > height) -popupHeight.toInt() else 0
    }
}