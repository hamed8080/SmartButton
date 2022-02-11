package ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.schedule

import android.os.Bundle
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.databinding.ActivityScheduleBinding
import ir.amozkade.advancedAsisstiveTouche.mvvm.BaseActivity
import ir.amozkade.advancedAsisstiveTouche.mvvm.dictionary.leitners.models.Leitner
import java.lang.ref.WeakReference

@AndroidEntryPoint
class ScheduleActivity : BaseActivity(), ScheduleDelegate {

    private lateinit var mBinding: ActivityScheduleBinding


    override lateinit var leitner : Leitner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_schedule)
        //loading set
        super.addLoadingsToContainer(mBinding.root as ViewGroup)

        (intent.getParcelableExtra("product") as? Leitner)?.let{
            leitner = it
        }
        savedInstanceState?.getParcelable<Leitner>("product")?.let { product ->
            this.leitner = product
        }
        mBinding.actionBar.setActionBarTitle(leitner.name)
        mBinding.root.findViewById<MaterialButton>(R.id.btnBack).setOnClickListener { finish() }
        mBinding.rcvSchedule.setHasFixedSize(true)
        mBinding.rcvSchedule.setItemViewCacheSize(5)
        mBinding.rcvSchedule.adapter = ScheduleAdapter(this, this, WeakReference(this))
        mBinding.rcvSchedule.layoutManager = LinearLayoutManager(this)
        mBinding.rcvSchedule.adapter?.notifyDataSetChanged()
        showLoading()
    }

}
