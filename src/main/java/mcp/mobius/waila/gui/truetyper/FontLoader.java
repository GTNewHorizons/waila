package mcp.mobius.waila.gui.truetyper;

/**
 * TrueTyper: Open Source TTF implementation for Minecraft.
 * Copyright (C) 2013 - Mr_okushama
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class FontLoader {

    public static TrueTypeFont loadSystemFont(final String name, final float defSize, final boolean antialias) {
        return loadSystemFont(name, defSize, antialias, Font.TRUETYPE_FONT);

    }

    public static TrueTypeFont loadSystemFont(final String name, final float defSize, final boolean antialias, final int type) {
        Font font;
        TrueTypeFont out = null;
        try {
            font = new Font(name, type, (int) defSize);
            font = font.deriveFont(defSize);
            out = new TrueTypeFont(font, antialias);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return out;
    }

    public static TrueTypeFont createFont(final ResourceLocation res, final float defSize, final boolean antialias) {
        return createFont(res, defSize, antialias, Font.TRUETYPE_FONT);
    }

    public static TrueTypeFont createFont(final ResourceLocation res, final float defSize, final boolean antialias, final int type) {
        Font font;
        TrueTypeFont out = null;
        try {
            font = Font.createFont(type, Minecraft.getMinecraft().getResourceManager().getResource(res).getInputStream());
            font = font.deriveFont(defSize);
            out = new TrueTypeFont(font, antialias);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return out;
    }

}