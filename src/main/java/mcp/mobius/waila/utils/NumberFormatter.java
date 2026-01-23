package mcp.mobius.waila.utils;

import static com.gtnewhorizon.gtnhlib.util.numberformatting.NumberFormatUtil.formatNumber;

import java.text.NumberFormat;
import java.util.Locale;

import net.minecraft.client.Minecraft;

public class NumberFormatter {

    private static NumberFormat numberFormat = NumberFormat.getInstance(getCurrentLocale());

    public static String format(long number) {
        if (LoadedMods.GTNH_LIB) {
            return formatNumber(number);
        }

        return numberFormat.format(number);
    }

    public static Locale getCurrentLocale() {
        String langCode = Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode();
        String[] parts = langCode.split("_", 2);
        String language = parts[0];
        String country = parts.length == 1 ? "" : parts[1];

        return new Locale(language, country);
    }

    public static void onResourcesReload() {
        numberFormat = NumberFormat.getInstance(getCurrentLocale());
    }
}
