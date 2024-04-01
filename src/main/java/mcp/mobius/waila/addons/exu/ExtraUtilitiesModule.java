package mcp.mobius.waila.addons.exu;

import org.apache.logging.log4j.Level;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.ModuleRegistrar;

public class ExtraUtilitiesModule {

    public static Class<?> TileEntityDrum = null;

    public static void register() {

        try {
            Class.forName("com.rwtema.extrautils.ExtraUtils");
            Waila.log.log(Level.INFO, "ExtraUtilities mod found.");
        } catch (ClassNotFoundException e) {
            Waila.log.log(Level.INFO, "[ExtraUtilities] ExtraUtilities mod not found.");
            return;
        }

        try {
            TileEntityDrum = Class.forName("com.rwtema.extrautils.tileentity.TileEntityDrum");
        } catch (ClassNotFoundException e) {
            Waila.log.log(Level.WARN, "[ExtraUtilities] Class not found. " + e);
            return;
        }

        ModuleRegistrar.instance().addConfigRemote("ExtraUtilities", "extrautilities.fluidamount");
        ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerDrum(), TileEntityDrum);
        ModuleRegistrar.instance().registerNBTProvider(new HUDHandlerDrum(), TileEntityDrum);
    }
}
