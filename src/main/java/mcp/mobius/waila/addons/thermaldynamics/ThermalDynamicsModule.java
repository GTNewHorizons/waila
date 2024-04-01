package mcp.mobius.waila.addons.thermaldynamics;

import org.apache.logging.log4j.Level;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.ModuleRegistrar;

public class ThermalDynamicsModule {

    public static Class<?> TileFluidDuct = null;

    public static void register() {
        // XXX : We register the Fluiduct
        try {
            TileFluidDuct = Class.forName("cofh.thermaldynamics.ducts.fluid.TileFluidDuct");

            ModuleRegistrar.instance().addConfigRemote("Thermal Dynamics", "thermaldynamics.fluidDuctsFluid");
            ModuleRegistrar.instance().addConfigRemote("Thermal Dynamics", "thermaldynamics.fluidDuctsAmount");
            ModuleRegistrar.instance().registerHeadProvider(new HUDHandlerDuct(), TileFluidDuct);
            ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerDuct(), TileFluidDuct);
            ModuleRegistrar.instance().registerNBTProvider(new HUDHandlerDuct(), TileFluidDuct);

        } catch (Exception e) {
            Waila.log.log(Level.WARN, "[Thermal Dynamics] Error while loading FluidDuct hooks." + e);
        }
    }

}
