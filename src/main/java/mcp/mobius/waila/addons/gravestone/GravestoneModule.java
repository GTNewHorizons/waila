package mcp.mobius.waila.addons.gravestone;

import org.apache.logging.log4j.Level;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.ModuleRegistrar;

public class GravestoneModule {

    public static Class<?> BlockGraveStone = null;

    public static void register() {

        try {
            Class.forName("GraveStone.ModGraveStone");
            Waila.log.log(Level.INFO, "GraveStone mod found.");
        } catch (ClassNotFoundException e) {
            Waila.log.log(Level.INFO, "[GraveStone] GraveStone mod not found.");
            return;
        }

        try {
            BlockGraveStone = Class.forName("GraveStone.block.BlockGSGraveStone");
        } catch (ClassNotFoundException e) {
            Waila.log.log(Level.WARN, "[GraveStone] Class not found. " + e);
        }

        ModuleRegistrar.instance().registerStackProvider(new HUDHandlerGravestone(), BlockGraveStone);
        ModuleRegistrar.instance().registerNBTProvider(new HUDHandlerGravestone(), BlockGraveStone);
    }

}
