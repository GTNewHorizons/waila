package mcp.mobius.waila.addons.statues;

import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import mcp.mobius.waila.api.ITaggedList.ITipList;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.utils.WailaExceptionHandler;
import static mcp.mobius.waila.api.SpecialChars.*;

public class HUDHandlerStatues implements IWailaDataProvider {

	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return null;
	}

	@Override
	public ITipList getWailaHead(ItemStack itemStack, ITipList currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		try {
			String skinName = (String)StatuesModule.skinName.get(accessor.getTileEntity());
			if (skinName.equals("")){
				TileEntity belowEnt = accessor.getWorld().getTileEntity(new BlockPos(accessor.getPosition().getX(), accessor.getPosition().getY() - 1, accessor.getPosition().getY()));
				if (StatuesModule.TileEntityStatue.isInstance(belowEnt))
					skinName = (String)StatuesModule.skinName.get(belowEnt);
			}
			
			
			
			if (skinName.equals("")){
				currenttip.clear();
				currenttip.add(WHITE + "Statue : Unknown");
			} else {
				currenttip.clear();
				currenttip.add(WHITE + "Statue : " + skinName);				
			}
			
			
		} catch (Exception e) {
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
	public ITipList getWailaBody(ItemStack itemStack, ITipList currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return currenttip;
	}

	@Override
	public ITipList getWailaTail(ItemStack itemStack, ITipList currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		currenttip.add(BLUE + ITALIC + "Statues" );
		return currenttip;
	}

	@Override
	public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x, int y, int z) {
		if (te != null)
			te.writeToNBT(tag);
		return tag;
	}	
	
}
