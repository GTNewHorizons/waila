package mcp.mobius.waila.utils;

import java.text.NumberFormat;
import java.util.Locale;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;

import gregtech.api.util.GTUtility;

public class NumberFormatter implements IResourceManagerReloadListener {

    private static NumberFormat numberFormat = NumberFormat.getInstance(getCurrentLocale());

    static {
        numberFormat.setGroupingUsed(true);
    }

    public static String format(long number) {
        if (LoadedMods.GT5U) {
            return GTUtility.formatNumbers(number);
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

    @Override
    public void onResourceManagerReload(IResourceManager p_110549_1_) {
        numberFormat = NumberFormat.getInstance(getCurrentLocale());
        numberFormat.setGroupingUsed(true);
    }
}
