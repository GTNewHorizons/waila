package mcp.mobius.waila.addons.magicalcrops;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import org.apache.logging.log4j.Level;

public class MagicalCropsModule {
    public static Class BlockMagicalCrops = null;


    public static void register() {
        try {
            final Class MagicalCrops = Class.forName("magicalcrops.mod_sCrops");
            Waila.log.log(Level.INFO, "MagicalCrops mod found.");
        } catch (final ClassNotFoundException e) {
            Waila.log.log(Level.INFO, "[MagicalCrops] MagicalCrops mod not found.");
            return;
        }

        try {
            BlockMagicalCrops = Class.forName("magicalcrops.BlockMagicalCrops");
        } catch (final ClassNotFoundException e) {
            Waila.log.log(Level.WARN, "[MagicalCrops] Class not found. " + e);
            return;
        }

        ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerMagicalCrops(), BlockMagicalCrops);
    }
}
