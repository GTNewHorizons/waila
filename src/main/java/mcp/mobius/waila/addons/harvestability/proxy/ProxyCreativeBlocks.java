package mcp.mobius.waila.addons.harvestability.proxy;

import cpw.mods.fml.common.Loader;
import net.minecraft.block.Block;

import java.lang.reflect.Method;

public class ProxyCreativeBlocks {

    private static Class<?> CreativeBlocks = null;
    private static Method isCreativeBlock = null;

    static {
        if (Loader.isModLoaded("CreativeBlocks")) {
            try {
                CreativeBlocks = Class.forName("squeek.creativeblocks.CreativeBlocks");
                isCreativeBlock = CreativeBlocks.getDeclaredMethod("isCreativeBlock", Block.class, int.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isCreativeBlock(Block block, int meta) {
        if (isCreativeBlock != null) {
            try {
                return (Boolean) isCreativeBlock.invoke(null, block, meta);
            } catch (Exception e) {}
        }
        return false;
    }
}
