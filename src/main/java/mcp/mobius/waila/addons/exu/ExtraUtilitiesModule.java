package mcp.mobius.waila.addons.exu;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import org.apache.logging.log4j.Level;

public class ExtraUtilitiesModule {

    public static Class ModExtraUtilities = null;
    public static Class TileEntityDrum = null;

    public static void register() {


        try {
            final Class ModExtraUtilities = Class.forName("com.rwtema.extrautils.ExtraUtils");
            Waila.log.log(Level.INFO, "ExtraUtilities mod found.");
        } catch (final ClassNotFoundException e) {
            Waila.log.log(Level.INFO, "[ExtraUtilities] ExtraUtilities mod not found.");
            return;
        }

        try {
            TileEntityDrum = Class.forName("com.rwtema.extrautils.tileentity.TileEntityDrum");
        } catch (final ClassNotFoundException e) {
            Waila.log.log(Level.WARN, "[ExtraUtilities] Class not found. " + e);
            return;
        }

        ModuleRegistrar.instance().addConfigRemote("ExtraUtilities", "extrautilities.fluidamount");
        ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerDrum(), TileEntityDrum);
        ModuleRegistrar.instance().registerNBTProvider(new HUDHandlerDrum(), TileEntityDrum);
    }
}
