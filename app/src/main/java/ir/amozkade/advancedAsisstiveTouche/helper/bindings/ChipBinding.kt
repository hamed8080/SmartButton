package ir.amozkade.advancedAsisstiveTouche.helper.bindings

import androidx.databinding.BindingAdapter
import com.google.android.material.chip.Chip
import ir.amozkade.advancedAsisstiveTouche.R
import ir.amozkade.advancedAsisstiveTouche.mvvm.tickets.model.TicketStatus

@BindingAdapter("setChipStyle")
fun setTextView(chip: Chip , ticketStatus: TicketStatus) {
    if( ticketStatus == TicketStatus.IN_PROGRESS){
        chip.setTextAppearanceResource(R.style.chipInProgressTicketText)
        chip.setChipBackgroundColorResource(R.color.primary_dark_color)
    }else{
        chip.setTextAppearanceResource(R.style.chipSolvedTicketText)
        chip.setChipBackgroundColorResource( R.color.green)
    }
}


