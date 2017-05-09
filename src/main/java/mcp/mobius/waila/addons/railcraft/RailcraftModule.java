package mcp.mobius.waila.addons.railcraft;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import org.apache.logging.log4j.Level;

import java.lang.reflect.Method;

public class RailcraftModule {
    public static Class TileTankBase = null;
    public static Class ITankTile = null;
    public static Method ITankTile_getTank = null;
    //public static Class  StandardTank = null;

    public static void register() {
        try {
            final Class ModRailcraft = Class.forName("mods.railcraft.common.core.Railcraft");
            Waila.log.log(Level.INFO, "Railcraft mod found.");
        } catch (final ClassNotFoundException e) {
            Waila.log.log(Level.INFO, "[Railcraft] Railcraft mod not found.");
            return;
        }

        try {
            TileTankBase = Class.forName("mods.railcraft.common.blocks.machine.beta.TileTankBase");
            ITankTile = Class.forName("mods.railcraft.common.blocks.machine.ITankTile");
            ITankTile_getTank = ITankTile.getMethod("getTank");
            //StandardTank = Class.forName("mods.railcraft.common.fluids.tanks.StandardTank");

        } catch (final ClassNotFoundException e) {
            Waila.log.log(Level.WARN, "[Railcraft] Class not found. " + e);
            return;
        } catch (final NoSuchMethodException e) {
            Waila.log.log(Level.WARN, "[Railcraft] Method not found." + e);
            return;
        }

        ModuleRegistrar.instance().addConfigRemote("Railcraft", "railcraft.fluidamount");
        ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerTank(), TileTankBase);
        ModuleRegistrar.instance().registerHeadProvider(new HUDHandlerTank(), TileTankBase);
        ModuleRegistrar.instance().registerNBTProvider(new HUDHandlerTank(), TileTankBase);
    }
}
