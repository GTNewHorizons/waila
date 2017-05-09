package mcp.mobius.waila.addons.agriculture;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import org.apache.logging.log4j.Level;

public class AgricultureModule {
    public static Class BlockCrop = null;


    public static void register() {
        try {
            final Class Agriculture = Class.forName("com.teammetallurgy.agriculture.Agriculture");
            Waila.log.log(Level.INFO, "Agriculture mod found.");
        } catch (final ClassNotFoundException e) {
            Waila.log.log(Level.INFO, "[Agriculture] Agriculture mod not found.");
            return;
        }

        try {
            BlockCrop = Class.forName("com.teammetallurgy.agriculture.crops.BlockCrop");
        } catch (final ClassNotFoundException e) {
            Waila.log.log(Level.WARN, "[Agriculture] Class not found. " + e);
            return;
        }

        ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerCrop(), BlockCrop);
    }
}
