package mcp.mobius.waila.addons.enderio;

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
import mcp.mobius.waila.api.IWailaDataAccessorServer;
import mcp.mobius.waila.api.IWailaDataProvider;
import static mcp.mobius.waila.api.SpecialChars.*;

public class HUDHandlerTesseract implements IWailaDataProvider {

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
		
		if (accessor.getPlayer().isSneaking()){
		
			String channel = "%s : %s%s%s %s";
			String freq;
			String frequser;
			String owner;
			
			if (config.getConfig("enderio.channel")){
			
				if (accessor.getNBTData().hasKey("channelName")){
					freq = accessor.getNBTData().getString("channelName");
					
					if (accessor.getNBTData().hasKey("channelUser"))
						frequser = "[ " + WHITE + accessor.getNBTData().getString("channelUser") + RESET + " ]";
					else
						frequser = "[ " + WHITE + LangUtil.translateG("hud.msg.public") + RESET +" ]";
						
				}
				else{
					freq     = LangUtil.translateG("hud.msg.none");
					frequser = "";
				}
				
				currenttip.add(String.format(channel, LangUtil.translateG("hud.msg.frequency"), TAB + WHITE, freq, RESET, frequser));
			}
	
			if (config.getConfig("enderio.owner")){
				if (accessor.getNBTData().hasKey("owner"))
					currenttip.add(String.format("%s : %s%s", LangUtil.translateG("hud.msg.owner"), TAB + WHITE, accessor.getNBTData().getString("owner")));
			}
		
		}
		
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
