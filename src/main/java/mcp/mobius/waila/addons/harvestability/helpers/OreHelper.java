package mcp.mobius.waila.addons.harvestability.helpers;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class OreHelper {

    public static boolean isBlockAnOre(Block block) {
        return isBlockAnOre(block, 0);
    }

    public static boolean isBlockAnOre(Block block, int metadata) {
        return isItemAnOre(new ItemStack(block, 1, metadata));
    }

    public static boolean isItemAnOre(ItemStack itemStack) {
        // check the ore dictionary to see if it starts with "ore"
        int[] oreIDs = OreDictionary.getOreIDs(itemStack);
        for (int oreID : oreIDs) {
            if (OreDictionary.getOreName(oreID).startsWith("ore")) return true;
        }

        // ore in the display name (but not part of another word)
        if (itemStack.getDisplayName().matches(".*(^|\\s)([oO]re)($|\\s).*")) return true;

        // ore as the start of the unlocalized name
        if (itemStack.getUnlocalizedName().startsWith("ore")) return true;

        return false;
    }
}
