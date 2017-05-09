package mcp.mobius.waila.addons.openblocks;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import org.apache.logging.log4j.Level;

public class OpenBlocksModule {

    public static Class ModOpenBlocks = null;
    public static Class TileEntityTank = null;

    public static void register() {
        try {
            final Class ModOpenBlocks = Class.forName("openblocks.OpenBlocks");
            Waila.log.log(Level.INFO, "OpenBlocks mod found.");
        } catch (final ClassNotFoundException e) {
            Waila.log.log(Level.INFO, "[OpenBlocks] OpenBlocks mod not found.");
            return;
        }

        try {
            TileEntityTank = Class.forName("openblocks.common.tileentity.TileEntityTank");
        } catch (final ClassNotFoundException e) {
            Waila.log.log(Level.WARN, "[OpenBlocks] Class not found. " + e);
            return;
        }

        ModuleRegistrar.instance().addConfigRemote("OpenBlocks", "openblocks.fluidamount");
        ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerTank(), TileEntityTank);
        ModuleRegistrar.instance().registerHeadProvider(new HUDHandlerTank(), TileEntityTank);
        ModuleRegistrar.instance().registerNBTProvider(new HUDHandlerTank(), TileEntityTank);
    }
}
