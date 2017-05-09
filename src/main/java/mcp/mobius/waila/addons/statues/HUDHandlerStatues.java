package mcp.mobius.waila.addons.statues;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.utils.WailaExceptionHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

import static mcp.mobius.waila.api.SpecialChars.BLUE;
import static mcp.mobius.waila.api.SpecialChars.ITALIC;
import static mcp.mobius.waila.api.SpecialChars.WHITE;

public class HUDHandlerStatues implements IWailaDataProvider {

    @Override
    public ItemStack getWailaStack(final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
        return null;
    }

    @Override
    public List<String> getWailaHead(final ItemStack itemStack, List<String> currenttip, final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
        try {
            String skinName = (String) StatuesModule.skinName.get(accessor.getTileEntity());
            if (skinName.equals("")) {
                final TileEntity belowEnt = accessor.getWorld().getTileEntity(new BlockPos(accessor.getPosition().getX(), accessor.getPosition().down().getY(), accessor.getPosition().getZ()));
                if (StatuesModule.TileEntityStatue.isInstance(belowEnt))
                    skinName = (String) StatuesModule.skinName.get(belowEnt);
            }


            if (skinName.equals("")) {
                currenttip.clear();
                currenttip.add(WHITE + "Statue : Unknown");
            } else {
                currenttip.clear();
                currenttip.add(WHITE + "Statue : " + skinName);
            }


        } catch (final Exception e) {
            currenttip = WailaExceptionHandler.handleErr(e, accessor.getTileEntity().getClass().getName(), currenttip);
        }

		/*
		String skinName = accessor.getNBTData().getString("skin");
		if (skinName.equals("")){
			if (accessor.getWorld().getBlockTileEntity(accessor.getPosition().blockX, accessor.getPosition().blockY - 1, accessor.getPosition().blockZ) !=  null){
				
			}
		}
		*/

        return currenttip;
    }

    @Override
    public List<String> getWailaBody(final ItemStack itemStack, final List<String> currenttip, final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
        return currenttip;
    }

    @Override
    public List<String> getWailaTail(final ItemStack itemStack, final List<String> currenttip, final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
        currenttip.add(BLUE + ITALIC + "Statues");
        return currenttip;
    }

    @Override
    public NBTTagCompound getNBTData(final EntityPlayerMP player, final TileEntity te, final NBTTagCompound tag, final World world, final BlockPos pos) {
        if (te != null)
            te.writeToNBT(tag);
        return tag;
    }

}
