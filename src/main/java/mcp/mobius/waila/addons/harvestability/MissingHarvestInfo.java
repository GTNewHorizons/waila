package mcp.mobius.waila.addons.harvestability;

import net.minecraft.init.Blocks;

public class MissingHarvestInfo {

    public static void init() {
        vanilla();
    }

    public static void vanilla() {
        Blocks.web.setHarvestLevel("sword", 0);
    }
}
