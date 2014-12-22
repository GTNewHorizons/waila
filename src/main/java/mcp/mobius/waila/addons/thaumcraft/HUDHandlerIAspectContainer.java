package mcp.mobius.waila.addons.thaumcraft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import mcp.mobius.waila.api.ITaggedList.ITipList;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import static mcp.mobius.waila.api.SpecialChars.*;

public class HUDHandlerIAspectContainer implements IWailaDataProvider{

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
		
		NBTTagCompound tag = accessor.getNBTData();
		if (tag.hasKey("Aspects")){
			NBTTagList taglist = tag.getTagList("Aspects", 10);
			
			List<String> unknownAspects = new ArrayList<String>();
			
			for (int i = 0; i < taglist.tagCount(); i++){
				NBTTagCompound subtag = taglist.getCompoundTagAt(i);
				
				String aspect = Character.toUpperCase(subtag.getString("key").charAt(0)) + subtag.getString("key").substring(1); 
				String amount = String.valueOf(subtag.getInteger("value"));
				
				if (!aspect.equals("???"))
					currenttip.add(String.format("%s" + TAB + ALIGNRIGHT + WHITE + "%s",  aspect, amount));
				else
					unknownAspects.add(String.format("%s" + TAB + ALIGNRIGHT + WHITE + "%s",  aspect, amount));
			}
			
			currenttip.addAll(unknownAspects);
		}		
		
		return currenttip;
	}

	@Override
	public ITipList getWailaTail(ItemStack itemStack, ITipList currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return currenttip;
	}

	@Override
	public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag,	World world, int x, int y, int z) {
	
		try{
			tag.setTag("Aspects", new NBTTagList());
			NBTTagList aspects = tag.getTagList("Aspects", 10);			
			
			ItemStack headSlot = player.inventory.armorInventory[3];
			if (headSlot == null) return tag;
			boolean hasReveal =  ThaumcraftModule.IGoggles.isInstance(headSlot.getItem());
			if (!hasReveal) return tag;
			
			HashMap knownAspects        = (HashMap)       ThaumcraftModule.CommonProxy_getKnownAspects.invoke(ThaumcraftModule.Thaumcraft_proxy.get(null));
			LinkedHashMap playerAspects = (LinkedHashMap) ThaumcraftModule.AspectList_aspects.get(knownAspects.get(player.getName()));
			LinkedHashMap tileAspects   = new LinkedHashMap();
			
			if (ThaumcraftModule.IAspectContainer.isInstance(te)){
				tileAspects = (LinkedHashMap) ThaumcraftModule.AspectList_aspects.get(ThaumcraftModule.IAspectContainer_getAspects.invoke(te));
			} else if (ThaumcraftModule.TileAlchemyFurnace.isInstance(te)){
				tileAspects = (LinkedHashMap) ThaumcraftModule.AspectList_aspects.get(ThaumcraftModule.TileAlchemyFurnace_aspects.get(te));
			}
			
			for (Object o : tileAspects.keySet()){
				if (playerAspects.containsKey(o)){
					NBTTagCompound cmptag = new NBTTagCompound();
					cmptag.setString ("key",   (String)ThaumcraftModule.Aspect_tag.get(o));
					cmptag.setInteger("value", (Integer) tileAspects.get(o));
					aspects.appendTag(cmptag);
				} else {
					NBTTagCompound cmptag = new NBTTagCompound();
					cmptag.setString ("key",   "???");
					cmptag.setInteger("value", (Integer) tileAspects.get(o));
					aspects.appendTag(cmptag);					
				}
			}
				
				//for (int i = 0; i < aspects.tagCount(); i++){
				//	System.out.printf("%s : %s\n", aspects.getCompoundTagAt(i).getString("key"), aspects.getCompoundTagAt(i).getString("value"));
				//}
		
			
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		return tag;
	}

}
