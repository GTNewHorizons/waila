package mcp.mobius.waila.api.elements;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.ProbeMode;

public interface IProbeDataProvider {

    default void addProbeInfo(ProbeMode probeMode, ItemStack itemStack, IProbeInfo probeInfo,
            IWailaDataAccessor accessor, IWailaConfigHandler config) {}

    default NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x,
            int y, int z) {
        if (te != null) te.writeToNBT(tag);
        return tag;
    }
}
