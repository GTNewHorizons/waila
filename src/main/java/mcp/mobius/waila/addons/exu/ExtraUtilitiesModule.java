package mcp.mobius.waila.addons.exu;

import org.apache.logging.log4j.Level;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import mcp.mobius.waila.utils.LoadedMods;

public class ExtraUtilitiesModule {

    public static Class<?> TileEntityDrum = null;

    public static void register() {
        if (LoadedMods.WAILA_PLUGINS) {
            return;
        }

        try {
            TileEntityDrum = Class.forName("com.rwtema.extrautils.tileentity.TileEntityDrum");
            Waila.log.log(Level.INFO, "ExtraUtilities mod found.");
        } catch (ClassNotFoundException e) {
            Waila.log.log(Level.INFO, "[ExtraUtilities] ExtraUtilities mod not found.");
            return;
        }

        ModuleRegistrar.instance().addConfigRemote("ExtraUtilities", "extrautilities.fluidamount");
        ModuleRegistrar.instance().registerBodyProvider(new DrumFluidHandler(), TileEntityDrum);
    }
}
