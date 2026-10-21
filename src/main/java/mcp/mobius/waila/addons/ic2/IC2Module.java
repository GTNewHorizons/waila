package mcp.mobius.waila.addons.ic2;

import java.lang.reflect.Field;

import org.apache.logging.log4j.Level;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.ModuleRegistrar;

public class IC2Module {

    public static Class<?> TileBaseGenerator = null;
    public static Field TileBaseGenerator_storage = null;
    public static Field TileBaseGenerator_maxStorage = null;
    public static Field TileBaseGenerator_production = null;

    public static Class<?> TileGeoGenerator = null;
    public static Field TileGeoGenerator_storage = null;
    public static Field TileGeoGenerator_maxStorage = null;
    public static Field TileGeoGenerator_production = null;

    public static Class<?> TileKineticGenerator = null;
    public static Field TileKineticGenerator_storage = null;
    public static Field TileKineticGenerator_maxStorage = null;
    public static Field TileKineticGenerator_production = null;

    public static Class<?> TileSemifluidGenerator = null;
    public static Field TileSemifluidGenerator_storage = null;
    public static Field TileSemifluidGenerator_maxStorage = null;
    public static Field TileSemifluidGenerator_production = null;

    public static Class<?> TileStirlingGenerator = null;
    public static Field TileStirlingGenerator_storage = null;
    public static Field TileStirlingGenerator_maxStorage = null;
    public static Field TileStirlingGenerator_production = null;

    public static Class<?> TileElectricBlock = null;
    public static Field TileElectricBlock_energy = null;
    public static Field TileElectricBlock_maxStorage = null;
    public static Field TileElectricBlock_output = null;

    public static Class<?> TileTransformer = null;
    public static Field TileTransformer_mode = null;
    public static Field TileTransformer_tier = null;
    public static Field TileTransformer_energy = null;

