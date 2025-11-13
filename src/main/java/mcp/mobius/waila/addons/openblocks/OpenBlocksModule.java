package mcp.mobius.waila.addons.openblocks;

import org.apache.logging.log4j.Level;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import mcp.mobius.waila.utils.LoadedMods;

public class OpenBlocksModule {

    public static Class<?> TileEntityTank = null;

    public static void register() {
        if (LoadedMods.WAILA_PLUGINS) {
            return;
        }

        try {
            TileEntityTank = Class.forName("openblocks.common.tileentity.TileEntityTank");
            Waila.log.log(Level.INFO, "OpenBlocks mod found.");
        } catch (ClassNotFoundException e) {
            Waila.log.log(Level.INFO, "[OpenBlocks] OpenBlocks mod not found.");
            return;
        }

        ModuleRegistrar.instance().addConfigRemote("OpenBlocks", "openblocks.fluidamount");

        TankFluidHandler tankFluidHandler = new TankFluidHandler();
        ModuleRegistrar.instance().registerBodyProvider(tankFluidHandler, TileEntityTank);
        ModuleRegistrar.instance().registerHeadProvider(tankFluidHandler, TileEntityTank);
    }
}
