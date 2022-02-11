package ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.databinding.RowViewPagerItemBinding
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.delegates.EnableDelegate
import ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.models.ButtonModelInPanel
import ir.amozkade.advancedAsisstiveTouche.mvvm.preferenceActivity.SettingRepository
import kotlin.math.ceil

class PagerButtonAdapter(
        private val list: List<ButtonModelInPanel>,
        context: Context,
        private val settingRepository: SettingRepository,
        private val listener: GridButtonAdapter.OnButtonInPanelClickListener
) : PagerAdapter() {

    val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private var pages = ArrayList<ArrayList<ButtonModelInPanel>>()

    init {
        splitToPager()
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as RecyclerView
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val row: RowViewPagerItemBinding = DataBindingUtil.inflate(inflater, R.layout.row_view_pager_item, container, false)
        val pageButtons = pages[position]
        row.rcvInPager.adapter = GridButtonAdapter(pageButtons, settingRepository, listener)
        (row.rcvInPager.adapter as GridButtonAdapter).notifyDataSetChanged()
        container.addView(row.root)
        return row.root
    }

    override fun getCount(): Int {
        return pageCount
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as RecyclerView)
    }

    fun updateEnableDisableButtonForIndex(@Suppress("UNUSED_PARAMETER") index: Int, button: ButtonModelInPanel, isEnable: Boolean) {
        (button.button as? EnableDelegate)?.isEnable = isEnable
        notifyDataSetChanged()
    }

    //    need to update item in adapter
    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }

    private fun splitToPager() {
        var i = 0
        while (i < pageCount) {
            val start = i * 9
            val end = if (start + 9 >= list.size) start + (list.size - start) else start + 9
            val buttonsInPage = ArrayList(list.subList(start, end))
            pages.add(buttonsInPage)
            i++
        }
    }

    private val pageCount: Int
        get() {
            return ceil(list.count().toFloat() / 9f).toInt()
        }
}