package ir.amozkade.advancedAsisstiveTouche.helper

import android.content.Context
import android.database.DataSetObserver
import android.view.*
import android.widget.Spinner
import android.widget.SpinnerAdapter
import androidx.databinding.DataBindingUtil
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.databinding.CvSpinnerBinding


class CustomSpinnerAdapter<T>(private val listData: List<T>, private val textColor: Int ,private val backColor:Int , private val defaultTitle: String) : SpinnerAdapter {

    override fun registerDataSetObserver(observer: DataSetObserver) = Unit
    override fun unregisterDataSetObserver(observer: DataSetObserver) = Unit

    override fun getCount(): Int {
        return listData.size
    }

    override fun getItem(position: Int): T {
        return listData[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        return  getCustomView(position,parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View? {
        return getCustomView(position,parent)
    }

    private fun getCustomView(position: Int ,parent: ViewGroup): View? {
        val inflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val itemView = DataBindingUtil.inflate<CvSpinnerBinding>(inflater , R.layout.cv_spinner  , parent, false)
        itemView.txtSpinner.text = listData[position].toString()
        itemView.txtSpinner.setTextColor(textColor)
        itemView.root.setBackgroundColor(backColor)
        //itemView.findViewById<View>(R.id.cv_Spinner).setOnClickListener{onClickSpinnerListener.onclick(listData[position - 1], position, id) }
        return itemView.root
    }

    override fun getItemViewType(position: Int): Int {
        return 1
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun isEmpty(): Boolean {
        return false
    }

    companion object {
        fun closeSpinner(spinner: Spinner?) {
            if (spinner != null) {
                try {
                    val method = Spinner::class.java.getDeclaredMethod("onDetachedFromWindow")
                    method.isAccessible = true
                    method.invoke(spinner)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
    }
}
