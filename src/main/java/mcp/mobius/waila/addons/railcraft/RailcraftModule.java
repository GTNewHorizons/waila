package mcp.mobius.waila.addons.railcraft;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.logging.log4j.Level;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import mcp.mobius.waila.utils.LoadedMods;

public class RailcraftModule {

    public static Class<?> TileTankBase = null;
    public static Method TileTankBase_getTank = null;

    public static Class<?> StandardTank = null;
    public static Field StandardTank_capacity = null;
    public static Field StandardTank_renderData = null;

    public static Field StandardTank_renderDataFluid = null;
    public static Field StandardTank_renderDataAmount = null;

    public static void register() {
        if (LoadedMods.WAILA_PLUGINS) {
            return;
        }

        try {
            TileTankBase = Class.forName("mods.railcraft.common.blocks.machine.beta.TileTankBase");
            TileTankBase_getTank = TileTankBase.getMethod("getTank");

            StandardTank = Class.forName("mods.railcraft.common.fluids.tanks.StandardTank");
            StandardTank_capacity = StandardTank.getSuperclass().getDeclaredField("capacity");
            StandardTank_capacity.setAccessible(true);
            StandardTank_renderData = StandardTank.getField("renderData");

            StandardTank_renderDataFluid = StandardTank_renderData.getType().getField("fluid");
            StandardTank_renderDataAmount = StandardTank_renderData.getType().getField("amount");

            Waila.log.log(Level.INFO, "Railcraft mod found.");
        } catch (ClassNotFoundException e) {
            Waila.log.log(Level.WARN, "[Railcraft] Class not found. " + e);
            return;
        } catch (NoSuchMethodException e) {
            Waila.log.log(Level.WARN, "[Railcraft] Method not found." + e);
            return;
        } catch (NoSuchFieldException e) {
            Waila.log.log(Level.WARN, "[Railcraft] Field not found." + e);
            return;
        }

        ModuleRegistrar.instance().addConfigRemote("Railcraft", "railcraft.fluidamount");

        TankFluidHandler tankFluidHandler = new TankFluidHandler();
        ModuleRegistrar.instance().registerHeadProvider(tankFluidHandler, TileTankBase);
        ModuleRegistrar.instance().registerBodyProvider(tankFluidHandler, TileTankBase);
    }
}
