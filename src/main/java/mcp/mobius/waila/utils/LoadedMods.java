package mcp.mobius.waila.utils;

import cpw.mods.fml.common.Loader;

public class LoadedMods {

    public static boolean FORGE_MULTIPART = Loader.isModLoaded("ForgeMultipart");
    public static boolean NEI = Loader.isModLoaded("NotEnoughItems");
    public static boolean GT5U = Loader.isModLoaded("gregtech_nh");
    public static boolean WAILA_PLUGINS = Loader.isModLoaded("wailaplugins");
}
