package com.example.uts_a22202302996.util;

import java.text.NumberFormat;
import java.util.Locale;

public class RupiahFormatter {

    public static String formatRupiah(double amount) {
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        String formatted = format.format(amount);

        // Remove decimal part if it's .00
        if (formatted.contains(",00")) {
            formatted = formatted.replace(",00", "");
        }

        // Format untuk nominal besar
        formatted = formatted.replace("Rp", "Rp ");

        return formatted;
    }

    public static String formatRupiahCompact(double amount) {
        if (amount < 1000) {
            return formatRupiah(amount);
        }

        String[] units = {"", "rb", "jt", "M", "T"};
        int unitIndex = 0;

        while (amount >= 1000 && unitIndex < units.length - 1) {
            amount /= 1000;
            unitIndex++;
        }

        return "Rp " + String.format(Locale.getDefault(), "%.1f %s", amount, units[unitIndex]);
    }
}
