package mcp.mobius.waila.addons.enderio;

import java.lang.reflect.Method;

import org.apache.logging.log4j.Level;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.ModuleRegistrar;

public class EnderIOModule {

    public static Class<?> TileEntityEio = null;

    public static Class<?> TileCapBank = null;
    public static Method TileCapBank_getEnergyStoredL = null;
    public static Method TileCapBank_getMaxEnergyStoredL = null;

    public static Class<?> AbstractGeneratorEntity = null;
    public static Method AbstractGeneratorEntity_isActive = null;
    public static Method AbstractGeneratorEntity_getPowerUsePerTick = null;

    public static void register() {
        try {
            TileEntityEio = Class.forName("crazypants.enderio.TileEntityEio");

            try {
                TileCapBank = Class.forName("crazypants.enderio.machine.capbank.TileCapBank");
                TileCapBank_getEnergyStoredL = TileCapBank.getMethod("getEnergyStoredL");
                TileCapBank_getMaxEnergyStoredL = TileCapBank.getMethod("getMaxEnergyStoredL");

                TileCapBank_getEnergyStoredL.setAccessible(true);
                TileCapBank_getMaxEnergyStoredL.setAccessible(true);
            } catch (Exception e) {
                Waila.log.log(Level.WARN, "[Ender IO] Error while loading capbank hooks: " + e);
            }

            try {
                AbstractGeneratorEntity = Class.forName("crazypants.enderio.machine.generator.AbstractGeneratorEntity");
                AbstractGeneratorEntity_isActive = AbstractGeneratorEntity.getMethod("isActive");
                AbstractGeneratorEntity_getPowerUsePerTick = AbstractGeneratorEntity.getMethod("getPowerUsePerTick");

                AbstractGeneratorEntity_isActive.setAccessible(true);
                AbstractGeneratorEntity_getPowerUsePerTick.setAccessible(true);
            } catch (Exception e) {
                Waila.log.log(Level.WARN, "[Ender IO] Error while loading generator hooks: " + e);
            }

            EnderIOEnergyHandler handler = new EnderIOEnergyHandler();

            ModuleRegistrar.instance().addConfigRemote("Ender IO", "enderio.energyhandler");
            ModuleRegistrar.instance().addConfigRemote("Ender IO", "enderio.rfenergybar");
            ModuleRegistrar.instance().registerBodyProvider(handler, TileEntityEio);
            ModuleRegistrar.instance().registerNBTProvider(handler, TileEntityEio);

            Waila.log.log(Level.INFO, "[Ender IO] Universal RF/t energy bar module successfully started.");
        } catch (ClassNotFoundException e) {
            Waila.log.log(Level.INFO, "[Ender IO] Base class TileEntityEio not found.");
        } catch (Exception e) {
            Waila.log.log(Level.WARN, "[Ender IO] Error registering module: " + e);
        }
    }
}
