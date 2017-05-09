package mcp.mobius.waila.cbcore;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.Language;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.SortedSet;

/**
 * Easy localisation access.
 */
public class LangUtil {
    public static LangUtil instance = new LangUtil(null);

    public String prefix;

    public LangUtil(final String prefix) {
        this.prefix = prefix;
    }

    public static String translateG(final String s, final Object... format) {
        return instance.translate(s, format);
    }

    public String translate(String s, final Object... format) {
        if (prefix != null && !s.startsWith(prefix + "."))
            s = prefix + "." + s;
        String ret = I18n.translateToLocal(s);//LanguageRegistry.instance().getStringLocalization(s);
        if (ret.length() == 0)
            ret = I18n.translateToLocal(s);//LanguageRegistry.instance().getStringLocalization(s, "en_US");
        if (ret.length() == 0)
            ret = I18n.translateToLocal(s);
        if (ret.length() == 0)
            return s;
        if (format.length > 0)
            ret = String.format(ret, format);
        return ret;
    }

    public void addLangFile(final InputStream resource, final String lang) throws IOException {
//        LanguageRegistry reg = LanguageRegistry.instance();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(resource, "UTF-8"));
        while (true) {
            final String read = reader.readLine();
            if (read == null)
                break;

            final int equalIndex = read.indexOf('=');
            if (equalIndex == -1)
                continue;
            String key = read.substring(0, equalIndex);
            final String value = read.substring(equalIndex + 1);
            if (prefix != null)
                key = prefix + "." + key;
//            reg.addStringLocalization(key, lang, value);
        }
    }

    @SideOnly(Side.CLIENT)
    public static LangUtil loadLangDir(final String domain) {
        return new LangUtil(domain).addLangDir(new ResourceLocation(domain, "lang"));
    }

    @SideOnly(Side.CLIENT)
    public LangUtil addLangDir(final ResourceLocation dir) {
        final IResourceManager resManager = Minecraft.getMinecraft().getResourceManager();
        for (final Language lang : (SortedSet<Language>) Minecraft.getMinecraft().getLanguageManager().getLanguages()) {
            final String langID = lang.getLanguageCode();
            final IResource langRes;
            try {
                langRes = resManager.getResource(new ResourceLocation(dir.getResourceDomain(), dir.getResourcePath() + '/' + langID + ".lang"));
            } catch (final Exception e) {
                continue;
            }
            try {
                addLangFile(langRes.getInputStream(), langID);
            } catch (final IOException e) {
                System.err.println("Failed to load lang resource. domain=" + prefix + ", resource=" + langRes);
                e.printStackTrace();
            }
        }
        return this;
    }
}
