package ir.amozkade.advancedAsisstiveTouche.helper.bindings;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

import androidx.databinding.BindingConversion;



public class PriceBinding {

    @BindingConversion
    public static String priceSeparator(BigDecimal price) {
        long intValOfBigDecimal = price.longValue();
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setGroupingSeparator(',');
        formatter.setDecimalFormatSymbols(symbols);
        return formatter.format(intValOfBigDecimal);
    }
}
