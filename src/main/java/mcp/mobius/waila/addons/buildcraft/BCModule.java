package mcp.mobius.waila.addons.buildcraft;

import java.lang.reflect.Method;

import net.minecraftforge.common.util.ForgeDirection;

import org.apache.logging.log4j.Level;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import mcp.mobius.waila.utils.LoadedMods;

public class BCModule {

    public static Class<?> TileTank = null;
    public static Method TileTank_getTankInfo = null;

    public static void register() {
        if (LoadedMods.WAILA_PLUGINS) {
            return;
        }

        try {
            TileTank = Class.forName("buildcraft.factory.TileTank");
            TileTank_getTankInfo = TileTank.getMethod("getTankInfo", ForgeDirection.class);

            Waila.log.log(Level.INFO, "Buildcraft mod found.");
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            Waila.log.log(Level.INFO, "[BC] Buildcraft mod not found.");
            return;
        }

        ModuleRegistrar.instance().addConfig("Buildcraft", "bc.tanktype");
        ModuleRegistrar.instance().addConfig("Buildcraft", "bc.tankamount");

        TankFluidHandler tankFluidHandler = new TankFluidHandler();
        ModuleRegistrar.instance().registerHeadProvider(tankFluidHandler, TileTank);
        ModuleRegistrar.instance().registerBodyProvider(tankFluidHandler, TileTank);
    }

}
