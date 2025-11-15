package mcp.mobius.waila.addons.thermaldynamics;

import org.apache.logging.log4j.Level;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import mcp.mobius.waila.utils.LoadedMods;

public class ThermalDynamicsModule {

    public static Class<?> TileFluidDuct = null;

    public static void register() {
        if (LoadedMods.WAILA_PLUGINS) {
            return;
        }

        try {
            TileFluidDuct = Class.forName("cofh.thermaldynamics.duct.fluid.TileFluidDuct");
            Waila.log.log(Level.INFO, "Thermal Dynamics mod found.");
        } catch (ClassNotFoundException e) {
            Waila.log.log(Level.INFO, "[Thermal Dynamics] Thermal Dynamics mod not found.");
            return;
        } catch (Exception e) {
            Waila.log.log(Level.WARN, "[Thermal Dynamics] Error while loading FluidDuct hooks. {}", e);
            return;
        }

        ModuleRegistrar.instance().addConfigRemote("Thermal Dynamics", "thermaldynamics.fluidDuctsFluid");
        ModuleRegistrar.instance().addConfigRemote("Thermal Dynamics", "thermaldynamics.fluidDuctsAmount");

        FluiductFluidHandler fluiductFluidHandler = new FluiductFluidHandler();
        ModuleRegistrar.instance().registerHeadProvider(fluiductFluidHandler, TileFluidDuct);
        ModuleRegistrar.instance().registerBodyProvider(fluiductFluidHandler, TileFluidDuct);
        ModuleRegistrar.instance().registerNBTProvider(fluiductFluidHandler, TileFluidDuct);
    }

}