    public static void register() {
        // XXX : We register the Energy interface first
        try {
            TileBaseGenerator = Class.forName("ic2.core.block.generator.tileentity.TileEntityBaseGenerator");
            TileBaseGenerator_storage = TileBaseGenerator.getDeclaredField("storage");
            TileBaseGenerator_maxStorage = TileBaseGenerator.getDeclaredField("maxStorage");
            TileBaseGenerator_production = TileBaseGenerator.getDeclaredField("production");

            TileGeoGenerator = Class.forName("ic2.core.block.generator.tileentity.TileEntityGeoGenerator");
            TileGeoGenerator_storage = TileGeoGenerator.getDeclaredField("storage");
            TileGeoGenerator_maxStorage = TileGeoGenerator.getDeclaredField("maxStorage");
            TileGeoGenerator_production = TileGeoGenerator.getDeclaredField("production");

            TileKineticGenerator = Class.forName("ic2.core.block.generator.tileentity.TileEntityKineticGenerator");
            TileKineticGenerator_storage = TileKineticGenerator.getDeclaredField("EUstorage");
            TileKineticGenerator_maxStorage = TileKineticGenerator.getDeclaredField("maxEUStorage");
            TileKineticGenerator_production = TileKineticGenerator.getDeclaredField("production");
            TileKineticGenerator_storage.setAccessible(true);
            TileKineticGenerator_maxStorage.setAccessible(true);
            TileKineticGenerator_production.setAccessible(true);

            TileSemifluidGenerator = Class.forName("ic2.core.block.generator.tileentity.TileEntitySemifluidGenerator");
            TileSemifluidGenerator_storage = TileSemifluidGenerator.getDeclaredField("storage");
            TileSemifluidGenerator_maxStorage = TileSemifluidGenerator.getDeclaredField("maxStorage");
            TileSemifluidGenerator_production = TileSemifluidGenerator.getDeclaredField("production");
            TileSemifluidGenerator_storage.setAccessible(true);
            TileSemifluidGenerator_maxStorage.setAccessible(true);
            TileSemifluidGenerator_production.setAccessible(true);

            TileStirlingGenerator = Class.forName("ic2.core.block.generator.tileentity.TileEntityStirlingGenerator");
            TileStirlingGenerator_storage = TileStirlingGenerator.getDeclaredField("EUstorage");
            TileStirlingGenerator_maxStorage = TileStirlingGenerator.getDeclaredField("maxEUStorage");
            TileStirlingGenerator_production = TileStirlingGenerator.getDeclaredField("production");

            ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerTEGenerator(), TileBaseGenerator);
            ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerTEGenerator(), TileGeoGenerator);
            ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerTEGenerator(), TileKineticGenerator);
            ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerTEGenerator(), TileSemifluidGenerator);
            ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerTEGenerator(), TileStirlingGenerator);

            ModuleRegistrar.instance().registerNBTProvider(new HUDHandlerTEGenerator(), TileBaseGenerator);
            ModuleRegistrar.instance().registerNBTProvider(new HUDHandlerTEGenerator(), TileGeoGenerator);
            ModuleRegistrar.instance().registerNBTProvider(new HUDHandlerTEGenerator(), TileKineticGenerator);
            ModuleRegistrar.instance().registerNBTProvider(new HUDHandlerTEGenerator(), TileSemifluidGenerator);
            ModuleRegistrar.instance().registerNBTProvider(new HUDHandlerTEGenerator(), TileStirlingGenerator);

            try {
                TileElectricBlock = Class.forName("ic2.core.block.wiring.TileEntityElectricBlock");
                TileElectricBlock_energy = TileElectricBlock.getDeclaredField("energy");
                TileElectricBlock_maxStorage = TileElectricBlock.getDeclaredField("maxStorage");
                TileElectricBlock_output = TileElectricBlock.getDeclaredField("output");

                TileElectricBlock_energy.setAccessible(true);
                TileElectricBlock_maxStorage.setAccessible(true);
                TileElectricBlock_output.setAccessible(true);

                ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerTEGenerator(), TileElectricBlock);
                ModuleRegistrar.instance().registerNBTProvider(new HUDHandlerTEGenerator(), TileElectricBlock);
            } catch (Exception e) {
                Waila.log.log(Level.WARN, "[IndustrialCraft 2] Error while loading energy storage hooks: " + e);
            }

            try {
                TileTransformer = Class.forName("ic2.core.block.wiring.TileEntityTransformer");
                TileTransformer_mode = TileTransformer.getDeclaredField("mode");
                TileTransformer_tier = TileTransformer.getDeclaredField("tier");
                TileTransformer_energy = TileTransformer.getDeclaredField("energy");

                TileTransformer_mode.setAccessible(true);
                TileTransformer_tier.setAccessible(true);
                TileTransformer_energy.setAccessible(true);

                ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerTEGenerator(), TileTransformer);
                ModuleRegistrar.instance().registerNBTProvider(new HUDHandlerTEGenerator(), TileTransformer);
            } catch (Exception e) {
                Waila.log.log(Level.WARN, "[IndustrialCraft 2] Error while loading transformer hooks: " + e);
            }

            ModuleRegistrar.instance().addConfigRemote("IndustrialCraft2", "ic2.storage");
            ModuleRegistrar.instance().addConfigRemote("IndustrialCraft2", "ic2.outputeu");
            ModuleRegistrar.instance().addConfigRemote("IndustrialCraft2", "ic2.euenergybar");
            ModuleRegistrar.instance().addConfigRemote("IndustrialCraft2", "ic2.transformer.mode");
            ModuleRegistrar.instance().addConfigRemote("IndustrialCraft2", "ic2.transformer.inout");

        } catch (Exception e) {
            if (e instanceof ClassNotFoundException || e instanceof NoSuchFieldException) {
                Waila.log.log(Level.INFO, "[IndustrialCraft 2] IndustrialCraft 2 mod not found.");
            } else {
                Waila.log.log(Level.WARN, "[IndustrialCraft 2] Error while loading generator hooks." + e);
            }
            return;
        }
        Waila.log.log(Level.INFO, "IndustrialCraft 2 mod found.");
    }

}
