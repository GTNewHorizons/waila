package mcp.mobius.waila.addons.exu;

import java.lang.reflect.Field;

import org.apache.logging.log4j.Level;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import mcp.mobius.waila.utils.LoadedMods;

public class ExtraUtilitiesModule {

    public static Class<?> TileEntityDrum = null;

    public static Class<?> TileEntityGenerator = null;
    public static Field TileEntityGenerator_storage = null;

    public static Class<?> EnergyStoragePlus = null;
    public static Field EnergyStoragePlus_energy = null;
    public static Field EnergyStoragePlus_capacity = null;

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

        try {
            TileEntityGenerator = Class.forName("com.rwtema.extrautils.tileentity.generators.TileEntityGenerator");
            TileEntityGenerator_storage = TileEntityGenerator.getDeclaredField("storage");
            TileEntityGenerator_storage.setAccessible(true);

            EnergyStoragePlus = Class.forName("com.rwtema.extrautils.tileentity.generators.EnergyStoragePlus");
            EnergyStoragePlus_energy = EnergyStoragePlus.getDeclaredField("energy");
            EnergyStoragePlus_capacity = EnergyStoragePlus.getDeclaredField("capacity");
            EnergyStoragePlus_energy.setAccessible(true);
            EnergyStoragePlus_capacity.setAccessible(true);

            GeneratorEnergyHandler generatorHandler = new GeneratorEnergyHandler();
            ModuleRegistrar.instance().addConfigRemote("Extra Utilites", "extrautilites.energyhandler");
            ModuleRegistrar.instance().addConfigRemote("Extra Utilites", "extrautilites.rfenergybar");
            ModuleRegistrar.instance().registerBodyProvider(generatorHandler, TileEntityGenerator);
            ModuleRegistrar.instance().registerNBTProvider(generatorHandler, TileEntityGenerator);

            Waila.log.log(Level.INFO, "[ExtraUtilities] Successfully loading generator hooks.");
        } catch (Exception e) {
            Waila.log.log(Level.WARN, "[ExtraUtilities] Error while loading generator hooks: " + e);
        }
    }
}
