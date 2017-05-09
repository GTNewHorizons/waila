package mcp.mobius.waila.addons.harvestcraft;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import org.apache.logging.log4j.Level;

public class HarvestcraftModule {
    public static Class TileEntityPamCrop = null;


    public static void register() {
        try {
            final Class PamHarvestCraft = Class.forName("assets.pamharvestcraft.PamHarvestCraft");
            Waila.log.log(Level.INFO, "PamHarvestCraft mod found.");
        } catch (final ClassNotFoundException e) {
            Waila.log.log(Level.INFO, "[PamHarvestCraft] PamHarvestCraft mod not found.");
            return;
        }

        try {
            TileEntityPamCrop = Class.forName("assets.pamharvestcraft.TileEntityPamCrop");
        } catch (final ClassNotFoundException e) {
            Waila.log.log(Level.WARN, "[PamHarvestCraft] Class not found. " + e);
            return;
        }

        ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerPamCrop(), TileEntityPamCrop);
    }
}
