package mcp.mobius.waila.addons.harvestability.helpers;

import mcp.mobius.waila.addons.harvestability.proxy.ProxyGregTech;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class BlockHelper {

    private static final HashMap<String, ItemStack> testTools = new HashMap<String, ItemStack>();
    static {
        testTools.put("pickaxe", new ItemStack(Items.wooden_pickaxe));
        testTools.put("shovel", new ItemStack(Items.wooden_shovel));
        testTools.put("axe", new ItemStack(Items.wooden_axe));
    }

    public static String getEffectiveToolOf(World world, int x, int y, int z, Block block, int metadata) {
        String effectiveTool = block.getHarvestTool(metadata);
        if (effectiveTool == null) {
            float hardness = block.getBlockHardness(world, x, y, z);
            if (hardness > 0f) {
                for (Map.Entry<String, ItemStack> testToolEntry : testTools.entrySet()) {
                    ItemStack testTool = testToolEntry.getValue();
                    if (testTool != null && testTool.getItem() instanceof ItemTool
                            && testTool.func_150997_a(block) >= ((ItemTool) testTool.getItem()).func_150913_i()
                                    .getEfficiencyOnProperMaterial()) {
                        effectiveTool = testToolEntry.getKey();
                        break;
                    }
                }
            }
        }
        return effectiveTool;
    }

    public static boolean isBlockUnbreakable(Block block, World world, int x, int y, int z) {
        return block.getBlockHardness(world, x, y, z) == -1.0f;
    }
}
