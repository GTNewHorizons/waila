package mcp.mobius.waila.addons.twilightforest;

import org.apache.logging.log4j.Level;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.ModuleRegistrar;

public class TwilightForestModule {

    public static Class<?> BlockTFRoots = null;
    public static Class<?> BlockTFPlant = null;
    public static Class<?> BlockTFSapling = null;

    public static void register() {

        try {
            Class.forName("twilightforest.TwilightForestMod");
            Waila.log.log(Level.INFO, "TwilightForestMod mod found.");
        } catch (ClassNotFoundException e) {
            Waila.log.log(Level.INFO, "[TwilightForestMod] TwilightForestMod mod not found.");
            return;
        }

        try {
            BlockTFRoots = Class.forName("twilightforest.block.BlockTFRoots");
            BlockTFPlant = Class.forName("twilightforest.block.BlockTFPlant");
            BlockTFSapling = Class.forName("twilightforest.block.BlockTFSapling");
        } catch (ClassNotFoundException e) {
            Waila.log.log(Level.WARN, "[TwilightForestMod] Class not found. " + e);
        }

        ModuleRegistrar.instance().registerStackProvider(new TwilightForestGenericOverride(), BlockTFRoots);
        ModuleRegistrar.instance().registerStackProvider(new TwilightForestGenericOverride(), BlockTFPlant);
    }

}
