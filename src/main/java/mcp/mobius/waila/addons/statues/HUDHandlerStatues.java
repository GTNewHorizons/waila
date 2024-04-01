package mcp.mobius.waila.addons.statues;

import static mcp.mobius.waila.api.SpecialChars.BLUE;
import static mcp.mobius.waila.api.SpecialChars.ITALIC;
import static mcp.mobius.waila.api.SpecialChars.WHITE;

import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.utils.WailaExceptionHandler;

public class HUDHandlerStatues implements IWailaDataProvider {

    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return null;
    }

    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
            IWailaConfigHandler config) {
        try {
            String skinName = (String) StatuesModule.skinName.get(accessor.getTileEntity());
            if (skinName.isEmpty()) {
                TileEntity belowEnt = accessor.getWorld().getTileEntity(
                        accessor.getPosition().blockX,
                        accessor.getPosition().blockY - 1,
                        accessor.getPosition().blockZ);
                if (StatuesModule.TileEntityStatue.isInstance(belowEnt))
                    skinName = (String) StatuesModule.skinName.get(belowEnt);
            }

            if (skinName.isEmpty()) {
                currenttip.clear();
                currenttip.add(WHITE + "Statue : Unknown");
            } else {
                currenttip.clear();
                currenttip.add(WHITE + "Statue : " + skinName);
            }

        } catch (Exception e) {
            currenttip = WailaExceptionHandler.handleErr(e, accessor.getTileEntity().getClass().getName(), currenttip);
        }

        return currenttip;
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
            IWailaConfigHandler config) {
        return currenttip;
    }

    @Override
    public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
            IWailaConfigHandler config) {
        currenttip.add(BLUE + ITALIC + "Statues");
        return currenttip;
    }

    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x,
            int y, int z) {
        if (te != null) te.writeToNBT(tag);
        return tag;
    }

}
