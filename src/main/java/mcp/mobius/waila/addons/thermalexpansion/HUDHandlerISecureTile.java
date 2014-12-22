package mcp.mobius.waila.addons.thermalexpansion;

import java.util.List;

import mcp.mobius.waila.cbcore.LangUtil;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import mcp.mobius.waila.api.ITaggedList.ITipList;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;


public class HUDHandlerISecureTile implements IWailaDataProvider {

	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return null;
	}

	@Override
	public ITipList getWailaHead(ItemStack itemStack, ITipList currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return currenttip;
	}

	@Override
	public ITipList getWailaBody(ItemStack itemStack, ITipList currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		
		if (config.getConfig("thermalexpansion.owner")){
		
			String owner  = accessor.getNBTData().getString("Owner");
			int    iaccess = accessor.getNBTInteger(accessor.getNBTData(), "Access");
			
			if (owner.equals("[None]"))
				return currenttip;
			
			String access = "INVALID";
			switch(iaccess){
			case 0:
				access = LangUtil.translateG("hud.msg.public");
				break;
			case 1:
				access = LangUtil.translateG("hud.msg.restricted");
				break;
			case 2:
				access = LangUtil.translateG("hud.msg.private");			
				break;
			}
			
			currenttip.add(String.format("%s : \u00a7f%s\u00a7r [ %s ]", LangUtil.translateG("hud.msg.owner"), owner, access));
		
		}
		
		return currenttip;
	}

	@Override
	public ITipList getWailaTail(ItemStack itemStack, ITipList currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return currenttip;
	}

	@Override
	public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x, int y, int z) {
		try {
			String owner = (String) ThermalExpansionModule.ISecureTile_getOwnerName.invoke(te);
			int    access = ((Enum) ThermalExpansionModule.ISecureTile_getAccess.invoke(te)).ordinal();
			
			tag.setString("Owner", owner);
			tag.setInteger("Access", access);
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return tag;
	}	

}
