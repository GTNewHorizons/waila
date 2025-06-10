package mcp.mobius.waila.overlay;

import net.minecraftforge.common.config.Configuration;

import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.utils.Constants;

// spotless:off
public class OverlayConfig {

    public static int posX;
    public static int posY;
    public static int alpha;
    public static int bgcolor;
    public static int gradient1;
    public static int gradient2;
    public static int fontcolor;
    public static float scale;

    public static void updateColors() {
        alpha     =  (int) (ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_ALPHA, 0) / 100.0f * 256) << 24;
        bgcolor   = alpha + ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_BGCOLOR, 0);
        gradient1 = alpha + ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_GRADIENT1, 0);
        gradient2 = alpha + ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_GRADIENT2, 0);
        fontcolor =         ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_FONTCOLOR, 0);
    }
}
// spotless:on
