package mcp.mobius.waila.addons.carpenters;

import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import mcp.mobius.waila.api.ITaggedList.ITipList;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataAccessorServer;
import mcp.mobius.waila.api.IWailaDataProvider;

public class HUDCarpentersBlocksTE implements IWailaDataProvider {

	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor,	IWailaConfigHandler config) {
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
	public ITipList getWailaHead(ItemStack itemStack, ITipList currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return currenttip;
	}

	@Override
	public ITipList getWailaBody(ItemStack itemStack, ITipList currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return currenttip;
	}

	@Override
	public ITipList getWailaTail(ItemStack itemStack, ITipList currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return currenttip;
	}

	@Override
	public NBTTagCompound getNBTData(TileEntity te, NBTTagCompound tag, IWailaDataAccessorServer accessor) {
		if (te != null)
			te.writeToNBT(tag);
		return tag;
	}	
	
}
