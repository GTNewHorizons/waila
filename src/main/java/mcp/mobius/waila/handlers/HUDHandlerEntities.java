package mcp.mobius.waila.handlers;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry.EntityRegistration;
import mcp.mobius.waila.api.ITaggedList.ITipList;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaEntityAccessor;
import mcp.mobius.waila.api.IWailaEntityAccessorServer;
import mcp.mobius.waila.api.IWailaEntityProvider;
import static mcp.mobius.waila.api.SpecialChars.*;

public class HUDHandlerEntities implements IWailaEntityProvider {

	public static int nhearts = 20;
	public static float maxhpfortext = 40.0f;
	
	@Override
	public Entity getWailaOverride(IWailaEntityAccessor accessor, IWailaConfigHandler config) {
		return null;
	}

	@Override
	public ITipList getWailaHead(Entity entity, ITipList currenttip, IWailaEntityAccessor accessor, IWailaConfigHandler config) {
		try{
			currenttip.add(WHITE + entity.getName());
		} catch (Exception e){
			currenttip.add(WHITE + "Unknown");
		}		
		return currenttip;
	}

	@Override
	public ITipList getWailaBody(Entity entity, ITipList currenttip, IWailaEntityAccessor accessor, IWailaConfigHandler config) {
		if (!config.getConfig("general.showhp")) return currenttip;
		
		if (entity instanceof EntityLivingBase){
			String hptip = "";
			
			nhearts = nhearts <= 0 ? 20 : nhearts;
			
			float  health = ((EntityLivingBase)entity).getHealth() / 2.0f;
			float  maxhp  = ((EntityLivingBase)entity).getMaxHealth() / 2.0f;
			
			if (((EntityLivingBase)entity).getHealth() > maxhpfortext)
				currenttip.add(String.format("HP : " + WHITE + "%.0f" + GRAY + " / " + WHITE + "%.0f", ((EntityLivingBase)entity).getHealth(), ((EntityLivingBase)entity).getMaxHealth()));
			
			else{
				for (int i = 0; i < MathHelper.floor_float(health); i++){
					hptip += HEART;
					if(hptip.length() % (nhearts * HEART.length()) == 0){
						currenttip.add(hptip);
						hptip = "";
					}
				}
				
				if (((EntityLivingBase)entity).getHealth() > MathHelper.floor_float(health) * 2.0f){
					hptip += HHEART;
					if(hptip.length() % (nhearts * HEART.length()) == 0){
						currenttip.add(hptip);
						hptip = "";
					}				
				}
	
				for (int i = 0; i < MathHelper.floor_float(maxhp - health); i++){
					hptip += EHEART;
					if(hptip.length() % (nhearts * HEART.length()) == 0){
						currenttip.add(hptip);
						hptip = "";
					}				
				}
				
				if (!hptip.equals(""))
					currenttip.add(hptip);
			}
		}		

		return currenttip;	
	}

	@Override
	public ITipList getWailaTail(Entity entity, ITipList currenttip, IWailaEntityAccessor accessor, IWailaConfigHandler config) {
		try{
			currenttip.add(BLUE + ITALIC + getEntityMod(entity));
		} catch (Exception e){
			currenttip.add(BLUE + ITALIC + "Unknown");
		}
		return currenttip;
	}

	@Override
	public NBTTagCompound getNBTData(Entity ent, NBTTagCompound tag, IWailaEntityAccessorServer accessor) {
		return tag;
	}	
	
    private static String getEntityMod(Entity entity){
    	String modName = "";
    	try{
    		EntityRegistration er = EntityRegistry.instance().lookupModSpawn(entity.getClass(), true);
    		ModContainer modC = er.getContainer();
    		modName = modC.getName();
    	} catch (NullPointerException e){
    		modName = "Minecraft";	
    	}
    	return modName;
    }	
	
}
