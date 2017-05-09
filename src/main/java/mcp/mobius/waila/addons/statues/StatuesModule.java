package mcp.mobius.waila.addons.statues;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import org.apache.logging.log4j.Level;

import java.lang.reflect.Field;

public class StatuesModule {

    public static Class TileEntityStatue = null;
    public static Field skinName = null;

    public static void register() {
        try {
            final Class Statues = Class.forName("info.jbcs.minecraft.statues.Statues");
            Waila.log.log(Level.INFO, "Statues mod found.");
        } catch (final ClassNotFoundException e) {
            Waila.log.log(Level.INFO, "[Statues] Statues mod not found.");
            return;
        }

        try {
            TileEntityStatue = Class.forName("info.jbcs.minecraft.statues.TileEntityStatue");
            skinName = TileEntityStatue.getDeclaredField("skinName");
        } catch (final ClassNotFoundException e) {
            Waila.log.log(Level.WARN, "[Statues] Class not found. " + e);
            return;
        } catch (final NoSuchFieldException e) {
            Waila.log.log(Level.WARN, "[Statues] Class not found. " + e);
            return;
        }

        ModuleRegistrar.instance().registerHeadProvider(new HUDHandlerStatues(), TileEntityStatue);
        ModuleRegistrar.instance().registerTailProvider(new HUDHandlerStatues(), TileEntityStatue);
        ModuleRegistrar.instance().registerNBTProvider(new HUDHandlerStatues(), TileEntityStatue);
    }

}
