package mcp.mobius.waila.addons.carpenters;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class HUDCarpentersBlocksTE implements IWailaDataProvider {

    @Override
    public ItemStack getWailaStack(final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
        /*
		if (!config.getConfig("carpenters.hide")) return null;

		NBTTagCompound tag  = accessor.getNBTData();
		short         data  = tag.getShort("data");
		short         cover = tag.getShort("cover_6");        
		
		if(CarpentersModule.BlockCarpentersDoor.isInstance(accessor.getBlock())){
			int type   = data & 0x7;
			if (type != 5)  return null;
			if (cover == 0) return null;
			
			return new ItemStack(cover & 0xfff, 0, (cover & 0xf000) >>> 12);
		}
		
		else if (CarpentersModule.BlockCarpentersHatch.isInstance(accessor.getBlock())){
			int type = data & 0x7;
			if (type  != 0) return null;
			if (cover == 0) return null;
			
			return new ItemStack(cover & 0xfff, 0, (cover & 0xf000) >>> 12);			
		}
		*/
        return null;
    }

    @Override
    public List<String> getWailaHead(final ItemStack itemStack, final List<String> currenttip, final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
        return currenttip;
    }

    @Override
    public List<String> getWailaBody(final ItemStack itemStack, final List<String> currenttip, final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
        return currenttip;
    }

    @Override
    public List<String> getWailaTail(final ItemStack itemStack, final List<String> currenttip, final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
        return currenttip;
    }

    @Override
    public NBTTagCompound getNBTData(final EntityPlayerMP player, final TileEntity te, final NBTTagCompound tag, final World world, final BlockPos pos) {
        if (te != null)
            te.writeToNBT(tag);
        return tag;
    }

}
