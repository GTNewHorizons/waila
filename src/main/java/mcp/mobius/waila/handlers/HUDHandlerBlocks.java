package mcp.mobius.waila.handlers;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.overlay.DisplayUtil;
import mcp.mobius.waila.utils.Constants;
import mcp.mobius.waila.utils.ModIdentification;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

import java.util.List;

import static mcp.mobius.waila.api.SpecialChars.BLUE;
import static mcp.mobius.waila.api.SpecialChars.ITALIC;
import static mcp.mobius.waila.api.SpecialChars.RENDER;

public class HUDHandlerBlocks implements IWailaDataProvider {

    @Override
    public ItemStack getWailaStack(final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
        return null;
    }

    @Override
    public List<String> getWailaHead(final ItemStack itemStack, final List<String> currenttip, final IWailaDataAccessor accessor, final IWailaConfigHandler config) {

        String name = null;
        try {
            final String s = DisplayUtil.itemDisplayNameShort(itemStack);
            if (s != null && !s.endsWith("Unnamed"))
                name = s;

            if (name != null)
                currenttip.add(name);
        } catch (final Exception e) {
        }

        if (itemStack.getItem() == Items.REDSTONE) {
            final int md = accessor.getMetadata();
            String s = "" + md;
            if (s.length() < 2)
                s = " " + s;
            currenttip.set(currenttip.size() - 1, name + " " + s);
        }

        if (currenttip.size() == 0)
            currenttip.add("< Unnamed >");
        else {
            if (ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_METADATA, true)) {
                currenttip.add(String.format(ITALIC + "< Unimplemented >"));
                //currenttip.add(String.format(ITALIC + "[%s:%d] | %s",  accessor.getBlock().getStateId(accessor.getBlockState()), accessor.getMetadata()/*, accessor.getBlockQualifiedName()*/));
            }
        }
        return currenttip;
    }

    @Override
    public List<String> getWailaBody(final ItemStack itemStack, final List<String> currenttip, final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
        /*
		if (ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_SHIFTBLOCK, false) && currenttip.size() > 0 && !accessor.getPlayer().isSneaking()){
			currenttip.clear();
			currenttip.add(ITALIC + "Press shift for more data");
			return currenttip;
		}
		*/
        return currenttip;
    }

    @Override
    public List<String> getWailaTail(final ItemStack itemStack, final List<String> currenttip, final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
        currenttip.add(RENDER + "{Plip}" + RENDER + "{Plop,thisisatest,222,333}");

        final String modName = ModIdentification.nameFromStack(itemStack);
        if (modName != null && !modName.equals("")) {
            currenttip.add(BLUE + ITALIC + modName);
        }

        return currenttip;
    }

    @Override
    public NBTTagCompound getNBTData(final EntityPlayerMP player, final TileEntity te, final NBTTagCompound tag, final World world, final BlockPos pos) {
        return tag;
    }
}
