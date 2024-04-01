package mcp.mobius.waila.addons.buildcraft;

import java.lang.reflect.Method;

import net.minecraftforge.common.util.ForgeDirection;

import org.apache.logging.log4j.Level;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.ModuleRegistrar;

public class BCModule {

    public static Class<?> TileTank = null;
    public static Method TileTank_getTankInfo = null;

    public static void register() {
        try {
            TileTank = Class.forName("buildcraft.factory.TileTank");
            TileTank_getTankInfo = TileTank.getMethod("getTankInfo", ForgeDirection.class);

            ModuleRegistrar.instance().addConfig("Buildcraft", "bc.tankamount");
            ModuleRegistrar.instance().addConfig("Buildcraft", "bc.tanktype");
            ModuleRegistrar.instance().registerHeadProvider(new HUDHandlerBCTanks(), TileTank);
            ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerBCTanks(), TileTank);

        } catch (ClassNotFoundException e) {
            Waila.log.log(Level.WARN, "[BC] Class not found. " + e);
        } catch (NoSuchMethodException e) {
            Waila.log.log(Level.WARN, "[BC] Method not found." + e);
        }

    }

}
