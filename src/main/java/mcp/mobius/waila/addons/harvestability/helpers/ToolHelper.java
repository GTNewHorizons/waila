package mcp.mobius.waila.addons.harvestability.helpers;

import cpw.mods.fml.common.Loader;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeHooks;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ToolHelper {

    // tinkers construct
    private static Class<?> HarvestTool = null;
    private static Class<?> DualHarvestTool = null;
    private static Method getHarvestType = null;
    private static Method getSecondHarvestType = null;
    public static boolean tinkersConstructLoaded = false;

    public static void init() {
        if (Loader.isModLoaded("TConstruct")) {
            try {
                HarvestTool = Class.forName("tconstruct.library.tools.HarvestTool");
                DualHarvestTool = Class.forName("tconstruct.library.tools.DualHarvestTool");
                getHarvestType = HarvestTool.getDeclaredMethod("getHarvestType");
                getSecondHarvestType = DualHarvestTool.getDeclaredMethod("getSecondHarvestType");
                getHarvestType.setAccessible(true);
                getSecondHarvestType.setAccessible(true);
                tinkersConstructLoaded = true;
            } catch (Exception e) {
                return;
            }
        }
    }

    public static boolean hasToolTag(ItemStack itemStack) {
        return itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey("InfiTool");
    }

    public static NBTTagCompound getToolTag(ItemStack tool) {
        NBTTagCompound tag = null;
        if (tool.hasTagCompound()) tag = tool.getTagCompound().getCompoundTag("InfiTool");
        return tag;
    }

    public static int getPrimaryHarvestLevel(NBTTagCompound toolTag) {
        return toolTag.getInteger("HarvestLevel");
    }

    public static int getSecondaryHarvestLevel(NBTTagCompound toolTag) {
        return toolTag.getInteger("HarvestLevel2");
    }

    public static Set<String> getToolClassesOf(ItemStack tool) {
        return tool.getItem().getToolClasses(tool);
    }

    public static boolean isToolOfClass(ItemStack tool, String toolClass) {
        return getToolClassesOf(tool).contains(toolClass);
    }

    public static boolean toolHasAnyToolClass(ItemStack tool) {
        return !getToolClassesOf(tool).isEmpty();
    }

    public static boolean isToolEffectiveAgainst(ItemStack tool, Block block, int metadata, String effectiveToolClass) {
        if (tinkersConstructLoaded && HarvestTool.isInstance(tool.getItem())) {
            Item item = tool.getItem();
            List<String> harvestTypes = new ArrayList<String>();
            try {
                harvestTypes.add((String) getHarvestType.invoke(item));
            } catch (Exception e) {
                e.printStackTrace();
                tinkersConstructLoaded = false;
            }

            if (DualHarvestTool.isInstance(item)) {
                try {
                    harvestTypes.add((String) getSecondHarvestType.invoke(item));
                } catch (Exception e) {
                    e.printStackTrace();
                    tinkersConstructLoaded = false;
                }
            }

            return harvestTypes.contains(effectiveToolClass);
        }
        return ForgeHooks.isToolEffective(tool, block, metadata)
                || (toolHasAnyToolClass(tool) ? isToolOfClass(tool, effectiveToolClass)
                        : tool.getItem().getDigSpeed(tool, block, metadata) > 1.5f);
    }

    public static boolean canToolHarvestLevel(ItemStack tool, Block block, int metadata, int harvestLevel) {
        boolean canTinkersToolHarvestBlock = false;

        NBTTagCompound toolTag = ToolHelper.getToolTag(tool);
        if (toolTag != null) {
            int toolHarvestLevel = Math
                    .max(ToolHelper.getPrimaryHarvestLevel(toolTag), ToolHelper.getSecondaryHarvestLevel(toolTag));
            canTinkersToolHarvestBlock = toolHarvestLevel >= harvestLevel;
        }

        return canTinkersToolHarvestBlock || ForgeHooks.canToolHarvestBlock(block, metadata, tool);
    }

    public static boolean canToolHarvestBlock(ItemStack tool, Block block, int metadata) {
        return block.getMaterial().isToolNotRequired() || tool.func_150998_b(block); // func_150998_b = canHarvestBlock
    }

}
